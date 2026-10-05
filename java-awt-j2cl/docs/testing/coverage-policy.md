# Coverage policy

WebAWT does not publish a line-coverage percentage yet because the available JVM differential tests execute the OpenJDK `java.awt` reference implementation, not the WebAWT classes that J2CL transpiles. Reporting those JVM lines as WebAWT coverage would be incorrect.

The mandatory coverage model currently has three independently enforced parts:

- Portable behavior vectors run against OpenJDK on the JVM and WebAWT under J2CL.
- Browser contract tests drive optimized WebAWT output in Chromium, Firefox, and WebKit.
- Emitted-API validation proves that every type and member marked implemented remains callable after transpilation.

Owner decision (2026-09-08): SonarQube and source-attributed percentage coverage are deferred. They are not required to prepare this release candidate. The existing behavior, browser, API-profile and packaging gates remain required; no percentage or Sonar pass is claimed.

The project will add a percentage quality gate only when coverage can be attributed to the original WebAWT Java sources. The intended implementation is source-map-aware instrumentation of J2CL output, LCOV generation from all browser suites, path normalization back to `src/main/java`, and SonarQube import of that report. Any project-owned JVM-only code can be merged separately, but execution of OpenJDK reference classes must never contribute to the WebAWT numerator or denominator.

Until that pipeline exists, new or promoted APIs require behavior assertions in the appropriate J2CL or real-browser suite. Coverage exclusions must describe generated or deliberately out-of-scope code; they must not be used to manufacture a target percentage.
