# Modern browser tests

The Playwright tests load the optimized J2CL showcase output in a real headless browser. They use browser-generated pointer and keyboard input, inspect rendered Canvas pixels, verify native roles and focus traversal, check high-DPI Canvas backing storage, and repeat mount/disposal cycles. These checks cover the browser boundary that JVM differential tests and the legacy J2CL browser runner cannot reproduce.

The same JUnit methods run against Chromium, Firefox, and WebKit. Playwright supplies a Java API and browser builds matched to its version, which avoids relying on whichever browser happens to be installed on a developer machine. Playwright and JUnit are test-only dependencies and are not present in WebAWT production targets or output.

Install the pinned browser builds once in WSL:

```shell
bazelisk run //src/test/playwright/com/webawt/playwright:playwright_cli -- install chromium firefox webkit
```

On a minimal Linux or CI host, install the browser builds and documented system libraries with sufficient operating-system privileges:

```shell
bazelisk run //src/test/playwright/com/webawt/playwright:playwright_cli -- install --with-deps chromium firefox webkit
```

Run the complete modern matrix with:

```shell
bazelisk test //:modern-browser-tests --nocache_test_results --test_output=errors
```

Run one engine while diagnosing a failure with one of these targets:

```shell
bazelisk test //src/test/playwright/com/webawt/playwright:WebAwtChromiumTest --test_output=errors
bazelisk test //src/test/playwright/com/webawt/playwright:WebAwtFirefoxTest --test_output=errors
bazelisk test //src/test/playwright/com/webawt/playwright:WebAwtWebKitTest --test_output=errors
```

Bazel marks only these external-browser tests `no-sandbox` because the browser executables live in Playwright's user cache. Test inputs and the optimized application remain Bazel runfiles. The CI workflow installs browser binaries and operating-system dependencies from the pinned Playwright CLI, so browser management remains versioned with the test dependency.
