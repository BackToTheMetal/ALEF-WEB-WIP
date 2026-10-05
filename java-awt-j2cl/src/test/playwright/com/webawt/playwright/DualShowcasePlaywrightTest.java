package com.webawt.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Assume;
import org.junit.Test;

public final class DualShowcasePlaywrightTest {
    @Test
    public void boundsCurveWorkAndImageUploadsAndReleasesRuntimeResources() {
        java.util.List<?> metrics = (java.util.List<?>) page.evaluate("webawtDualShowcasePerformanceDiagnostics()");
        System.out.println("Rendering workload and upload metrics: " + metrics);
        assertTrue("50 software curved fills must finish within 2 seconds", ((Number) metrics.get(0)).doubleValue() < 2000);
        int[] expected = {1, 19, 8, 4, 16384, 0, 0};
        for (int index = 0; index < expected.length; index++) { assertEquals("cache metric " + index, expected[index], ((Number) metrics.get(index + 1)).intValue()); }
        for (int index = 8; index <= 10; index++) { assertTrue("large upload/scaling/layout workload " + index + " must finish within 2 seconds", ((Number) metrics.get(index)).doubleValue() < 2000); }
        assertEquals(2, ((Number) metrics.get(11)).intValue());
        assertEquals(0, ((Number) metrics.get(12)).intValue());
        assertEquals(0, ((Number) metrics.get(13)).intValue());
        page.evaluate("() => { webawtDualShowcaseUnmount(); window.lifecycleListeners = new Set(); for(const target of [document, window]) { const add=target.addEventListener.bind(target), remove=target.removeEventListener.bind(target); target.addEventListener=(type,listener,...rest)=>{ if(type==='visibilitychange'||type==='beforeunload') lifecycleListeners.add(listener); return add(type,listener,...rest); }; target.removeEventListener=(type,listener,...rest)=>{ lifecycleListeners.delete(listener); return remove(type,listener,...rest); }; } webawtDualShowcaseMount('webawt-root'); }");
        for (int cycle = 0; cycle < 3; cycle++) {
            selectPage("Rendering lab");
            button("Load reference font").focus();
            assertEquals(1, ((Number) ((java.util.List<?>) page.evaluate("webawtDualShowcaseResourceDiagnostics()")).get(4)).intValue());
            assertEquals(2, ((Number) page.evaluate("lifecycleListeners.size")).intValue());
            page.evaluate("webawtDualShowcaseQueueThenUnmount()");
            assertEquals(Arrays.asList(0, 0, 0, 0, 0), page.evaluate("webawtDualShowcaseResourceDiagnostics()"));
            assertEquals(0, ((Number) page.evaluate("lifecycleListeners.size")).intValue());
            assertEquals(0, page.locator("#webawt-root").locator(":scope > *").count());
            page.evaluate("webawtDualShowcaseMount('webawt-root')");
            assertTrue((Boolean) page.evaluate("webawtDualShowcaseRunSelfCheck()"));
        }
    }

