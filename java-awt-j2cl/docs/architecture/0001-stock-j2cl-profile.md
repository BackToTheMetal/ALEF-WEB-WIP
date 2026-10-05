# ADR 0001: Stock J2CL compatibility profile

## Status

Accepted for Phase 0.

## Context

J2CL does not provide AWT and exposes a deliberately limited JRE. A complete OpenJDK AWT signature set references types that are absent from stock J2CL, including types in packages already owned by the J2CL JRE.

## Decision

WebAWT will initially publish a stock-J2CL compatibility profile. Every included method must work or have a meaningful documented browser mapping. APIs that cannot meet that rule will be absent and recorded in the compatibility manifest. WebAWT will not maintain a J2CL JRE fork during the initial implementation.

File dialogs and all printing APIs are excluded from the approved scope.

## Consequences

The initial artifact will not claim complete Java SE AWT compatibility. Consumers will receive compile-time feedback for absent APIs instead of delayed runtime failures. A patched-J2CL profile may be evaluated later as a separate decision.

## Signature gates

The generated dependency audit is authoritative for the pinned J2CL release. The following owned-JRE gaps require explicit omissions in the stock profile:

| Missing J2CL contract | Affected AWT surface | Stock-profile decision |
| --- | --- | --- |
| `java.io.File` and `java.io.FilenameFilter` | Font file loading, desktop and file-oriented APIs | Omit file overloads; file dialogs and printing remain excluded |
| `java.lang.reflect.InvocationTargetException` | `EventQueue.invokeAndWait` | Omit the blocking method and provide browser task scheduling through `invokeLater` |
| `java.util.ResourceBundle` | `ComponentOrientation.getOrientation(ResourceBundle)` | Omit the overload; retain locale-based orientation |
| `java.util.EventListenerProxy` | AWT and JavaBeans named-listener proxies | Preserve registration and dispatch; omit proxy wrapper classes and document aggregate getter differences |
| `java.util.Hashtable` and `java.util.Properties` | Legacy image-consumer properties and toolkit desktop properties | Omit the affected legacy signatures; expose typed browser capabilities outside `java.awt` where needed |
| `java.lang.Character.Subset` | Font Unicode subset APIs | Omit subset overloads in the stock profile |
| `java.security.BasicPermission` | `AWTPermission` | Omit the permission type because the browser sandbox is the security boundary |

Types supplied in previously unclaimed packages, including the required portions of `java.beans`, `java.text`, and `javax.accessibility`, are project-owned compatibility code and must satisfy the same differential-test and provenance rules as `java.awt`.
