# Try the ALEF backend preview

Open **http://localhost:8080/**. The local application is running with real
Keycloak login, PostgreSQL governance, Git model persistence and the native MPS
worker. The existing generated browser editor loads the complete authorized
sample workspace. The preview uses a small workspace; further large-workspace
performance tuning is deferred.

## Accounts

Development usernames are `author`, `editor`, `reviewer` and `administrator`.
Their generated passwords are in this private, ignored local file:

`C:\Users\niekh\Documents\ChatGPT\ALEFBackend\.local\development-identities.json`

Use a separate browser profile or private window for the reviewer or second
editor. Ordinary tabs share the login session, but each tab owns its own editing
leases. Do not copy these development credentials into source control.

## First journey

1. Log in as `author` and wait for loading to finish.
2. Select a root in the project tree and choose **Lock root**. Edit using the
   existing generated editor and inspector, then choose **Save**.
3. Reload to check the saved result. Save publishes a structurally valid draft;
   semantic diagnostics may still need correction before a release validates.
4. Open **Releases** and submit the current revision. Wait for native validation
   and package preparation, then inspect changes and validation evidence.
5. Log in as `reviewer` in another browser session to approve the exact package.
   The submitter and contributors cannot approve their own release. Validation
   failures require corrections and a new candidate.
6. Open **Execution platforms** to deliver an approved package to simulated test
   and promote those same bytes to simulated production. These durable stubs
   record progress and results; **they do not execute ALEF services**.

The local sample contains edits from integration verification. It has not been
reset to discard existing work. A second authorized user can read a locked root;
only its owning tab can edit it while its lease remains valid.

## Restart the existing setup

In PowerShell 7, from `C:\Users\niekh\Documents\ChatGPT\ALEFBackend`:

```powershell
./tools/start-application.ps1 -Restart
```

Keep PostgreSQL and Keycloak running. This restarts the application's owned API
and worker, preserving saved models and workflow history. Do not bootstrap the
existing workspace again. Initial setup, builds and troubleshooting are documented
in `C:\Users\niekh\Documents\ChatGPT\ALEFBackend\docs\running-the-application.md`.

## Verification and remaining work

The freshly served optimized Chromium bundle passed real login, complete loading,
root locking, keyboard editing, governed Save and reload on 2026-09-25
(`out/backend-demo-editor.log`). Earlier evidence covers restart, the other browser
engines, release approval and simulated deployment.

This is a working preview, not a claim that the full implementation checklist is
finished. See [implementation status](backend-implementation-status.md) for
verified features, capacity measurements, remaining hardening and staged changes
that are not installed. The default workspace limit is 10,000 nodes. The 100,000
node trial needed a larger API heap and showed slow responses and concurrent
snapshot quota rejections; it is not the active preview configuration.
