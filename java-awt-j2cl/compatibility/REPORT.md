# WebAWT compatibility report

This report combines the planning classification with explicitly tracked implementation state. A type is only marked implemented after it transpiles and its current tests pass.

Reference inventory: `compatibility/openjdk-awt-api.json`

## Summary

| Status | Types | Members |
| --- | ---: | ---: |
| exact | 85 | 1826 |
| web-mapped | 30 | 855 |
| limited | 184 | 2033 |
| deferred | 0 | 0 |
| unavailable | 11 | 131 |
| excluded | 15 | 169 |

## Implementation state

| State | Types | Members |
| --- | ---: | ---: |
| implemented | 265 | 3592 |
| partial | 34 | 1124 |
| not-started | 26 | 298 |

## Explicitly excluded

- `java.awt.FileDialog`
- `java.awt.JobAttributes`
- `java.awt.PageAttributes`
- `java.awt.print.Book`
- `java.awt.print.Pageable`
- `java.awt.print.PageFormat`
- `java.awt.print.Paper`
- `java.awt.print.Printable`
- `java.awt.print.PrinterAbortException`
- `java.awt.print.PrinterException`
- `java.awt.print.PrinterGraphics`
- `java.awt.print.PrinterIOException`
- `java.awt.print.PrinterJob`
- `java.awt.PrintGraphics`
- `java.awt.PrintJob`

## Unavailable platform APIs

- `java.awt.AWTPermission`: The type declaration requires missing J2CL-owned type(s): java.security.BasicPermission
- `java.awt.event.AWTEventListenerProxy`: The type declaration requires missing J2CL-owned type(s): java.util.EventListenerProxy
- `java.awt.im.InputSubset`: The type declaration requires missing J2CL-owned type(s): java.lang.Character$Subset
- `java.awt.MouseInfo`: No safe or meaningful browser equivalent
- `java.awt.PointerInfo`: No safe or meaningful browser equivalent
- `java.awt.Robot`: No safe or meaningful browser equivalent
- `java.awt.SecondaryLoop`: No safe or meaningful browser equivalent
- `java.awt.SplashScreen`: No safe or meaningful browser equivalent
- `java.awt.SystemTray`: No safe or meaningful browser equivalent
- `java.awt.Taskbar`: No safe or meaningful browser equivalent
- `java.awt.TrayIcon`: No safe or meaningful browser equivalent

## Next audit steps

1. Review deferred and limited entries method by method before implementation.
2. Expand JVM differential vectors as each portable type is implemented.
3. Run SonarQube analysis when a server or CI service is configured.
4. Update implementation state only after code, tests, browser verification and quality checks pass.
