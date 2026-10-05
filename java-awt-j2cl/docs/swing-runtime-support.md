# Shared Swing runtime support

The 0.2.0 browser profile provides the compiler/runtime Class type operations in
`third_party/patches/j2cl-type-support.patch` and the standard callback exception
in `j2cl-invocation-support.patch`. Both target J2CL 20260402. Copy these patches
into the consuming root and apply them once with `single_version_override`;
dependency MODULE overrides are not inherited. The Swing archive carries verified
copies of these AWT-owned patches. No MPS runtime or source tree is required.

Type metadata remains required with optimized/STRIPPED builds. The patch generator
is `tools/compatibility/make_type_support_patch.py`; it takes the pristine pinned
J2CL tree through `--upstream`. Supporting contracts live in `src/test/runtime`.

Normal consumers keep AWT's four java.net classes. A composed JRE that already
provides URI, URL, URISyntaxException and MalformedURLException sets
`--@webawt//src/main/java/java/net:external_jre=true`. The setting removes only
those sources while preserving the uri-j2cl label. The consumer must provide all
four compatible classes; selecting this option without a provider is unsupported.

The Class support source and its behavioral limits are retained from the reviewed
MPS integration. Native JsType nominal identities remain explicitly unsupported;
ordinary Java subclasses, interfaces, arrays and lambdas retain type relations.

Standalone roots also apply j2cl-reflection-base.patch, a checksummed snapshot
of java-io-j2cl ReflectiveOperationException. Composed java-io consumers omit
that patch because their JRE already owns the identical class.

`tools/compatibility/check_net_profile.py` analyzes both flag values and verifies
that the stable target exposes exactly four AWT sources or none, respectively.
