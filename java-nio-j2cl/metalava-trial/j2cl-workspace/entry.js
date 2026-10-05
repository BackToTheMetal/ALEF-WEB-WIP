goog.module('metalava.trial');
const TrialProbe = goog.require('trial.TrialProbe');
goog.global['metalavaTrialResult'] = TrialProbe.run();