    @Test
    public void ignoresFontCompletionAfterShowcaseDisposal() {
        page.evaluate("() => { const base=document.createElement('base'); base.href='https://webawt.test/'; document.head.append(base); }");
        java.util.concurrent.atomic.AtomicReference<Route> pending = new java.util.concurrent.atomic.AtomicReference<>();
        page.route("**/fonts/DejaVuSans.ttf", pending::set);
        selectPage("Rendering lab");
        button("Load reference font").click();
        assertThat(page.getByText("Reference font loading...", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertTrue("Font request reached the route", pending.get() != null);
        page.evaluate("webawtDualShowcaseUnmount()");
        pending.get().fulfill(new Route.FulfillOptions().setPath(Paths.get(System.getenv("TEST_SRCDIR"), "_main/samples/dual_showcase/fonts/DejaVuSans.ttf")).setContentType("font/ttf"));
        page.waitForFunction("Array.from(document.fonts).some(face => face.status === 'loaded' && face.family.includes('WebAWT Reference'))");
        assertEquals(0, page.locator("#webawt-root").locator(":scope > *").count());
    }

    @Test
    public void loadsReferenceFontAndMatchesMeasuredWidthsOnBothBackends() throws Exception {
        page.evaluate("() => { const base = document.createElement('base'); base.href = 'https://webawt.test/'; document.head.append(base); }");
        java.util.concurrent.atomic.AtomicInteger requests = new java.util.concurrent.atomic.AtomicInteger();
        page.route("**/fonts/DejaVuSans.ttf", route -> {
            if (requests.incrementAndGet() == 1) {
                route.fulfill(new Route.FulfillOptions().setStatus(500).setBody("transient font failure"));
                return;
            }
            route.fulfill(new Route.FulfillOptions().setPath(Paths.get(System.getenv("TEST_SRCDIR"), "_main/samples/dual_showcase/fonts/DejaVuSans.ttf")).setContentType("font/ttf"));
        });
        page.route("**/fonts/missing.ttf", route -> route.fulfill(new Route.FulfillOptions().setStatus(404).setBody("missing font")));
        selectPage("Rendering lab");
        button("Fail font load").click();
        assertThat(page.getByText("Reference font failed; retry is available", new Page.GetByTextOptions().setExact(true))).isVisible();
        // Warm measurement before loading; the fallback may already be the same system font.
        page.evaluate("webawtDualShowcaseFontMeasurements()");
        button("Load reference font").click();
        assertThat(page.getByText("Reference font failed; retry is available", new Page.GetByTextOptions().setExact(true))).isVisible();
        button("Load reference font").click();
        assertThat(page.getByText("Reference font ready; measurements refreshed", new Page.GetByTextOptions().setExact(true))).isVisible();
        org.webawt.samples.dualshowcase.DesktopFonts.install();
        double[] reference = org.webawt.samples.dualshowcase.FontScenarios.measurements();
        java.util.List<?> actual = (java.util.List<?>) page.evaluate("webawtDualShowcaseFontMeasurements()");
        java.util.List<?> nativeWidths = (java.util.List<?>) page.evaluate("texts => { const ctx = document.createElement('canvas').getContext('2d'); ctx.font = '24px \"WebAWT Reference\"'; return texts.map(text => ctx.measureText(text).width); }", java.util.Arrays.asList(org.webawt.samples.dualshowcase.FontScenarios.TEXT));
        for (int index = 0; index < reference.length; index++) {
            assertEquals("JDK reference metric " + index, reference[index], ((Number) actual.get(index)).doubleValue(), 2.0);
            if (index % 2 == 0) {
                assertEquals("Native browser advance " + index, ((Number) nativeWidths.get(index / 2)).doubleValue(), ((Number) actual.get(index)).doubleValue(), 0.05);
            }
        }
        Locator canvas = page.locator("canvas:visible");
        for (int index = 0; index < 4; index++) {
            int width = ((Number) actual.get(index * 2 + 1)).intValue();
            for (int origin : new int[] {30, 450}) {
                int x = origin + 40;
                int y = 94 + index * 32;
                assertTrue("Font metric controls the visible bar endpoint", (Boolean) canvas.evaluate("(element, p) => { const c=element.getContext('2d'); const a=c.getImageData(p[0]+p[2]-2,p[1],1,1).data; const b=c.getImageData(p[0]+p[2]+2,p[1],1,1).data; return a[0]===0 && a[1]===255 && b[0]===255 && b[1]===0; }", java.util.Arrays.asList(x, y, width)));
            }
        }
        int faces = ((Number) page.evaluate("document.fonts.size")).intValue();
        button("Load reference font").click();
        assertThat(page.getByText("Reference font ready; measurements refreshed", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertEquals(faces, ((Number) page.evaluate("document.fonts.size")).intValue());
        assertEquals(2, requests.get());
        capture("font-metrics.png");
    }

    @Test
    public void comparesPaintedTextWithNativeOracleOnBothBackends() throws Exception {
        double maximumError = 0;
        for (int density : new int[] {1, 2}) {
            if (density == 2) {
                context.close();
                mountShowcase(2);
            }
            page.evaluate("() => { const base = document.createElement('base'); base.href = 'https://webawt.test/'; document.head.append(base); }");
            page.route("**/fonts/DejaVuSans.ttf", route -> route.fulfill(new Route.FulfillOptions().setPath(Paths.get(System.getenv("TEST_SRCDIR"), "_main/samples/dual_showcase/fonts/DejaVuSans.ttf")).setContentType("font/ttf")));
            selectPage("Rendering lab");
            button("Load reference font").click();
            assertThat(page.getByText("Reference font ready; measurements refreshed", new Page.GetByTextOptions().setExact(true))).isVisible();
            for (int offset : new int[] {20, 40}) {
                if (offset == 40) {
                    button("Toggle translation 20 / 40").click();
                }
                for (int scenario = 22; scenario <= 25; scenario++) {
                    page.getByRole(AriaRole.COMBOBOX).nth(2).selectOption(org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES[scenario]);
                    for (int backend = 0; backend < 2; backend++) {
                        Number error = (Number) page.locator("canvas:visible").evaluate(TEXT_ORACLE, java.util.Arrays.asList(scenario - 22, offset, backend, density));
                        maximumError = Math.max(maximumError, error.doubleValue());
                        assertTrue("Native text oracle: scenario=" + scenario + ", backend=" + backend + ", density=" + density + ", error=" + error, error.doubleValue() <= 8.0);
                    }
                    if (offset == 20 && density == 1) {
                        capture("text-" + scenario + ".png");
                    }
                }
            }
        }
        System.out.println("Maximum normalized text pixel error: " + maximumError);
    }

    @Test
    public void paintsAttributedRunsWrappingAndPositionedGlyphsOnBothBackends() throws Exception {
        page.evaluate("() => { const base = document.createElement('base'); base.href = 'https://webawt.test/'; document.head.append(base); }");
        page.route("**/fonts/DejaVuSans.ttf", route -> route.fulfill(new Route.FulfillOptions().setPath(Paths.get(System.getenv("TEST_SRCDIR"), "_main/samples/dual_showcase/fonts/DejaVuSans.ttf")).setContentType("font/ttf")));
        selectPage("Rendering lab");
        button("Load reference font").click();
        assertThat(page.getByText("Reference font ready; measurements refreshed", new Page.GetByTextOptions().setExact(true))).isVisible();
        org.webawt.samples.dualshowcase.DesktopFonts.install();
        double[] reference = org.webawt.samples.dualshowcase.AttributedScenarios.diagnostics();
        java.util.List<?> actual = (java.util.List<?>) page.evaluate("webawtDualShowcaseTextLayoutDiagnostics()");
        for (int index = 0; index < reference.length; index++) {
            assertEquals("Styled layout / caret / wrapping " + index, reference[index], ((Number) actual.get(index)).doubleValue(), index < 4 ? 2.0 : 0.0);
        }
        for (int scenario = 26; scenario <= 29; scenario++) {
            page.getByRole(AriaRole.COMBOBOX).nth(2).selectOption(org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES[scenario]);
            assertThat(page.getByText("Graphics state checks passed:", new Page.GetByTextOptions().setExact(false))).isVisible();
            for (int origin : new int[] {30, 450}) {
                java.util.List<?> ink = (java.util.List<?>) page.locator("canvas:visible").evaluate("(canvas, origin) => { const p=canvas.getContext('2d').getImageData(origin,70,320,180).data; let blue=0,green=0,yellow=0; for(let i=0;i<p.length;i+=4) { if(p[i]<100&&p[i+2]>100)blue++; if(p[i]<100&&p[i+1]>100)green++; if(p[i]>200&&p[i+1]>200&&p[i+2]<50)yellow++; } return [blue,green,yellow]; }", origin);
                assertTrue("Blue text is painted in scenario " + scenario, ((Number) ink.get(0)).intValue() > 40);
                if (scenario < 29) { assertTrue("Green style run is painted", ((Number) ink.get(1)).intValue() > 35); }
                if (scenario == 27 || scenario == 28) { assertTrue("Attribute background/highlight is painted", ((Number) ink.get(2)).intValue() > 100); }
            }
            capture("attributed-" + scenario + ".png");
        }
    }

    private static final String TEXT_ORACLE = """
        (element, args) => {
          const [scenario, offset, backend, density] = args;
          const scale = backend === 0 ? density : 1;
          const source = document.createElement('canvas');
          source.width = 320 * scale; source.height = 180 * scale;
          const g = source.getContext('2d');
          g.scale(scale, scale); g.fillStyle = 'red'; g.fillRect(0, 0, 320, 180);
          g.fillStyle = 'blue'; g.font = '24px "WebAWT Reference"';
          g.textBaseline = 'alphabetic'; g.textAlign = 'left';
          if (scenario === 0) {
            g.fillText('WebAWT canvas', offset + 20, 35);
            g.fillText('Cafe\u0301', offset + 20.5, 75.25);
            g.fillText('AV\u00e9', offset + 20, 115);
            g.fillText('AV\u00e9', offset + 20, 155);
          } else if (scenario === 1) {
            g.save(); g.translate(offset + 25, 65); g.rotate(-Math.PI / 6); g.fillText('Hi', 0, 0); g.restore();
            g.save(); g.translate(offset + 130, 60); g.scale(1.5, .8); g.transform(1, 0, .3, 1, 0, 0); g.fillText('AV', .5, .25); g.restore();
            g.save(); g.translate(offset + 30, 130); g.transform(1.2, .2, -.2, 1.1, 3, -2); g.fillText('Tilt', 0, 0); g.restore();
          } else if (scenario === 2) {
            g.beginPath(); g.rect(offset + 35, 25, 100, 100); g.clip();
            g.translate(offset + 20, 45); g.rotate(Math.PI / 12); g.globalAlpha = .5;
            g.fillText('Clipped text', 0, 0); g.fillText('Second row', .5, 45.25);
          } else {
            g.fillStyle = '#00ff00'; g.fillRect(20, 20, 240, 120);
            g.globalCompositeOperation = 'destination-out'; g.fillText('Clear', offset + 20, 60);
            g.globalCompositeOperation = 'source-over'; g.fillStyle = 'blue'; g.fillText('After', offset + 20, 120);
          }
          const expected = document.createElement('canvas');
          expected.width = 320 * density; expected.height = 180 * density;
          const out = expected.getContext('2d');
          if (backend === 1) { out.fillStyle = 'white'; out.fillRect(0, 0, expected.width, expected.height); }
          out.drawImage(source, 0, 0, expected.width, expected.height);
          const a = element.getContext('2d').getImageData((backend === 0 ? 30 : 450) * density, 70 * density, expected.width, expected.height).data;
          const b = out.getImageData(0, 0, expected.width, expected.height).data;
          let difference = 0, ink = 0;
          for (let i = 0; i < a.length; i += 4) {
            for (let c = 0; c < 4; c++) difference += Math.abs(a[i + c] - b[i + c]);
            if (b[i + 2] > 20 || b[i + 3] < 240) ink++;
          }
          if (ink < 35) throw new Error('Oracle has no glyph ink');
          return difference / (4 * ink);
        }
        """;

    @Test
    public void comparesGraphicsStateOnDirectCanvasAndBufferedImage() {
        selectPage("Rendering lab");
        page.getByRole(AriaRole.COMBOBOX).nth(1).selectOption("Graphics state");
        Locator scenarioChoice = page.getByRole(AriaRole.COMBOBOX).nth(2);
        Locator canvas = page.locator("canvas:visible");
        for (int offset : new int[] {20, 40}) {
            if (offset == 40) {
                button("Toggle translation 20 / 40").click();
            }
            for (int scenario = 0; scenario < org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES.length; scenario++) {
                scenarioChoice.selectOption(org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES[scenario]);
                assertThat(page.getByText("Graphics state checks passed:", new Page.GetByTextOptions().setExact(false))).isVisible();
                assertStatePixels(canvas, scenario, offset, false);
                if (scenario == 36) {
                    for (int origin : new int[] {30, 450}) {
                        Number ink = (Number) canvas.evaluate("(canvas, x) => { const d=canvas.getContext('2d').getImageData(x,188,70,5).data; let n=0; for(let i=2;i<d.length;i+=4) if(d[i]>100)n++; return n; }", origin + offset + 20);
                        assertTrue("Transformed device hairline: " + ink, ink.intValue() >= 60 && ink.intValue() <= 150);
                    }
                }
            }
            button("Toggle cyan / transparent clear").click();
            assertStatePixels(canvas, 2, offset, true);
            button("Toggle cyan / transparent clear").click();
        }
        capture("graphics-state.png");
        scenarioChoice.selectOption("Curves and mutable image reuse");
        capture("graphics-performance.png");
        scenarioChoice.selectOption("Rendered and renderable images");
        capture("graphics-rendered.png");
        scenarioChoice.selectOption("Image overloads");
        capture("graphics-images.png");
        scenarioChoice.selectOption("Scaled clipped copy");
        capture("graphics-copy.png");
        scenarioChoice.selectOption("Caps and joins");
        capture("graphics-strokes.png");
        scenarioChoice.selectOption("Dashed primitives");
        capture("graphics-dashes.png");
        scenarioChoice.selectOption("Texture paint");
        assertStatePixels(canvas, 14, 40, false);
        capture("graphics-texture.png");
        assertStatePixels(canvas, 14, 40, false);
        scenarioChoice.selectOption("Composite rules");
        assertStatePixels(canvas, 17, 40, false);
        capture("graphics-composites.png");
    }

    @Test
    public void comparesTransferAndStrokeScenariosOnHighDensityCanvas() {
        context.close();
        mountShowcase(2);
        selectPage("Rendering lab");
        page.getByRole(AriaRole.COMBOBOX).nth(1).selectOption("Graphics state");
        for (int scenario = 4; scenario < org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES.length; scenario++) {
            page.getByRole(AriaRole.COMBOBOX).nth(2).selectOption(org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES[scenario]);
            // Stroke-edge BufferedImage pixels are filtered when enlarged. Their exact colors
            // are checked at native resolution; high-density stroke geometry uses direct Canvas.
            assertStatePixels(page.locator("canvas:visible"), scenario, 20, false, scenario >= 9 ? 1 : 2);
        }
    }

    private static void assertStatePixels(Locator canvas, int scenario, int offset, boolean transparent) {
        assertStatePixels(canvas, scenario, offset, transparent, 2);
    }

    private static void assertStatePixels(Locator canvas, int scenario, int offset, boolean transparent, int backends) {
        for (int[] pixel : org.webawt.samples.dualshowcase.GraphicsStateScenarios.pixels(scenario, offset, transparent)) {
            for (int backend = 0; backend < backends; backend++) {
                int x = pixel[0] + (backend == 0 ? 30 : 450);
                int y = pixel[1] + 70;
                int expected = backend == 1 && pixel[2] == 0 ? 0xffffffff : pixel[2];
                Number actual = (Number) canvas.evaluate("(element, point) => { const d = Math.max(1, devicePixelRatio); const p = element.getContext('2d').getImageData(Math.floor((point[0] + 0.5) * d), Math.floor((point[1] + 0.5) * d), 1, 1).data; return (p[3] << 24) | (p[0] << 16) | (p[1] << 8) | p[2]; }", java.util.Arrays.asList(x, y));
                assertTrue("scenario=" + scenario + ", backend=" + backend + ", pixel=" + x + "," + y + ": expected=" + Integer.toHexString(expected) + ", actual=" + Integer.toHexString(actual.intValue()), org.webawt.samples.dualshowcase.GraphicsStateScenarios.matchesPixel(expected, actual.intValue(), pixel.length > 3 ? pixel[3] : 0));
            }
        }
    }

    private static final String[] PAGE_NAMES = {"Controls & events", "Layouts", "Graphics2D", "Images & color", "Text & focus", "Menus & scrolling", "DnD & clipboard", "Windows & Desktop", "Runtime & limits", "Keyboard & input", "Rendering lab", "Lifecycle & accessibility"};
    private static Browser browser;
    private static Playwright playwright;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public static void launchBrowser() {
        playwright = Playwright.create();
        String engine = System.getenv("WEBAWT_PLAYWRIGHT_BROWSER");
        BrowserType type = "firefox".equals(engine) ? playwright.firefox() : "webkit".equals(engine) ? playwright.webkit() : playwright.chromium();
        browser = type.launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(10000));
    }

    @AfterClass
    public static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Before
    public void mountShowcase() {
        mountShowcase(1);
    }

    private void mountShowcase(double density) {
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 900).setDeviceScaleFactor(density));
        page = context.newPage();
        page.setDefaultTimeout(5000);
        page.setContent("<!doctype html><html><body><div id=webawt-root></div></body></html>");
        page.addScriptTag(new Page.AddScriptTagOptions().setPath(showcaseScript()));
        page.evaluate("webawtDualShowcaseMount('webawt-root')");
        assertTrue("The dual showcase did not mount: " + page.content(), page.locator("#webawt-root").locator(":scope > *").count() > 0);
    }

    @After
    public void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void navigatesEveryCapabilityPageThroughTheNativeChoice() {
        Locator pageChoice = page.getByRole(AriaRole.COMBOBOX).first();
        assertEquals(PAGE_NAMES.length, pageChoice.locator("option").count());
        for (String pageName : PAGE_NAMES) {
            pageChoice.selectOption(pageName);
            assertEquals(pageName, page.evaluate("webawtDualShowcaseSelectedPage()"));
        }
    }

    @Test
    public void reportsSingleOrderedKeySequencesAndNamedMappings() {
        selectPage("Keyboard & input");
        assertCanvasContainsPaint(page.locator("canvas:visible"));
        Locator input = keyInput();
        clearKeyTrace();
        input.press("a");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('RELEASED target=keyInput')");
        String trace = keyTrace();
        assertEquals(trace, 1, occurrences(trace, "PRESSED target=keyInput"));
        assertEquals(trace, 1, occurrences(trace, "TYPED target=keyInput"));
        assertEquals(trace, 1, occurrences(trace, "RELEASED target=keyInput"));
        assertTrue(trace, trace.indexOf("PRESSED") < trace.indexOf("TYPED"));
        assertTrue(trace, trace.indexOf("TYPED") < trace.indexOf("RELEASED"));
        assertTrue(trace, trace.contains("code=65 key=A char=U+61"));
        String[] keys = {"F1", "F6", "F12", "Delete", "Insert", "Home", "End", "PageUp", "PageDown", "ArrowLeft", "Shift", "Control", "Alt", "Meta", "'", "Shift+Digit1"};
        int[] codes = {112, 117, 123, 127, 155, 36, 35, 33, 34, 37, 16, 17, 18, 157, 222, 49};
        for (int index = 0; index < keys.length; index++) {
            clearKeyTrace();
            input.press(keys[index]);
            String expected = "PRESSED target=keyInput code=" + codes[index] + " ";
            page.waitForFunction("expected => webawtDualShowcaseKeyboardTrace().includes(expected)", expected);
        }
        assertTrue(keyTrace(), keyTrace().contains("char=U+21"));
        capture("keyboard-input.png");
    }

    @Test
    public void preservesNumpadLocationsAltGraphAndUnknownKeys() {
        selectPage("Keyboard & input");
        keyInput().focus();
        keyInput().evaluate("element => element.dispatchEvent(new KeyboardEvent('keydown', {key:'1', code:'Numpad1', location:3, bubbles:true, cancelable:true}))");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('code=97 key=NumPad-1')");
        assertTrue(keyTrace(), keyTrace().contains("location=4"));
        keyInput().evaluate("element => element.dispatchEvent(new KeyboardEvent('keydown', {key:'@', code:'KeyQ', ctrlKey:true, altKey:true, modifierAltGraph:true, bubbles:true, cancelable:true}))");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('char=U+40')");
        assertTrue(keyTrace(), keyTrace().contains("Alt Graph"));
        keyInput().evaluate("element => element.dispatchEvent(new KeyboardEvent('keydown', {key:'Unidentified', bubbles:true}))");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('code=0 key=Undefined char=undefined')");
    }

    @Test
    public void entersRepeatedActionsButDoesNotSubmitOnBlurAndHonorsConsumption() {
        selectPage("Keyboard & input");
        keyInput().fill("submitted");
        keyInput().press("Enter");
        keyInput().press("Enter");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('ACTION Enter count=2')");
        keyInput().fill("edited");
        button("Inspect selection").click();
        assertEquals(2, occurrences(keyTrace(), "ACTION Enter"));
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Consume X and Enter")).check();
        clearKeyTrace();
        keyInput().press("x");
        keyInput().press("Enter");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('RELEASED target=keyInput code=10')");
        assertEquals("edited", keyInput().inputValue());
        assertTrue(keyTrace(), keyTrace().contains("consumed=true"));
        assertFalse(keyTrace(), keyTrace().contains("TYPED"));
        assertFalse(keyTrace(), keyTrace().contains("ACTION Enter"));
    }

    @Test
    public void synchronizesProgrammaticAndNativeSelectionIncludingBackwardCaret() {
        selectPage("Keyboard & input");
        keyInput().fill("abcdef");
        button("Select sample").click();
        assertEquals("0:4:4", page.evaluate("webawtDualShowcaseKeyboardSelection()"));
        assertEquals(Arrays.asList(0, 4), keyInput().evaluate("element => [element.selectionStart, element.selectionEnd]"));
        button("Caret to end").click();
        assertEquals("6:6:6", page.evaluate("webawtDualShowcaseKeyboardSelection()"));
        keyInput().press("Shift+ArrowLeft");
        assertEquals("5:6:5", page.evaluate("webawtDualShowcaseKeyboardSelection()"));
        keyInput().press("ControlOrMeta+a");
        assertEquals("0:6:6", page.evaluate("webawtDualShowcaseKeyboardSelection()"));
        keyInput().press("z");
        assertEquals("z", keyInput().inputValue());
        assertEquals("1:1:1", page.evaluate("webawtDualShowcaseKeyboardSelection()"));
        selectPage("Text & focus");
        button("Select all").click();
        Locator text = page.locator("textarea:visible");
        assertEquals(text.inputValue().length(), ((Number) text.evaluate("element => element.selectionEnd - element.selectionStart")).intValue());
    }

    @Test
    public void observesCompositionWithoutPrematureTypedEventsActionsOrBindings() {
        selectPage("Keyboard & input");
        clearKeyTrace();
        keyInput().focus();
        keyInput().evaluate("element => { element.dispatchEvent(new CompositionEvent('compositionstart', {data:'', bubbles:true})); element.dispatchEvent(new CompositionEvent('compositionupdate', {data:'漢', bubbles:true})); element.dispatchEvent(new KeyboardEvent('keydown', {key:'Enter', code:'Enter', isComposing:true, bubbles:true, cancelable:true})); element.dispatchEvent(new KeyboardEvent('keydown', {key:'K', code:'KeyK', ctrlKey:true, shiftKey:true, isComposing:true, bubbles:true, cancelable:true})); element.dispatchEvent(new CompositionEvent('compositionend', {data:'漢', bubbles:true})); }");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('IME text=漢 committed=1')");
        assertFalse(keyTrace(), keyTrace().contains("TYPED"));
        assertFalse(keyTrace(), keyTrace().contains("ACTION"));
        keyInput().fill("Cafe é 漢字 🙂");
        assertEquals("Cafe é 漢字 🙂", keyInput().inputValue());
        keyInput().press("Enter");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('ACTION Enter count=1')");
    }

    @Test
    public void traversesTargetsAndCanvasAndDispatchesShortcutsOnceAcrossRemounts() {
        selectPage("Keyboard & input");
        keyInput().focus();
        keyInput().press("Tab");
        assertThat(page.locator("input[type=password]:visible")).isFocused();
        page.keyboard().press("Tab");
        assertThat(button("Optional target")).isFocused();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("F6 / Shift+F6 traversal")).check();
        keyInput().press("F6");
        assertThat(page.locator("input[type=password]:visible")).isFocused();
        page.keyboard().press("Shift+F6");
        assertThat(keyInput()).isFocused();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Hide optional")).check();
        keyInput().press("Tab");
        page.keyboard().press("Tab");
        assertThat(page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Hide optional"))).isFocused();
        Locator canvas = page.locator("canvas:visible");
        canvas.click();
        assertThat(canvas).isFocused();
        canvas.press("ArrowRight");
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('PRESSED target=inputCanvas code=39')");
        for (int mount = 0; mount < 2; mount++) {
            if (mount > 0) {
                page.evaluate("webawtDualShowcaseMount('webawt-root')");
                selectPage("Keyboard & input");
            }
            clearKeyTrace();
            keyInput().press("ControlOrMeta+Shift+k");
            keyInput().press("ControlOrMeta+Shift+m");
            page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('ACTION menu shortcut')");
            assertEquals(keyTrace(), 1, occurrences(keyTrace(), "ACTION application binding"));
            assertEquals(keyTrace(), 1, occurrences(keyTrace(), "ACTION menu shortcut"));
        }
    }

    @Test
    public void exposesRenderingModesImageCompletionFailureAndBufferedPresentation() {
        long started = System.nanoTime();
        selectPage("Rendering lab");
        Locator canvas = page.locator("canvas:visible");
        assertCanvasContainsPaint(canvas);
        long elapsedMillis = (System.nanoTime() - started) / 1000000;
        System.out.println("Rendering lab first paint: " + elapsedMillis + " ms");
        assertTrue("Rendering lab blocked the UI for " + elapsedMillis + " ms", elapsedMillis < 2000);
        assertTrue("Area subtraction must preserve both the fill and its hole", (Boolean) canvas.evaluate("element => { const ctx = element.getContext('2d'); const fill = ctx.getImageData(55, 260, 1, 1).data; const hole = ctx.getImageData(140, 280, 1, 1).data; return fill[2] > fill[0] && hole[0] === 255 && hole[1] === 255 && hole[2] === 255; }"));
        canvas.click(new Locator.ClickOptions().setPosition(140, 280));
        assertThat(page.getByText("Area.contains(140, 280) = false", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertTrue((Boolean) canvas.evaluate("element => { const pixel = element.getContext('2d').getImageData(600, 30, 1, 1).data; return pixel[0] === 255 && pixel[1] === 255 && pixel[2] === 255; }"));
        capture("paint-geometry.png");
        assertTrue("TexturePaint must repeat colored and white tiles", (Boolean) canvas.evaluate("element => { const context = element.getContext('2d'); const pixels = context.getImageData(330, 55, 80, 60).data; let white = 0; let green = 0; for (let i = 0; i < pixels.length; i += 4) { if (pixels[i] > 235 && pixels[i + 1] > 235) white++; if (pixels[i] < 60 && pixels[i + 1] > 120 && pixels[i + 1] < 185) green++; } const a = context.getImageData(340, 70, 1, 1).data; const b = context.getImageData(360, 70, 1, 1).data; return white > 100 && green > 500 && a.every((value, i) => Math.abs(value - b[i]) < 4); }"));
        page.getByRole(AriaRole.COMBOBOX).nth(1).selectOption("Text layout");
        assertCanvasContainsPaint(canvas);
        assertTrue("TextLayout must paint glyphs, not filled metric rectangles", (Boolean) canvas.evaluate("element => { const pixels = element.getContext('2d').getImageData(35, 30, 320, 40).data; let dark = 0; for (let i = 0; i < pixels.length; i += 4) { if (pixels[i] < 80 && pixels[i + 1] < 80 && pixels[i + 2] < 80) dark++; } return dark > 50 && dark < 4000; }"));
        button("Toggle wrap width").click();
        assertThat(page.getByText("Wrap width: 230", new Page.GetByTextOptions().setExact(true))).isVisible();
        capture("text-layout.png");
        button("Decode PNG").click();
        assertThat(page.getByText("Image ready: 1 x 1", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertTrue((Boolean) canvas.evaluate("element => { const pixel = element.getContext('2d').getImageData(540, 100, 1, 1).data; return pixel[0] > 240 && pixel[1] < 10 && pixel[2] < 10; }"));
        button("Decode invalid image").click();
        assertThat(page.getByText("Image failed: decoder rejected the payload", new Page.GetByTextOptions().setExact(true))).isVisible();
        button("Decode PNG").click();
        assertThat(page.getByText("Image ready: 1 x 1", new Page.GetByTextOptions().setExact(true))).isVisible();
        button("Draw buffered frame").click();
        assertThat(page.getByText("BufferStrategy presented;", new Page.GetByTextOptions().setExact(false))).isVisible();
        capture("rendering-images.png");
        selectPage("Controls & events");
        selectPage("Rendering lab");
        page.getByRole(AriaRole.COMBOBOX).nth(1).selectOption("Paint & geometry");
        assertCanvasContainsPaint(canvas);
    }

    @Test
    public void decoratesAndClosesOwnedDialogsAtTheirAwtBounds() {
        page.addStyleTag(new Page.AddStyleTagOptions().setContent("body { margin: 0; padding: 16px; background: #e9eef5; } #webawt-root { min-width: 1080px; min-height: 720px; }"));
        selectPage("Windows & Desktop");
        button("Open Dialog").click();
        Locator dialog = page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName("AWT Dialog").setExact(true));
        assertThat(dialog).isVisible();
        BoundingBox bounds = dialog.boundingBox();
        BoundingBox owner = page.locator("#webawt-root > section").first().boundingBox();
        assertEquals(460, bounds.width, 0.1);
        assertEquals(170, bounds.height, 0.1);
        assertEquals(owner.x + 120, bounds.x, 0.1);
        assertEquals(owner.y + 120, bounds.y, 0.1);
        Locator title = dialog.locator("[data-webawt-dialog-title]");
        assertThat(title.getByText("AWT Dialog", new Locator.GetByTextOptions().setExact(true))).isVisible();
        BoundingBox content = dialog.getByText("This is the same owned java.awt.Dialog on both runtimes.", new Locator.GetByTextOptions().setExact(true)).boundingBox();
        assertTrue(content.y >= title.boundingBox().y + title.boundingBox().height);
        assertTrue(button("Close").boundingBox().y + button("Close").boundingBox().height <= bounds.y + bounds.height - 3);
        capture("dialog.png");
        button("Close AWT Dialog").click();
        assertEquals(0, page.locator("dialog").count());
        button("Open Dialog").click();
        button("Close").click();
        assertEquals(0, page.locator("dialog").count());
    }

    @Test
    public void exercisesDynamicLayoutAccessibleActionsAndWindowLifecycle() {
        selectPage("Lifecycle & accessibility");
        button("Add component").click();
        assertThat(button("Dynamic 3")).isVisible();
        button("Remove component").click();
        assertEquals(0, button("Dynamic 3").count());
        button("Lifecycle action").press("Tab");
        assertThat(button("Dynamic 1")).isFocused();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Reverse focus order")).check();
        button("Lifecycle action").press("Tab");
        assertThat(button("Dynamic 2")).isFocused();
        button("Toggle container width").click();
        assertTrue(page.locator("textarea:visible").inputValue().contains("RESIZED 320 x 170"));
        button("Inspect accessibility").click();
        assertThat(page.getByText("Name=Lifecycle action", new Page.GetByTextOptions().setExact(false))).isVisible();
        button("Invoke accessible action").click();
        assertTrue(page.locator("textarea:visible").inputValue().contains("Accessible action invoked=true"));
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Sample enabled")).uncheck();
        assertThat(button("Lifecycle action")).isDisabled();
        button("Invoke accessible action").click();
        assertTrue(page.locator("textarea:visible").inputValue().contains("Accessible action invoked=false"));
        button("Open lifecycle dialog").click();
        button("Close lifecycle dialog").click();
        assertTrue(page.locator("textarea:visible").inputValue().contains("WINDOW closed"));
        capture("lifecycle-accessibility.png");
    }

    @Test
    public void reportsPointerCoordinatesDragClicksAndConsumedWheelOnTheInputCanvas() {
        selectPage("Keyboard & input");
        Locator canvas = page.locator("canvas:visible");
        BoundingBox bounds = canvas.boundingBox();
        page.mouse().move(bounds.x + 45, bounds.y + 60);
        page.mouse().down();
        page.mouse().move(bounds.x + 95, bounds.y + 80);
        page.mouse().up();
        canvas.dblclick(new Locator.DblclickOptions().setPosition(45, 60));
        page.mouse().wheel(0, 100);
        page.waitForFunction("webawtDualShowcaseKeyboardTrace().includes('WHEEL rotation=')");
        String trace = keyTrace();
        assertTrue(trace, trace.contains("MOUSE pressed x=45 y=60 button=1"));
        assertTrue(trace, trace.contains("MOUSE dragged x=95 y=80"));
        assertTrue(trace, trace.contains("clicks=2"));
        assertTrue(trace, trace.contains("consumed=true"));
    }

    private Locator keyInput() {
        return page.locator("input[type=text]:visible").first();
    }

    private Locator button(String name) {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name).setExact(true));
    }

    private void clearKeyTrace() {
        button("Clear input log").click();
        keyInput().focus();
    }

    private String keyTrace() {
        return (String) page.evaluate("webawtDualShowcaseKeyboardTrace()");
    }

    private void capture(String fileName) {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(System.getenv("TEST_UNDECLARED_OUTPUTS_DIR"), fileName)).setFullPage(true));
    }

