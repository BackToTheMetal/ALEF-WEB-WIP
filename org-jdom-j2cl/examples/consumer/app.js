goog.module('example.app');
const Consumer = goog.require('example.Consumer');
goog.exportSymbol('runConsumer', () => Consumer.run());
