package com.webawt.playwright;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public final class WebAwtPlaywrightTest {
    private static Browser browser;
    private static Playwright playwright;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public static void launchBrowser() {
        playwright = Playwright.create();
        browser = selectBrowser(playwright, System.getenv("WEBAWT_PLAYWRIGHT_BROWSER")).launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(10000));
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
        openShowcase(1.0);
    }

    @After
    public void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void updatesAwtStateThroughNativeSelectionAndPointerInput() {
        Locator select = page.locator("select");
        assertEquals(2, select.locator("option").count());
        select.selectOption("Red");
        page.getByRole(AriaRole.BUTTON).click();
        assertEquals("Red", page.locator("#webawt-root span").first().textContent());
    }

    @Test
    public void dispatchesKeyboardActivationThroughTheBrowser() {
        page.locator("select").selectOption("Red");
        Locator button = page.getByRole(AriaRole.BUTTON);
        button.focus();
        page.keyboard().press("Enter");
        assertEquals("Red", page.locator("#webawt-root span").first().textContent());
    }

    @Test
    public void paintsAwtGraphicsIntoARealCanvas() {
        Locator canvas = page.locator("canvas");
        int canvasWidth = Integer.parseInt(canvas.getAttribute("width"));
        assertTrue(canvasWidth > 0);
        List<?> pixel = (List<?>) canvas.evaluate("canvas => Array.from(canvas.getContext('2d').getImageData(30, 30, 1, 1).data)");
        assertTrue(((Number) pixel.get(2)).intValue() > 200);
        assertTrue(((Number) pixel.get(3)).intValue() > 200);
    }

    @Test
    public void preservesNativeRolesAndFocusOrder() {
        Locator choice = page.getByRole(AriaRole.COMBOBOX);
        Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update"));
        choice.focus();
        assertEquals("colors", page.evaluate("webawtShowcaseFocusOwnerName()"));
        page.keyboard().press("Tab");
        assertTrue((Boolean) button.evaluate("element => document.activeElement === element"));
        assertEquals("update", page.evaluate("webawtShowcaseFocusOwnerName()"));
    }

    @Test
    public void reportsTheLastNativePointerPositionInsideAComponent() {
        Locator choice = page.getByRole(AriaRole.COMBOBOX);
        choice.evaluate("element => { const bounds = element.getBoundingClientRect(); element.dispatchEvent(new MouseEvent('mouseenter', {clientX: bounds.left + 5, clientY: bounds.top + 6})); }");
        assertTrue(((Number) page.evaluate("webawtShowcaseColorChoiceMouseX()")).intValue() >= 0);
        assertTrue(((Number) page.evaluate("webawtShowcaseColorChoiceMouseY()")).intValue() >= 0);
    }

    @Test
    public void exposesSemanticMenusAndDispatchesTheirActions() {
        Locator menuBar = page.locator("[role=menubar]");
        assertEquals(1, menuBar.count());
        Locator file = menuBar.locator("button:has-text('File')").first();
        file.click();
        assertEquals("true", file.getAttribute("aria-expanded"));
        menuBar.locator("button:has-text('Menu action')").click();
        assertEquals(1, ((Number) page.evaluate("webawtShowcaseMenuActionCount()")).intValue());
        page.evaluate("webawtShowcaseShowPopupMenu()");
        Locator popup = page.locator("div[id^=webawt-popup-menu-]");
        assertTrue(popup.isVisible());
        popup.locator("button:has-text('Popup action')").click();
        assertEquals(2, ((Number) page.evaluate("webawtShowcaseMenuActionCount()")).intValue());
        assertFalse(popup.isVisible());
    }

    @Test
    public void supportsKeyboardNavigationAndCheckboxMenuState() {
        page.evaluate("webawtShowcaseShowPopupMenu()");
        Locator popup = page.locator("div[id^=webawt-popup-menu-]");
        Locator checkbox = popup.locator("[role=menuitemcheckbox]");
        assertEquals("false", checkbox.getAttribute("aria-checked"));
        page.keyboard().press("ArrowDown");
        assertTrue((Boolean) checkbox.evaluate("element => document.activeElement === element"));
        page.keyboard().press("Enter");
        assertTrue((Boolean) page.evaluate("webawtShowcaseIsPopupOptionSelected()"));
        assertFalse(popup.isVisible());
        page.evaluate("webawtShowcaseShowPopupMenu()");
        assertEquals("true", popup.locator("[role=menuitemcheckbox]").getAttribute("aria-checked"));
        page.keyboard().press("Escape");
        assertFalse(popup.isVisible());
    }

    @Test
    public void synchronizesNativeScrollingWithAwtAdjustables() {
        Locator scrollPane = page.locator("[role=region]");
        assertEquals(1, scrollPane.count());
        scrollPane.hover();
        page.mouse().wheel(0, 100);
        page.waitForFunction("webawtShowcaseScrollPaneY() > 0");
        assertTrue(((Number) page.evaluate("webawtShowcaseScrollPaneY()")).intValue() > 0);
        scrollPane.evaluate("element => element.scrollTo(45, 65)");
        page.waitForFunction("webawtShowcaseScrollPaneX() === 45 && webawtShowcaseScrollPaneY() === 65");
        assertEquals(45, ((Number) page.evaluate("webawtShowcaseScrollPaneX()")).intValue());
        assertEquals(65, ((Number) page.evaluate("webawtShowcaseScrollPaneY()")).intValue());
    }

    @Test
    public void mapsScrollbarInputToAdjustmentEvents() {
        Locator scrollbar = page.locator("input[type=range]");
        assertEquals(1, scrollbar.count());
        assertEquals("horizontal", scrollbar.getAttribute("aria-orientation"));
        scrollbar.evaluate("element => { element.value = '35'; element.dispatchEvent(new Event('input', {bubbles: true})); }");
        assertEquals(35, ((Number) page.evaluate("webawtShowcaseScrollbarValue()")).intValue());
        assertEquals(1, ((Number) page.evaluate("webawtShowcaseScrollbarEventCount()")).intValue());
    }

    @Test
    public void translatesNativeCompositionAcrossTheBrowserBoundary() {
        Locator input = page.locator("input[type=text]");
        assertEquals(1, input.count());
        input.evaluate("element => element.dispatchEvent(new CompositionEvent('compositionupdate', {data: '\u6f22', bubbles: true}))");
        assertEquals(1, ((Number) page.evaluate("webawtShowcaseCompositionEventCount()")).intValue());
        assertEquals(0, ((Number) page.evaluate("webawtShowcaseCommittedCompositionCharacterCount()")).intValue());
        input.evaluate("element => element.dispatchEvent(new CompositionEvent('compositionend', {data: '\u6f22', bubbles: true}))");
        assertEquals(2, ((Number) page.evaluate("webawtShowcaseCompositionEventCount()")).intValue());
        assertEquals(1, ((Number) page.evaluate("webawtShowcaseCommittedCompositionCharacterCount()")).intValue());
    }

    @Test
    public void transfersAwtStringDataThroughNativeDragAndDrop() {
        Locator source = page.getByText("Drag payload", new Page.GetByTextOptions().setExact(true));
        Locator target = page.getByText("Drop here", new Page.GetByTextOptions().setExact(true));
        source.dragTo(target);
        page.waitForFunction("webawtShowcaseDropCount() === 1");
        assertEquals(1, page.getByText("Dropped: WebAWT drag payload", new Page.GetByTextOptions().setExact(true)).count());
    }

    @Test
    public void scalesCanvasBackingStoreForHighDensityDisplays() {
        context.close();
        context = null;
        openShowcase(2.0);
        Locator canvas = page.locator("canvas");
        int canvasWidth = Integer.parseInt(canvas.getAttribute("width"));
        double cssWidth = ((Number) canvas.evaluate("element => element.getBoundingClientRect().width")).doubleValue();
        assertEquals((int) Math.round(cssWidth * 2.0), canvasWidth);
        assertEquals(800, ((Number) page.evaluate("webawtShowcaseGraphicsEnvironmentWidth()")).intValue());
        assertEquals(600, ((Number) page.evaluate("webawtShowcaseGraphicsEnvironmentHeight()")).intValue());
        assertEquals(2.0, ((Number) page.evaluate("webawtShowcaseGraphicsEnvironmentScale()")).doubleValue(), 0.0001);
        assertEquals(800, ((Number) page.evaluate("webawtShowcaseToolkitScreenWidth()")).intValue());
        page.setViewportSize(1024, 720);
        assertEquals(1024, ((Number) page.evaluate("webawtShowcaseGraphicsEnvironmentWidth()")).intValue());
        assertEquals(720, ((Number) page.evaluate("webawtShowcaseGraphicsEnvironmentHeight()")).intValue());
        assertEquals(1024, ((Number) page.evaluate("webawtShowcaseToolkitScreenWidth()")).intValue());
    }

    @Test
    public void decodesImageDataThroughTheBrowserCanvas() {
        page.evaluate("webawtShowcaseStartImageDecode()");
        page.waitForFunction("(webawtShowcaseImageDecodeStatus() & 32) !== 0");
        assertEquals(32, ((Number) page.evaluate("webawtShowcaseImageDecodeStatus()")).intValue());
        assertEquals(1, ((Number) page.evaluate("webawtShowcaseImageDecodeWidth()")).intValue());
        assertEquals(1, ((Number) page.evaluate("webawtShowcaseImageDecodeHeight()")).intValue());
        assertTrue((Boolean) page.evaluate("webawtShowcaseDrawDecodedImage()"));
        List<?> pixel = (List<?>) page.locator("canvas").evaluate("canvas => Array.from(canvas.getContext('2d').getImageData(205, 25, 1, 1).data)");
        assertTrue(((Number) pixel.get(0)).intValue() > 200);
        assertTrue(((Number) pixel.get(1)).intValue() < 20);
        assertTrue(((Number) pixel.get(2)).intValue() < 20);
        assertTrue(((Number) pixel.get(3)).intValue() > 200);
    }

    @Test
    public void repeatedlyMountsAndRemovesTheAwtHierarchy() {
        for (int cycle = 0; cycle < 3; cycle++) {
            page.evaluate("webawtShowcaseUnmount()");
            assertEquals(0, page.locator("#webawt-root").locator(":scope > *").count());
            page.evaluate("webawtShowcaseMount('webawt-root')");
            assertEquals(1, page.locator("#webawt-root canvas").count());
        }
        page.locator("select").selectOption("Red");
        page.getByRole(AriaRole.BUTTON).click();
        assertEquals("Red", page.locator("#webawt-root span").first().textContent());
    }

    private static BrowserType selectBrowser(Playwright playwright, String browserName) {
        if ("firefox".equals(browserName)) {
            return playwright.firefox();
        }
        if ("webkit".equals(browserName)) {
            return playwright.webkit();
        }
        return playwright.chromium();
    }

    private static Path showcaseScript() {
        String testSourceDirectory = System.getenv("TEST_SRCDIR");
        String testWorkspace = System.getenv("TEST_WORKSPACE");
        if (testSourceDirectory == null || testWorkspace == null) {
            throw new IllegalStateException("The test must run through Bazel so its J2CL bundle is available as a runfile");
        }
        return Paths.get(testSourceDirectory, testWorkspace, "samples/showcase/showcase_application.js");
    }

    private void openShowcase(double deviceScaleFactor) {
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(800, 600).setDeviceScaleFactor(deviceScaleFactor));
        page = context.newPage();
        page.setDefaultTimeout(3000);
        page.setContent("<!doctype html><html><body><div id=webawt-root></div></body></html>");
        page.addScriptTag(new Page.AddScriptTagOptions().setPath(showcaseScript()));
        page.evaluate("webawtShowcaseMount('webawt-root')");
        assertTrue("The optimized J2CL application did not mount DOM content: " + page.content(), page.locator("#webawt-root").locator(":scope > *").count() > 0);
    }
}