    private static int occurrences(String text, String value) {
        return text.split(java.util.regex.Pattern.quote(value), -1).length - 1;
    }

    @Test
    public void exercisesControlsCanvasImagesAndPortableSelfChecks() {
        selectPage("Controls & events");
        page.getByRole(AriaRole.COMBOBOX).nth(1).selectOption("Sunset");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply controls").setExact(true)).click();
        assertEquals("Button action for Ada", page.evaluate("webawtDualShowcaseStatusText()"));
        selectPage("Graphics2D");
        assertCanvasContainsPaint(page.locator("canvas:visible"));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Rotate motif").setExact(true)).click();
        assertCanvasContainsPaint(page.locator("canvas:visible"));
        selectPage("Images & color");
        assertCanvasContainsPaint(page.locator("canvas:visible"));
        assertTrue((Boolean) page.evaluate("webawtDualShowcaseRunSelfCheck()"));
        assertTrue(((String) page.evaluate("webawtDualShowcaseStatusText()")).startsWith("Self-check passed"));
    }

    @Test
    public void rendersCompactCheckboxesWithClickableCaptionsAndExclusiveRadios() {
        Locator checkbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enabled").setExact(true));
        Locator caption = page.getByText("Enabled", new Page.GetByTextOptions().setExact(true));
        assertTrue(checkbox.isChecked());
        BoundingBox indicator = checkbox.boundingBox();
        BoundingBox text = caption.boundingBox();
        assertTrue(indicator.width >= 10 && indicator.width <= 24);
        assertTrue(indicator.height >= 10 && indicator.height <= 24);
        assertTrue(text.x >= indicator.x + indicator.width);
        assertEquals(indicator.y + indicator.height / 2, text.y + text.height / 2, 2.0);
        caption.click();
        assertFalse(checkbox.isChecked());
        assertEquals(1, countEvent("Checkbox: false"));
        checkbox.focus();
        page.keyboard().press("Space");
        assertTrue(checkbox.isChecked());
        assertEquals(1, countEvent("Checkbox: true"));
        Locator compact = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Compact").setExact(true));
        Locator comfortable = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Comfortable").setExact(true));
        assertTrue(compact.isChecked());
        assertFalse(comfortable.isChecked());
        page.getByText("Comfortable", new Page.GetByTextOptions().setExact(true)).click();
        assertFalse(compact.isChecked());
        assertTrue(comfortable.isChecked());
        assertEquals(1, countEvent("Radio: Comfortable"));
        comfortable.click();
        assertTrue(comfortable.isChecked());
        assertEquals(1, countEvent("Radio: Comfortable"));
        comfortable.focus();
        page.keyboard().press("ArrowLeft");
        assertTrue(compact.isChecked());
        assertFalse(comfortable.isChecked());
        assertEquals(1, countEvent("Radio: Compact"));
    }

