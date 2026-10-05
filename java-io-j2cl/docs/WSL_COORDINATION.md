# WSL coordination contract

All J2CL compatibility projects on this workstation use the same Ubuntu build
environment and must serialize memory-intensive Bazel work with:

```sh
flock /tmp/j2cl-build.lock <command>
```

Project validation scripts acquire this lock themselves. Do not wrap one
validation script from another while holding the same lock. All projects use
J2CL `20260402`, Bazel `8.3.1`, rules_closure `0.16.0`, Java 21, one Bazel job,
one test job, and one worker/multiplex worker on this host.

The coordinated ownership boundary is:

- `java-io-j2cl`: missing `java.io.*` JRE declarations plus explicitly recorded
  prerequisites.
- `java-nio-j2cl`: missing `java.nio.*` declarations.
- WebAWT: `java.awt`, browser peers, and its current `java.net` compatibility
  layer until a shared JRE patch is agreed.
- WebSwing and `org-jdom-j2cl`: consumers; they must not copy IO/NIO classes.

`File.toPath()` and `Path.toFile()` require a composed root-owned J2CL patch and
remain outside individual releases until the IO and NIO patches pass a joint
consumer build. `File.toURI()` likewise remains deferred while WebAWT owns URI.

If WSL terminates a cold build without a compiler diagnostic, inspect memory and
kernel logs before changing Java sources. Never run cold J2CL builds from two
projects concurrently on this host.
