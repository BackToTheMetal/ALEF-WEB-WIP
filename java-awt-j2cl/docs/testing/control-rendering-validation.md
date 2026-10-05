# Control rendering validation — 2026-09-06

The highlighted dual-runtime gallery issues are corrected in the AWT model and browser peers: frame menu space and Help placement, checkbox captions and indicator sizing, radio group presentation and reassignment, label alignment, and logical font conversion.

## Executed checks

Every check in `tools/quality/Validate-WebAwt.ps1` passed. Bazel commands used `--jobs=1 --worker_max_instances=1`; the repository's default build settings were not changed. Earlier runs were interrupted by another task restarting WSL. The final complete run exited successfully after those restarts stopped.

| Check | Result |
| --- | --- |
| Java formatting | 448 source files passed |
| Validation tooling | 5 tests passed |
| Production dependencies | Passed; no new production dependencies |
| `//:all-tests` | 46 test targets passed |
| `//:display-tests` under Xvfb | 3 test targets passed, including shared JDK checkbox behavior |
| `//:modern-browser-tests` | All 6 suites passed: both showcases in Chromium, Firefox, and WebKit |
| `bazelisk build //...` | 303 targets built successfully |
| Bundle budgets | Passed with the documented small-showcase budget adjustment |
| Compatibility tooling | 14 tests passed |
| Emitted API | 299 types compared with the 325-type OpenJDK inventory; zero false implementation claims |
| Completeness invariants | 299/299 target types and 4714/4714 target members callable; zero invariant violations |

API callability does not establish complete semantic compatibility. Existing partial support classifications remain in place.

The browser tests verify visible captions, compact indicators, label centering, menu/header separation, right-aligned Help and its popup bounds, mouse and keyboard selection, event counts, and repeated mounting. DOM integration tests also verify runtime alignment, inherited fonts, menu replacement, resizing, hiding/showing, group changes, and listener disposal. The final Chromium screenshot was inspected visually.

## Artifacts and size

- [Final Chromium controls screenshot](../screenshots/controls-chromium.png)
- [Implementation decision and measured bundle cost](../architecture/0007-control-captions-and-frame-menus.md)
- [Generated bundle report](../../compatibility/bundle-size-report.json)
- [Generated API validation](../../compatibility/webawt-api-validation.md)

The small showcase grew by 5,960 raw bytes and 1,691 gzip bytes (3.2 percent compressed). Its limits are now 168 KiB raw and 55 KiB gzip; the measured output is 169,193 raw bytes and 54,959 gzip bytes. The dual showcase is 240,174 raw bytes and 79,440 gzip bytes, within its existing limits.