    @Test
    public void separatesTheMenuFromTheHeaderAndCentersFormLabels() {
        Locator bar = page.getByRole(AriaRole.MENUBAR);
        BoundingBox menu = bar.boundingBox();
        BoundingBox heading = page.getByText("WebAWT capability gallery - WebAWT / J2CL / Elemental2", new Page.GetByTextOptions().setExact(true)).boundingBox();
        assertTrue("Menu overlaps heading", heading.y >= menu.y + menu.height);
        BoundingBox help = bar.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Help").setExact(true)).boundingBox();
        assertEquals(menu.x + menu.width - 2, help.x + help.width, 2.0);
        BoundingBox label = page.getByText("TextField", new Page.GetByTextOptions().setExact(true)).boundingBox();
        BoundingBox field = page.locator("input[type=text]").first().boundingBox();
        assertEquals(field.y + field.height / 2, label.y + label.height / 2, 3.0);
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(System.getenv("TEST_UNDECLARED_OUTPUTS_DIR"), "controls.png")).setFullPage(true));
        bar.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Help").setExact(true)).click();
        Locator limits = bar.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Runtime & limits").setExact(true));
        BoundingBox item = limits.boundingBox();
        assertTrue(item.x + item.width <= menu.x + menu.width + 1);
        limits.click();
        assertEquals("Runtime & limits", page.evaluate("webawtDualShowcaseSelectedPage()"));
    }

    @Test
    public void remountsCaptionPeersWithoutDuplicatingControlsOrEvents() {
        for (int cycle = 0; cycle < 3; cycle++) {
            page.evaluate("webawtDualShowcaseUnmount()");
            assertEquals(0, page.locator("[role=menubar]").count());
            assertEquals(0, page.locator("label input").count());
            page.evaluate("webawtDualShowcaseMount('webawt-root')");
            assertEquals(1, page.getByRole(AriaRole.MENUBAR).count());
            assertEquals(2, page.getByRole(AriaRole.RADIO).count());
            page.getByText("Enabled", new Page.GetByTextOptions().setExact(true)).click();
            assertEquals(1, countEvent("Checkbox: false"));
        }
    }

    private int countEvent(String message) {
        String log = page.locator("textarea").first().inputValue();
        int count = 0;
        for (String line : log.split("\n")) {
            if (line.endsWith(message)) {
                count++;
            }
        }
        return count;
    }

    @Test
    public void exercisesDragAndDropMenusScrollingAndDialogs() {
        selectPage("DnD & clipboard");
        Locator source = page.getByText("Drag this WebAWT payload", new Page.GetByTextOptions().setExact(true));
        Locator target = page.getByText("Drop payload here", new Page.GetByTextOptions().setExact(true));
        source.dragTo(target);
        page.getByText("Dropped: WebAWT dual-runtime payload", new Page.GetByTextOptions().setExact(true)).waitFor();
        selectPage("Menus & scrolling");
        popupButton().click();
        Locator popup = page.locator("div[id^=webawt-popup-menu-]:visible");
        popup.waitFor();
        assertEquals(1, popup.count());
        popup.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Record popup action").setExact(true)).click();
        assertEquals("Popup menu action", page.evaluate("webawtDualShowcaseStatusText()"));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Scroll to 640, 420").setExact(true)).click();
        assertTrue(((String) page.evaluate("webawtDualShowcaseStatusText()")).startsWith("Scroll position:"));
        selectPage("Windows & Desktop");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Dialog").setExact(true)).click();
        Locator dialog = page.locator("dialog").last();
        assertTrue("Dialog markup: " + dialog.evaluate("element => element.outerHTML"), (Boolean) dialog.evaluate("element => element.hasAttribute('open')"));
        page.getByText("This is the same owned java.awt.Dialog on both runtimes.", new Page.GetByTextOptions().setExact(true)).waitFor();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close").setExact(true)).click();
    }

    @Test
    public void opensPopupFromItsButtonAndPreservesOptionsAndNestedActions() {
        selectPage("Menus & scrolling");
        popupButton().click();
        Locator popup = page.locator("div[id^=webawt-popup-menu-]");
        assertTrue(popup.isVisible());
        BoundingBox anchor = popupButton().boundingBox();
        BoundingBox menu = popup.boundingBox();
        assertEquals(anchor.x + 12, menu.x, 1.0);
        assertEquals(Math.min(anchor.y + 24, 900 - menu.height), menu.y, 1.0);
        popup.getByRole(AriaRole.MENUITEMCHECKBOX, new Locator.GetByRoleOptions().setName("Persistent option")).click();
        assertFalse(popup.isVisible());
        assertEquals(1, countEvent("Popup option: true"));
        popupButton().click();
        assertEquals("true", popup.getByRole(AriaRole.MENUITEMCHECKBOX).getAttribute("aria-checked"));
        popup.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Nested actions").setExact(true)).click();
        Locator nested = popup.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Nested item").setExact(true));
        assertTrue(nested.isVisible());
        assertTrue(nested.boundingBox().x >= menu.x + menu.width - 3);
        nested.click();
        assertEquals(1, countEvent("Nested menu action"));
        assertFalse(popup.isVisible());
        popupButton().press("Enter");
        assertTrue(popup.isVisible());
        page.keyboard().press("Escape");
        assertFalse(popup.isVisible());
        popupButton().press("Space");
        assertTrue(popup.isVisible());
        page.keyboard().press("Enter");
        assertEquals(1, countEvent("Popup menu action"));
        popupButton().click();
        page.getByText("Scrollable tile 1", new Page.GetByTextOptions().setExact(true)).click();
        assertFalse(popup.isVisible());
        popupButton().click();
        page.keyboard().press("Tab");
        assertFalse(popup.isVisible());
    }

    @Test
    public void keepsPopupAndSubmenuInsideTheViewportAndDisposesThem() {
        page.setViewportSize(1140, 780);
        for (int cycle = 0; cycle < 2; cycle++) {
            selectPage("Menus & scrolling");
            popupButton().click();
            Locator popup = page.locator("div[id^=webawt-popup-menu-]");
            assertInsideViewport(popup);
            popup.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Nested actions").setExact(true)).click();
            Locator nested = popup.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Nested item").setExact(true));
            assertInsideViewport(nested);
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(System.getenv("TEST_UNDECLARED_OUTPUTS_DIR"), "menus.png")));
            nested.click();
            assertEquals(1, countEvent("Nested menu action"));
            page.evaluate("webawtDualShowcaseUnmount()");
            assertEquals(0, popup.count());
            page.evaluate("webawtDualShowcaseMount('webawt-root')");
        }
    }

    @Test
    public void preservesTileBoundsSpacingAndCenteredCaptionsAtBothDisplayScales() {
        for (double scale : new double[] {1.0, 1.25}) {
            context.close();
            context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 900).setDeviceScaleFactor(scale));
            page = context.newPage();
            page.setContent("<!doctype html><html><body><div id=webawt-root></div></body></html>");
            page.addScriptTag(new Page.AddScriptTagOptions().setPath(showcaseScript()));
            page.evaluate("webawtDualShowcaseMount('webawt-root')");
            selectPage("Menus & scrolling");
            for (int index = 0; index < 18; index++) {
                Locator caption = page.getByText("Scrollable tile " + (index + 1), new Page.GetByTextOptions().setExact(true));
                BoundingBox text = caption.boundingBox();
                BoundingBox tile = caption.locator("..").boundingBox();
                assertEquals(160.0, tile.width, 0.1);
                assertEquals(170.0, tile.height, 0.1);
                assertEquals(tile.x + 80, text.x + text.width / 2, 1.0);
                assertEquals(tile.y + 85, text.y + text.height / 2, 1.0);
                assertEquals((30 + index % 6 * 185) + "px", caption.locator("..").evaluate("element => element.style.left"));
                assertEquals((30 + index / 6 * 220) + "px", caption.locator("..").evaluate("element => element.style.top"));
            }
        }
    }

    private Locator popupButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Show popup menu").setExact(true));
    }

    @Test
    public void waitsForClipboardCompletionAndReadsFreshExternalText() {
        selectPage("DnD & clipboard");
        page.evaluate("() => { window.clipboardWrites = []; Object.defineProperty(navigator, 'clipboard', {configurable: true, value: {writeText: text => { window.clipboardWrites.push(text); return new Promise(resolve => window.finishClipboardWrite = resolve); }, readText: () => new Promise(resolve => window.finishClipboardRead = resolve)}}); }");
        clipboardField().fill("Copy from the gallery: café 😀");
        clipboardButton(false).click();
        assertEquals(Collections.singletonList("Copy from the gallery: café 😀"), page.evaluate("window.clipboardWrites"));
        assertClipboardPending("Writing system clipboard...");
        page.evaluate("window.finishClipboardWrite()");
        awaitStatus("Wrote system clipboard");
        assertFalse(clipboardButton(true).isDisabled());
        clipboardField().fill("Keep this until the read completes");
        clipboardButton(true).click();
        assertClipboardPending("Reading system clipboard...");
        assertEquals("Keep this until the read completes", clipboardField().inputValue());
        page.evaluate("window.finishClipboardRead('Fresh text from another application')");
        awaitStatus("Read system clipboard");
        assertEquals("Fresh text from another application", clipboardField().inputValue());
        clipboardButton(true).click();
        page.evaluate("window.finishClipboardRead('')");
        awaitStatus("Read system clipboard");
        assertEquals("", clipboardField().inputValue());
        assertTrue(clipboardField().isEditable());
        assertFalse(clipboardButton(false).isDisabled());
    }

    @Test
    public void reportsDeniedClipboardAccessWithoutClearingTextAndAllowsRetry() {
        selectPage("DnD & clipboard");
        page.evaluate("() => { window.clipboardDenied = true; Object.defineProperty(navigator, 'clipboard', {configurable: true, value: {writeText: text => window.clipboardDenied ? Promise.reject(new DOMException('Permission denied', 'NotAllowedError')) : Promise.resolve(), readText: () => window.clipboardDenied ? Promise.reject(new DOMException('Permission denied', 'NotAllowedError')) : Promise.resolve('Retry succeeded')}}); }");
        clipboardField().fill("Preserve this text");
        for (boolean reading : new boolean[] {false, true}) {
            clipboardButton(reading).click();
            page.waitForFunction("() => webawtDualShowcaseStatusText().includes('NotAllowedError')");
            String status = (String) page.evaluate("webawtDualShowcaseStatusText()");
            assertTrue(status, status.startsWith("Clipboard " + (reading ? "read" : "write") + " failed:"));
            assertEquals("Preserve this text", clipboardField().inputValue());
            assertTrue(clipboardField().isEditable());
            assertFalse(clipboardButton(false).isDisabled());
            assertFalse(clipboardButton(true).isDisabled());
        }
        page.evaluate("window.clipboardDenied = false");
        clipboardButton(true).click();
        awaitStatus("Read system clipboard");
        assertEquals("Retry succeeded", clipboardField().inputValue());
        clipboardButton(false).click();
        awaitStatus("Wrote system clipboard");
    }

    @Test
    public void reportsUnavailableClipboardWithoutPretendingToCopyLocally() {
        selectPage("DnD & clipboard");
        page.evaluate("Object.defineProperty(navigator, 'clipboard', {configurable: true, value: undefined})");
        clipboardField().fill("Preserve this text");
        for (boolean reading : new boolean[] {false, true}) {
            clipboardButton(reading).click();
            awaitStatus("Clipboard " + (reading ? "read" : "write") + " failed: System clipboard unavailable. Open this page over HTTPS or localhost.");
            assertEquals("Preserve this text", clipboardField().inputValue());
            assertFalse(clipboardButton(false).isDisabled());
            assertFalse(clipboardButton(true).isDisabled());
        }
    }

    @Test
    public void ignoresClipboardCompletionAfterUnmount() {
        selectPage("DnD & clipboard");
        page.evaluate("Object.defineProperty(navigator, 'clipboard', {configurable: true, value: {readText: () => new Promise(resolve => window.finishOldClipboardRead = resolve)}})");
        clipboardButton(true).click();
        page.evaluate("webawtDualShowcaseUnmount()");
        page.evaluate("webawtDualShowcaseMount('webawt-root')");
        selectPage("DnD & clipboard");
        String status = (String) page.evaluate("webawtDualShowcaseStatusText()");
        page.evaluate("() => { window.finishOldClipboardRead('Stale clipboard data'); return Promise.resolve(); }");
        assertEquals("Shared system clipboard text", clipboardField().inputValue());
        assertEquals(status, page.evaluate("webawtDualShowcaseStatusText()"));
    }

    @Test
    public void writesTheRealSystemClipboardFromAUserClick() {
        mountSecureClipboardPage();
        if ("chromium".equals(System.getenv("WEBAWT_PLAYWRIGHT_BROWSER"))) {
            context.grantPermissions(Collections.singletonList("clipboard-write"));
        }
        clipboardField().fill("Real clipboard from WebAWT: café 😀");
        clipboardButton(false).click();
        awaitStatus("Wrote system clipboard");
        clipboardField().fill("");
        clipboardField().press("Control+V");
        assertThat(clipboardField()).hasValue("Real clipboard from WebAWT: café 😀");
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(System.getenv("TEST_UNDECLARED_OUTPUTS_DIR"), "clipboard.png")));
    }

    @Test
    public void readsRealExternalClipboardTextInChromium() {
        Assume.assumeTrue("Chromium exposes clipboard permission grants for this external-writer test", "chromium".equals(System.getenv("WEBAWT_PLAYWRIGHT_BROWSER")));
        mountSecureClipboardPage();
        context.grantPermissions(Arrays.asList("clipboard-read", "clipboard-write"));
        clipboardField().fill("Unrelated field text");
        page.evaluate("navigator.clipboard.writeText('External clipboard text')");
        clipboardButton(true).click();
        awaitStatus("Read system clipboard");
        assertEquals("External clipboard text", clipboardField().inputValue());
        clipboardField().fill("Written by the button");
        clipboardButton(false).click();
        awaitStatus("Wrote system clipboard");
        assertEquals("Written by the button", page.evaluate("navigator.clipboard.readText()"));
    }

    private void mountSecureClipboardPage() {
        page.route("http://localhost/clipboard-test", route -> route.fulfill(new Route.FulfillOptions().setContentType("text/html").setBody("<!doctype html><html><body><div id=webawt-root></div></body></html>")));
        page.navigate("http://localhost/clipboard-test");
        page.addScriptTag(new Page.AddScriptTagOptions().setPath(showcaseScript()));
        page.evaluate("webawtDualShowcaseMount('webawt-root')");
        assertEquals(true, page.evaluate("window.isSecureContext && !!navigator.clipboard"));
        selectPage("DnD & clipboard");
    }

    private Locator clipboardField() {
        return page.locator("input[type=text]:visible");
    }

    private Locator clipboardButton(boolean reading) {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName((reading ? "Read" : "Write") + " system clipboard").setExact(true));
    }

    private void awaitStatus(String status) {
        page.waitForFunction("status => webawtDualShowcaseStatusText() === status", status);
    }

    private void assertClipboardPending(String status) {
        page.getByText(status, new Page.GetByTextOptions().setExact(true)).waitFor();
        assertTrue(clipboardButton(false).isDisabled());
        assertTrue(clipboardButton(true).isDisabled());
        assertFalse(clipboardField().isEditable());
    }

    private void assertInsideViewport(Locator locator) {
        assertTrue(locator.isVisible());
        BoundingBox bounds = locator.boundingBox();
        assertTrue(bounds.x >= 0 && bounds.y >= 0);
        assertTrue(bounds.x + bounds.width <= page.viewportSize().width + 1);
        assertTrue(bounds.y + bounds.height <= page.viewportSize().height + 1);
    }

    private static Path showcaseScript() {
        String testSourceDirectory = System.getenv("TEST_SRCDIR");
        String testWorkspace = System.getenv("TEST_WORKSPACE");
        if (testSourceDirectory == null || testWorkspace == null) {
            throw new IllegalStateException("The test must run through Bazel so its J2CL bundle is available as a runfile");
        }
        return Paths.get(testSourceDirectory, testWorkspace, "samples/dual_showcase/browser_application.js");
    }

    private void selectPage(String pageName) {
        page.evaluate("pageName => webawtDualShowcaseSelectPage(pageName)", pageName);
        assertEquals(pageName, page.evaluate("webawtDualShowcaseSelectedPage()"));
    }

    private static void assertCanvasContainsPaint(Locator canvas) {
        canvas.waitFor();
        assertEquals(1, canvas.count());
        assertTrue((Boolean) canvas.evaluate("element => { const context = element.getContext('2d'); const pixels = context.getImageData(0, 0, element.width, element.height).data; for (let index = 3; index < pixels.length; index += 4) { if (pixels[index] !== 0) return true; } return false; }"));
    }
}
