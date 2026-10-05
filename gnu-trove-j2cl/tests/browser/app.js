goog.module('trovej2cl.app');
const Entry = goog.require('org.trovej2cl.BrowserEntry');
/**
 * @param {number} seed
 * @return {?string}
 */
function run(seed) { return Entry.run(seed); }
goog.exportSymbol('runTroveContracts', run);
goog.exportSymbol('runTroveRegressions', () => Entry.regressions());
