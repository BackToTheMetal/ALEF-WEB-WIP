goog.module('webawt.dual_showcase.app');

const BrowserLauncher = goog.require('org.webawt.samples.dualshowcase.BrowserLauncher');

goog.exportSymbol('webawtDualShowcaseMount', BrowserLauncher.mount);
goog.exportSymbol('webawtDualShowcaseUnmount', BrowserLauncher.unmount);
goog.exportSymbol('webawtDualShowcaseSelectedPage', BrowserLauncher.selectedPage);
goog.exportSymbol('webawtDualShowcaseStatusText', BrowserLauncher.statusText);
goog.exportSymbol('webawtDualShowcaseRunSelfCheck', BrowserLauncher.runSelfCheck);
goog.exportSymbol('webawtDualShowcaseKeyboardTrace', BrowserLauncher.keyboardTrace);
goog.exportSymbol('webawtDualShowcaseKeyboardSelection', BrowserLauncher.keyboardSelection);
goog.exportSymbol('webawtDualShowcaseSelectPage', BrowserLauncher.selectPage);
goog.exportSymbol('webawtDualShowcaseShowPopupMenu', BrowserLauncher.showPopupMenu);

goog.exportSymbol('webawtDualShowcaseFontMeasurements', BrowserLauncher.fontMeasurements);

goog.exportSymbol("webawtDualShowcaseTextLayoutDiagnostics", BrowserLauncher.textLayoutDiagnostics);

goog.exportSymbol('webawtDualShowcasePerformanceDiagnostics', BrowserLauncher.performanceDiagnostics);
goog.exportSymbol('webawtDualShowcaseResourceDiagnostics', BrowserLauncher.resourceDiagnostics);
goog.exportSymbol('webawtDualShowcaseQueueThenUnmount', BrowserLauncher.queueThenUnmount);
