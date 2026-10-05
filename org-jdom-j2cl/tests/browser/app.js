goog.module('mpsxml.app');
const Entry = goog.require('org.mpsxml.BrowserEntry');
goog.exportSymbol('runXmlContracts', () => Entry.run());
/**
 * @param {string} xml
 * @return {?string}
 */
function roundTrip(xml) { return Entry.roundTrip(xml); }
goog.exportSymbol('roundTripMpsXml', roundTrip);
