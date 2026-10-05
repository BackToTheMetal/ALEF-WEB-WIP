# Deployment actions

The Deployment page now keeps the Deploy package action visible in the header
and first environment row. Subsequent environments show Promote to <environment>
even when promotion is not ready. Disabled actions explain missing permission,
approval/finalization, a required preceding deployment, an active deployment,
a disabled environment, or an already installed package. Active deployments
continue to offer View progress.

The author account has no Deploy permission. Its controls therefore remain
disabled; this change grants no permissions and preserves backend authorization.
Authorized users use the existing approved-package picker and promotion flow.

Verified on 29 September 2026:

- `node tools/test_deployment_tab.mjs`: visibility and prerequisite states,
  approved-package selection, idempotent retries, promotion of the same digest,
  progress/history and responsive layout passed using isolated fixtures.
- `node tools/test_interface.mjs`: existing interface regression checks passed.
- `node tools/test_deployment_tab_live.mjs`: served author and administrator views
  passed without package, deployment or workspace writes.
- Served `interface.js` and `interface.css` match the changed source files.

Screenshots are under `out/deployment-tab`. The deployment fixture server was
also updated to serve the Vue runtime now imported by the shared project view.
Only frontend assets were published; the backend and native editor were not
restarted or rebuilt. Deployment remains the existing simulated delivery flow.
