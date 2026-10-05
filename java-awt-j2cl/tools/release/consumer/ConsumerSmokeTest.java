import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class ConsumerSmokeTest {
    @Test
    public void archivedShowcaseMountsPaintsAndDisposesInAllEngines() {
        try (Playwright playwright = Playwright.create()) {
            for (BrowserType engine : new BrowserType[] {playwright.chromium(), playwright.firefox(), playwright.webkit()}) {
                try (Browser browser = engine.launch(new BrowserType.LaunchOptions().setHeadless(true)); BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 900))) {
                    Page page = context.newPage();
                    System.out.println(engine.name() + " browser revision: " + browser.version());
                    java.util.List<String> errors = new java.util.ArrayList<>();
                    page.onPageError(errors::add);
                    page.setContent("<!doctype html><div id='webawt-root'></div>");
                    page.addScriptTag(new Page.AddScriptTagOptions().setPath(Paths.get(System.getenv("TEST_SRCDIR"), System.getenv("TEST_WORKSPACE"), "browser_application.js")));
                    assertEquals("PASS", page.evaluate("consumerRuntimeResult"));
                    page.evaluate("webawtDualShowcaseMount('webawt-root')");
                    assertTrue((Boolean) page.evaluate("webawtDualShowcaseRunSelfCheck()"));
                    page.evaluate("webawtDualShowcaseSelectPage('Rendering lab')");
                    assertTrue(page.locator("canvas").count() > 0);
                    page.getByRole(com.microsoft.playwright.options.AriaRole.COMBOBOX).nth(1).selectOption("Graphics state");
                    page.getByRole(com.microsoft.playwright.options.AriaRole.COMBOBOX).nth(2).selectOption("Curves and mutable image reuse");
                    for (int x : new int[] {110, 530}) {
                        assertEquals(java.util.Arrays.asList(0, 0, 255, 255), page.locator("canvas:visible").evaluate("(canvas, x) => Array.from(canvas.getContext('2d').getImageData(x,130,1,1).data)", x));
                    }
                    String outputs = System.getenv("TEST_UNDECLARED_OUTPUTS_DIR");
                    if (outputs != null) {
                        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(outputs, "consumer-" + engine.name() + ".png")));
                    }
                    page.evaluate("webawtDualShowcaseUnmount()");
                    assertEquals(0, page.locator("#webawt-root > *").count());
                    assertEquals(java.util.Arrays.asList(0, 0, 0, 0, 0), page.evaluate("webawtDualShowcaseResourceDiagnostics()"));
                    assertTrue(engine.name() + " page errors: " + errors, errors.isEmpty());
                    System.out.println(engine.name() + " archived showcase mount/render/self-check/teardown passed");
                }
            }
        }
    }
}
