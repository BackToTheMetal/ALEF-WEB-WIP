# Approved ALEF interface — revision 23

The approved, runnable prototype is preserved in
`alef-interface-approved-r23-2026-09-27.zip`. It contains the complete
`2026-09-25-interface` directory: active revision 23, every earlier iteration,
design notes, scripts, verification scripts, and image assets.

SHA-256: `367BA2C4ABA9CC255FCEEF9AA663F70C78FEFFE84868D56A746C33C6D036384A`

To restore, extract the archive into an empty directory. Run
`node 2026-09-25-interface/preview-server.cjs` from the restored directory and use
the address printed by the server. `index.html` is the approved entry point.
This archive is a design reference; it contains simulated data and must never
be connected to production services.

The working reference remains in `../2026-09-25-interface/`. Application
implementation belongs in `example/` and the ALEFBackend application, not in
the archived prototype. Keep this ZIP immutable and include it in repository
backups. Local preservation is complete; no remote backup has been performed.
