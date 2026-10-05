// Standalone design revision 03. This replaces the revision-02 review screens.
// It reuses the captured editor and environment layouts; no ALEF API is called.
// Package contents are frozen. Review metadata lives separately from the bytes.
(() => {
  const originalModel = model;
  const originalEnvironment = environmentPage;
  const originalHandle = handle;
  let releaseId = 18, releaseTitle = 'Gegevensmodel update', releaseVersion = '1.4.0';
  let detail = false, section = 'diff', selected = 2, filter = 'all';
  let requestText = 'Please correct the Testspraak expectation for the service response.';
  let showAllRoots = false;
  const rootSeed = [
    [rootName, 'servicetest.choice', 18, 'a731d1c0'],
    ['serviceapartmodel', 'serviceEnBerichtTypesInAparteModellen.Service', 9, '63b94ae2'],
    ['ServiceTest', 'serviceEnBerichtTypesInAparteModellen.Service', 12, '12a30b8d'],
    ['ibm', 'servicetest.choice', 4, 'c01a7140'],
    ['keuzetest_A1790279010802', 'servicetest.choice', 7, 'a0938ca1'],
    ['leeg', 'servicetest.choice', 2, '20b41ce0'],
    ['MoveTarget1790287481145', 'servicetest.choice', 1, '71e02a39'],
    ['MoveTarget1790287585892Z', 'servicetest.choice', 3, '0f849aca'],
    ['ubc', 'servicetest.choice', 6, '21eb95ce'],
    ['Berichtuit_B1790279324981', 'serviceEnBerichtTypesInAparteModellen.Berichten', 5, 'aff19651'],
    ['Invoer_spApart_spModel_A1790279324981', 'serviceEnBerichtTypesInAparteModellen.Berichten', 4, '710d4a8a'],
    ['Uitvoer_spApart_spModel', 'serviceEnBerichtTypesInAparteModellen.Berichten', 3, '0aba21ca'],
    ['Regels apart model', 'serviceEnBerichtTypesInAparteModellen.Regels', 8, 'e9ab3e78'],
    ['Regeltest', 'serviceEnBerichtTypesInAparteModellen.Service', 5, '487b212a'],
    ['Gegevens apart model Remote', 'serviceEnBerichtTypesInAparteModellen.Gegevens', 6, '639fee02']
  ];
  const roots = rootSeed.map(([name, modelName, version, hash], i) => Object.freeze({
    id: 'demo-root-' + String(i + 1).padStart(3, '0'), name, modelName, version, hash
  }));
  const seal = (number, digest, source, included, expected = 'Jan') => Object.freeze({
    number, id: `REL-${releaseId}-P${number}`, digest, source,
    roots: Object.freeze(included.slice()), expected,
    baseline: 'v1.3.0 · 73fd446', dependencies: 'ALEF/MPS · pinned toolchain',
    created: number === 1 ? 'Today, 09:10' : number === 2 ? 'Today, 10:42' : 'Just now'
  });
  let packages = [
    seal(1, '77ac9821…401b', '19c73a4', roots.map((r, i) => i === 1 || i === 2 ? Object.freeze({...r, version: r.version - 1, hash: i === 1 ? '94dc712e' : '47002bac'}) : r)),
    seal(2, '9c1f72b4…6f08', '4ae7b2c', roots)
  ];
  let decisions = {1: 'Changes requested', 2: 'In review'};
  const current = () => packages[packages.length - 1];
  const inspected = () => packages.find(p => p.number === selected);
  const status = () => decisions[selected];
  const latest = () => selected === current().number;
  const badge = s => `<span class="pill ${s === 'Changes requested' ? 'warn' : ['Rejected', 'Revoked'].includes(s) ? 'danger' : s === 'In review' ? 'neutral' : ''}">${s}</span>`;

  go = next => {
    detail = ['pulls', 'release'].includes(next);
    view = detail ? 'releases' : next;
    location.hash = detail ? 'release' : view;
    render();
  };
  steps = active => `<div class="flow-steps">${['Prepare package', 'Review', 'Accept', 'Release', 'Deploy'].map((s, i) => `${i ? I('chevron') : ''}<span class="${i + 1 === (view === 'deployments' ? 5 : active) ? 'active' : ''}">${i + 1}. ${s}</span>`).join('')}</div>`;
  render = () => {
    document.querySelectorAll('nav [data-page]').forEach(b => b.classList.toggle('active', b.dataset.page === view));
    document.querySelector('#screen').innerHTML = view === 'model' ? model() : view === 'changes' ? changes() : view === 'releases' ? (detail ? releaseDetail() : releaseCatalog()) : environmentPage();
    const role = view === 'releases' && detail ? 'reviewer' : view === 'deployments' ? 'administrator' : 'author';
    document.querySelector('#identity-label').textContent = role + ' Local';
    document.querySelector('#avatar').textContent = role === 'reviewer' ? 'RL' : role === 'administrator' ? 'AD' : 'AL';
    document.querySelector('#state-preview').disabled = view !== 'model';
    document.querySelector('#state-preview').value = editorState;
    document.querySelector('#footer-state').textContent = 'Design preview · no application changes';
    iconLabels();
  };
  model = () => originalModel()
    .replace(/PR #\d+/g, 'Release #' + releaseId)
    .replace(/Correction draft from revision \d+/g, 'Correction draft from package ' + current().number)
    .replace(/Review & submit revision \d+/g, 'Review replacement package')
    .replace('PR correction draft', 'Package correction draft');
  environmentPage = () => originalEnvironment()
    .replace('Illustrative deployed release', 'Independent deployment scenario · illustrative')
    .replaceAll(released ? snapshots[revision].digest : '9c1f72b4…6f08', '9c1f72b4…6f08');

  function rootManifest(p) {
    const rows = showAllRoots ? p.roots : p.roots.slice(0, 3);
    return `<article class="card"><div class="card-head">Included roots <span class="count">${p.roots.length}</span><span class="grow"></span><span class="pill neutral">Fixed versions</span></div><div class="manifest-wrap"><table class="manifest-table"><thead><tr><th>Root / model</th><th>Pinned version</th><th>Content hash</th></tr></thead><tbody>${rows.map(r => `<tr><td><strong>${escapeText(r.name)}</strong><small>${escapeText(r.modelName)}</small><small><code>${r.id}</code></small></td><td><span class="pin">${I('lock')}v${r.version}</span></td><td><code>${r.hash}</code></td></tr>`).join('')}</tbody></table></div><div class="release-card-footer"><span>${showAllRoots ? 'All 15 roots shown' : '3 changed roots shown · 12 unchanged roots also pinned'}</span><span class="grow"></span>${B(showAllRoots ? 'Show changed roots' : 'Show all 15 roots', 'package-roots-toggle', 'small ghost')}</div><div class="package-root-note">Every root is a fixed version, including unchanged roots. Workspace edits never update this manifest. Names come from the inspected workspace; IDs, versions and hashes here are illustrative.</div></article>`;
  }
  function packageDiff() {
    const p = inspected();
    const name = selectedFile === 'gegevens' ? 'Gegevensmodel' : selectedFile === 'service' ? 'serviceapartmodel' : 'ServiceTest';
    const before = selectedFile === 'gegevens' ? ['Objecttype de naam', '  de bijnaam   Tekst;', '  de initialen Tekst;', '  de voornaam  Tekst;'] : selectedFile === 'service' ? ['Service serviceapartmodel', '  uitvoer naam', '  verwijst naar de voornaam'] : ['ServiceTest', '  invoer persoon', '  verwacht voornaam = "Jan"'];
    const after = before.slice();
    if (selectedFile === 'gegevens') after[3] = '  de roepnaam  Tekst;';
    else if (p.number > 1 || releaseId > 18) after[2] = selectedFile === 'service' ? '  verwijst naar de roepnaam' : `  verwacht roepnaam = "${p.expected}"`;
    return `<div class="diff-workspace">${files()}<div class="diff-main"><div class="diff-top">${I('file')}<strong>${name}</strong><span class="grow"></span>${I('lock')}<span class="pill neutral">Read only</span></div><div class="line-diff">${[[`Baseline · v1.3.0`, before], [`Package ${p.number} · fixed snapshot`, after]].map(([label, lines], col) => `<div><div class="diff-title">${label}</div>${lines.map((line, i) => `<div class="number-line ${i === lines.length - 1 && before[i] !== after[i] ? col ? 'add' : 'remove' : ''}"><span class="ln">${i + 1}</span><code>${escapeText(line)}</code></div>`).join('')}<div class="diff-space"></div></div>`).join('')}</div><div class="inline-thread"><div class="flex"><span class="avatar">RL</span><strong>reviewer Local</strong><span class="grow"></span><span class="pill ${p.number > 1 ? '' : 'warn'}">${p.number > 1 ? 'Resolved in package 2' : 'Discussion'}</span></div><p>Please also update the service reference and the matching Testspraak expectation.</p><small>Comment anchored to package 1 · root version remains unchanged.</small><div style="margin-top:9px">${B('Reply', 'package-reply', 'small ghost')}</div></div></div></div>`;
  }
  function packageBody(p) {
    if (section === 'roots') return rootManifest(p);
    if (section === 'diff') return packageDiff();
    if (section === 'checks') return `<article class="pr-overview"><h2>Validation evidence · package ${p.number}</h2><div class="check-line">${I('check')}Native MPS validation passed</div><div class="check-line">${I('check')}1 Testspraak test passed</div><div class="check-line">${I('check')}All 15 root versions and references pinned</div><div class="check-line">${I('check')}Dependencies, resources and toolchain recorded</div><div class="check-line">${I('check')}Package checksummed and retained</div><p class="hint">2 informational diagnostics. Evidence applies only to <code>${p.id}</code> and <code>${p.digest}</code>.</p><p class="hint">Illustrative evidence; this prototype does not run validation.</p></article>`;
    if (section === 'history') return `<article class="card package-history"><div class="card-head">Package history<span class="grow"></span><span class="hint">Same release · separate immutable packages</span></div>${packages.slice().reverse().map(s => `<div class="release-row ${s.number === selected ? 'selected' : ''}"><span class="platform-icon">${I('lock')}</span><div class="grow"><strong>Package ${s.number} ${s.number === current().number ? '· current candidate' : '· retained'}</strong><p>${s.created} · ${decisions[s.number]}</p><code>${s.id} · sha256:${s.digest}</code><p>${s.number > 1 ? `Based on package ${s.number - 1}; unchanged root versions carried forward.` : 'Created from a fixed saved workspace revision.'}</p></div>${B('Inspect package ' + s.number, 'inspect-package', 'small', '', `data-package="${s.number}"`)}</div>`).join('')}<div class="package-root-note">Review decisions stay with their original package. Replacement packages require new validation and acceptance; previous packages are never overwritten.</div></article>`;
    return `<article class="pr-overview"><h2>Release summary</h2><p>Update the name model, related service references and matching tests. This release contains a complete workspace package.</p><h2 style="margin-top:24px">Activity</h2><div class="timeline"><div class="timeline-row"><strong>Package ${p.number} · ${status().toLowerCase()}</strong><small>Independent decision bound to its exact digest and root manifest.</small></div><div class="timeline-row"><strong>Root versions frozen at package creation</strong><small>15 roots · source ${p.source} · baseline v1.3.0</small></div><div class="timeline-row"><strong>Release #${releaseId} created</strong><small>author Local · ${escapeText(releaseTitle)}</small></div></div></article>`;
  }
  function releaseDetail() {
    const p = inspected(), s = status(), old = !latest();
    const accepted = ['Accepted', 'Released'].includes(s);
    return `<section class="page release-page">${steps(s === 'Released' ? 4 : s === 'Accepted' ? 3 : 2)}<div class="breadcrumbs">Releases ${I('chevron')} #${releaseId}</div><div class="page-heading pr-title"><div class="grow"><div class="flex" style="margin-bottom:7px;flex-wrap:wrap"><h1 style="margin:0">${escapeText(releaseTitle)}</h1>${badge(s)}</div><div class="pr-meta"><span>v${escapeText(releaseVersion)}</span><span>·</span><span>author Local</span><span>·</span><code>${p.id}</code>${old ? '<span class="pill neutral">Earlier package · read only</span>' : ''}</div></div><div class="page-actions">${!old && s === 'Changes requested' ? B('Prepare replacement', 'prepare-replacement', 'primary', 'file') : ''}${!old && s === 'Accepted' ? B('Release package', 'release-package', 'primary', 'arrow') : ''}${!old && s === 'Released' ? B('Deploy to environments', 'go-environments', 'primary', 'arrow') : ''}${B('All releases', 'all-releases', 'small')}</div></div><div class="freeze-note">${I('lock')}<div><strong>Root versions in this package never change.</strong><small>${old ? 'This earlier package is retained exactly as reviewed.' : 'Review, acceptance and release all refer to these same bytes.'} Corrections require a replacement package.</small></div></div>${s === 'Changes requested' ? `<div class="subtle-banner warn"><div><strong>Changes requested on package ${p.number}</strong><div class="package-comment">${escapeText(requestText)}</div><small>Start from this exact package; correct a draft, then create a new package for review.</small></div></div>` : ''}<div class="package-summary"><div><span class="upper">Immutable package</span><strong>Package ${p.number} ${old ? '· retained' : '· current candidate'}</strong><small><code>sha256:${p.digest}</code></small></div><div><span class="upper">Included model</span><strong>15 pinned roots · 6 models</strong><small>Complete workspace + dependencies</small></div><div><span class="upper">Comparison baseline</span><strong>v1.3.0 · 3 roots changed</strong><small>Source revision <code>${p.source}</code></small></div></div><div class="pr-tabs package-tabs" role="tablist" aria-label="Release sections">${[['overview', 'Overview'], ['diff', 'Changes'], ['roots', 'Included roots'], ['history', 'Package history'], ['checks', 'Checks']].map(([key, label]) => B(label + (key === 'history' ? ` <span class="count">${packages.length}</span>` : ''), 'package-section', section === key ? 'active' : '', '', `data-section="${key}" role="tab" aria-selected="${section === key}"`)).join('')}<span class="grow"></span><select class="revision-select" aria-label="Inspect package">${packages.slice().reverse().map(s => `<option value="${s.number}" ${s.number === selected ? 'selected' : ''}>Package ${s.number} · ${s.number === current().number ? 'current' : 'earlier'}</option>`).join('')}</select></div><div class="pr-layout"><div>${packageBody(p)}<div class="immutable-strip" style="border:1px solid var(--line);margin-top:14px;border-radius:5px">${I('lock')}Created ${p.created}<span class="grow"></span><span>Fixed source, references & resources</span><code>${p.source}</code></div></div><aside class="review-rail stack"><article class="card"><div class="card-head">Review & acceptance<span class="grow"></span><span class="pill neutral">${accepted ? '1' : '0'} / 1</span></div><div class="card-body"><div class="flex"><span class="avatar">RL</span><div><strong style="font-size:12px">reviewer Local</strong><div class="hint">Independent reviewer</div></div></div><div class="check-line">${I('check')}Access to all included roots</div><div class="check-line">${I('check')}Not submitter or contributor</div><div class="check-line">${I('check')}Validation evidence attached</div><p>Acceptance binds package ${p.number}, its root versions and digest.</p>${!old && s === 'In review' ? `<label class="hint" for="package-feedback">Review summary</label><textarea id="package-feedback" class="review-reason" placeholder="Add your review feedback…"></textarea><div class="decision-actions">${B('Accept package ' + p.number, 'accept-package', 'primary', 'check')}${B('Request changes', 'package-request-changes')}${B('Reject', 'reject-package', 'small ghost')}</div>` : old ? '<div class="subtle-banner">Retained for inspection. Review the current candidate to make a decision.</div>' : s === 'Changes requested' ? '<span class="pill warn">Awaiting replacement</span><p>This package stays frozen. The replacement starts with zero acceptances.</p>' : accepted ? `<span class="pill">${I('check')}Exact package accepted</span><p>${s === 'Released' ? 'Released unchanged. Each environment receives these exact bytes.' : 'Ready to release. No rebuild or root-version refresh.'}</p>${B('Revoke acceptance', 'revoke-package', 'small ghost')}` : `<p>${s === 'Revoked' ? 'New deployments are blocked. Existing installations and history are retained.' : 'This candidate is closed. Its package is retained.'}</p>`}</div></article><article class="card compact-card"><h3>One release workflow</h3><div class="facts"><div><span class="muted">Release</span><strong>#${releaseId} · v${escapeText(releaseVersion)}</strong></div><div><span class="muted">Package</span><strong>${p.id}</strong></div><div><span class="muted">Contents</span><strong>Immutable</strong></div><div><span class="muted">Deployment</span><strong>${s === 'Released' ? 'Eligible · target gates apply' : 'Not eligible'}</strong></div></div><p class="revision-note">Versions, hashes and decisions are illustrative. Review metadata is separate from package content.</p></article></aside></div></section>`;
  }
  function releaseCatalog() {
    const s = decisions[current().number];
    return `<section class="page release-page"><div class="page-heading"><div class="grow"><h1>Releases</h1><p>Prepare a package, review it together, accept it and release the exact same contents.</p></div>${B('Create release package', 'new-package', 'primary', 'plus')}</div><div class="release-progress"><strong>Create package</strong>${I('chevron')}<span>Review changes</span>${I('chevron')}<span>Accept</span>${I('chevron')}<span>Release</span>${I('chevron')}<span>Deploy to environments</span></div><div class="freeze-note">${I('lock')}<div><strong>Every package pins its root versions permanently.</strong><small>Requested changes become a new package in the same release. Earlier packages remain available in its history.</small></div></div><div class="env-controls"><div class="segmented">${[['all', 'All'], ['review', 'In review'], ['released', 'Released']].map(([key, label]) => B(label, 'release-filter', filter === key ? 'active' : '', '', `data-filter="${key}"`)).join('')}</div><span class="grow"></span><span class="hint">Complete workspace packages</span></div><article class="card"><div class="manifest-wrap"><table class="manifest-table release-table"><thead><tr><th>Release</th><th>Current package</th><th>Review status</th><th></th></tr></thead><tbody>${filter === 'all' || (filter === 'released' ? s === 'Released' : s !== 'Released') ? `<tr><td><strong>v${escapeText(releaseVersion)} · ${escapeText(releaseTitle)}</strong><span class="subline">#${releaseId} · author Local</span></td><td><strong>Package ${current().number}</strong><span class="subline"><code>${current().digest}</code> · 15 pinned roots</span></td><td>${badge(s)}<span class="subline">${['Accepted', 'Released'].includes(s) ? '1 independent acceptance' : '1 independent acceptance required'}</span></td><td>${B(s === 'Released' ? 'View release' : 'Open package', 'open-package', 'small')}</td></tr>` : ''}${filter !== 'review' ? `<tr><td><strong>v1.3.0 · Previous release</strong><span class="subline">Retained baseline</span></td><td><strong>Package 1</strong><span class="subline"><code>f0b39be8…079b</code> · fixed manifest</span></td><td>${badge('Released')}<span class="subline">Accepted independently</span></td><td>${B('Package details', 'baseline-details', 'small')}</td></tr>` : ''}</tbody></table></div></article><p class="hint" style="margin-top:18px">Release state and per-environment deployment state are tracked separately. Simulated environments are clearly labelled.</p></section>`;
  }
  changes = () => `<section class="page release-page">${steps(1)}<div class="page-heading"><div class="grow"><h1>${correcting ? 'Review correction draft' : 'Changes'}</h1><p>${correcting ? `Release #${releaseId} · based on exact package ${current().number}.` : 'Inspect a saved workspace revision before freezing a release package.'}</p></div>${B(correcting ? 'Create replacement package' : 'Create release package', correcting ? 'replacement-dialog' : 'new-package', 'primary', 'arrow')}</div><div class="compare-bar"><span class="muted">Compare</span><strong>${correcting ? 'Package ' + current().number : 'Last release · v1.3.0'}</strong>${I('arrow')}<strong>${correcting ? 'Saved correction draft' : 'Saved workspace · 4ae7b2c'}</strong><span class="grow"></span><span class="pill neutral">${correcting ? '1 corrected root' : '3 changed roots'}</span></div><div class="freeze-note">${I('lock')}<div><strong>${correcting ? 'Earlier package contents stay unchanged.' : 'A package is frozen before review begins.'}</strong><small>${correcting ? 'Only the corrected root receives a new version. The other 14 root versions are carried forward exactly.' : 'All root versions, references, resources and dependencies are pinned. Unsaved edits are excluded.'}</small></div></div>${correcting ? `<article class="card"><div class="card-head">ServiceTest<span class="grow"></span><span class="pill neutral">Corrected</span></div><div class="line-diff"><div><div class="diff-title">Package ${current().number} · pinned v${current().roots[2].version}</div><div class="number-line remove"><span class="ln">3</span><code>verwacht roepnaam = "${current().expected}"</code></div></div><div><div class="diff-title">Saved draft · new version on publication</div><div class="number-line add"><span class="ln">3</span><code>verwacht roepnaam = "Jeroen"</code></div></div></div><div class="package-root-note">Illustrative correction. The generated editor is a capture; no actual model was edited.</div></article>` : `<div class="diff-workspace">${files()}<div class="diff-main">${diffLines('changes')}<div class="immutable-strip">Complete workspace snapshot · all 15 root versions will be pinned</div></div></div>`}</section>`;

  function newPackageDialog() {
    modal('Create release package', `<label for="package-title">Release title</label><input id="package-title" value="Gegevensmodel update"><label for="package-version">Release version</label><input id="package-version" value="1.4.0"><label for="package-reviewer">Reviewer</label><select id="package-reviewer"><option>reviewer Local · independent</option></select><div class="selection-box">Saved workspace <code>4ae7b2c</code><br>Baseline <code>v1.3.0 · 73fd446</code><br>15 root versions · complete dependencies and resources</div><p>Creating the package freezes its contents and starts review. Later edits require a replacement package; they never change this one.</p><p class="hint">Design preview only. No package is created in ALEF.</p>`, 'Create package & start review', 'confirm-new-package');
  }
  handle = (action, button) => {
    if (action === 'new-package') { newPackageDialog(); return; }
    if (action === 'confirm-new-package') {
      releaseTitle = document.querySelector('#package-title').value.trim() || 'Gegevensmodel update';
      releaseVersion = document.querySelector('#package-version').value.trim() || '1.4.0';
      releaseId++; packages = [seal(1, '9c1f72b4…6f08', '4ae7b2c', roots)]; decisions = {1: 'In review'};
      selected = 1; section = 'diff'; correcting = false; released = false;
      closeModal(); go('release'); return;
    }
    if (action === 'all-releases') { go('releases'); return; }
    if (action === 'open-package') { selected = current().number; section = 'diff'; go('release'); return; }
    if (action === 'release-filter') { filter = button.dataset.filter; render(); return; }
    if (action === 'package-section') { section = button.dataset.section; render(); return; }
    if (action === 'package-roots-toggle') { showAllRoots = !showAllRoots; render(); return; }
    if (action === 'inspect-package') { selected = Number(button.dataset.package); section = 'roots'; render(); return; }
    if (action === 'package-request-changes') {
      if (!latest() || status() !== 'In review') return;
      const text = document.querySelector('#package-feedback')?.value.trim() || requestText;
      modal('Request changes · package ' + selected, `<label for="requested-feedback">Feedback for the author</label><textarea id="requested-feedback" class="comment-input" rows="4">${escapeText(text)}</textarea><p>Package ${selected} and all of its root versions stay frozen. The author can correct a draft based on it and create a replacement package in this release.</p>`, 'Request changes · preview', 'confirm-package-changes'); return;
    }
    if (action === 'confirm-package-changes') {
      requestText = document.querySelector('#requested-feedback').value.trim() || requestText;
      decisions[selected] = 'Changes requested'; closeModal(); render(); return;
    }
    if (action === 'prepare-replacement') {
      if (!latest() || status() !== 'Changes requested') return;
      correcting = true; correctionSaved = false; editorState = 'draft';
      go('model'); toast('Design preview: correction draft based on package ' + current().number + '. Its pinned roots stay unchanged.'); return;
    }
    if (action === 'review-correction') { go('changes'); return; }
    if (action === 'replacement-dialog') {
      if (!correcting || !correctionSaved) { toast('Save the correction draft before creating a replacement package.'); return; }
      modal('Create replacement package ' + (current().number + 1), `<p>Continue release #${releaseId} with a new immutable package.</p><div class="selection-box"><strong>ServiceTest</strong>: v${current().roots[2].version} → v${current().roots[2].version + 1}<br>14 other roots: exact pinned versions carried forward<br>Package ${current().number}: retained without any content changes</div><p>The replacement receives a new ID, digest and validation evidence. It starts with zero acceptances. Previous review decisions remain attached to the earlier package.</p>`, 'Create replacement · preview', 'confirm-replacement'); return;
    }
    if (action === 'confirm-replacement') {
      const previous = current(), n = previous.number + 1;
      const updatedRoots = previous.roots.map((r, i) => i === 2 ? Object.freeze({...r, version: r.version + 1, hash: '82ab' + n + '4ce'}) : r);
      packages.push(seal(n, 'c93a40' + n + 'd…201a', '8bc4d' + n + 'a', updatedRoots, 'Jeroen'));
      decisions[n] = 'In review'; selected = n; section = 'roots'; correcting = false; correctionSaved = false;
      closeModal(); go('release'); return;
    }
    if (action === 'accept-package') {
      if (!latest() || status() !== 'In review') return;
      decisions[selected] = 'Accepted'; render(); toast('Preview: acceptance binds this exact package and all pinned root versions.'); return;
    }
    if (action === 'reject-package') {
      if (!latest() || status() !== 'In review') return;
      decisions[selected] = 'Rejected'; render(); return;
    }
    if (action === 'release-package') {
      if (!latest() || status() !== 'Accepted') return;
      modal('Release v' + escapeText(releaseVersion), `<div class="selection-box"><strong>Package ${selected} · ${inspected().id}</strong><br><code>sha256:${inspected().digest}</code><br>15 fixed root versions · independently accepted</div><p>Release the exact accepted package. No rebuild, new serialization or refresh from the workspace occurs.</p><p>Deployment is a separate action for each selected environment.</p><p class="hint">Design preview only; no actual release or deployment is performed.</p>`, 'Release this package · preview', 'confirm-package-release'); return;
    }
    if (action === 'confirm-package-release') {
      if (!latest() || status() !== 'Accepted') return;
      decisions[selected] = 'Released'; released = true; revision = selected;
      snapshots[revision] = {digest: inspected().digest}; closeModal(); render(); return;
    }
    if (action === 'revoke-package') {
      modal('Revoke acceptance', '<p>The package and its pinned root versions remain unchanged. Revocation blocks new deliveries and rollback jobs; existing installations and history are retained.</p><p class="hint">Design preview only.</p>', 'Revoke · preview', 'confirm-package-revoke'); return;
    }
    if (action === 'confirm-package-revoke') { decisions[selected] = 'Revoked'; closeModal(); render(); return; }
    if (action === 'package-reply') { modal('Comment on package ' + selected, '<textarea class="comment-input" placeholder="Write a comment…"></textarea><p>Comments stay attached to this exact package and root version.</p>'); return; }
    if (action === 'baseline-details') { modal('v1.3.0 · retained release', '<div class="selection-box">Package 1 · <code>sha256:f0b39be8…079b</code><br>Root manifest retained · accepted independently</div><p>This is the immutable comparison baseline. Governed rollback reuses its exact package.</p>', 'View rollback preview', 'rollback'); return; }
    if (action === 'deploy-env') {
      const canDeploy = decisions[current().number] === 'Released';
      modal('Deploy release · simulation', `<span class="pill warn">SIMULATED</span><label style="margin-top:15px">Released package</label><select>${canDeploy ? `<option>v${escapeText(releaseVersion)} · package ${current().number} · ${current().digest}</option>` : ''}<option>v1.3.0 · retained release · f0b39be8…079b</option></select>${!canDeploy ? '<p>The current candidate is unavailable until it is accepted and released.</p>' : ''}<label>Target environments</label><div class="selection-box">${environments.map(e => `<label><input type="checkbox" ${e.type === 'production' ? 'disabled' : e.name === button.dataset.environment ? 'checked' : ''}>${escapeText(e.name)}${e.type === 'production' ? ' · prerequisites pending' : ''}</label>`).join('')}</div><p>Independent jobs, identical package bytes. Each target rechecks acceptance, permissions and its own gates.</p><p class="hint">Preview only; no operation is submitted and no ALEF services execute.</p>`); return;
    }
    // Only editor and environment controls from revision 02 are reused.
    const retained = ['toggle-tree','toggle-dock','dock-inspector','dock-diagnostics','more','save','lock','resume','reconnect','cancel-load','select-file','go-environments','env-filter','add-environment','confirm-environment','configure-env','gates','delivery','reconcile','rollback','close-modal','new-root','root','undo','redo','menu-action'];
    if (retained.includes(action)) originalHandle(action, button);
  };
  document.addEventListener('change', e => {
    if (e.target.matches('[aria-label="Inspect package"]')) { selected = Number(e.target.value); render(); }
  });
  const route = location.hash.slice(1);
  if (['pulls', 'release'].includes(route)) { view = 'releases'; detail = true; location.hash = 'release'; }
  render();
})();
