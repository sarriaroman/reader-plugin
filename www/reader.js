cordova.define("reports/cordova/plugins/reader", function(require, exports, module) {
    var exec = require('cordova/exec');

    var Reader = function() {};

    Reader.prototype.test = function(str, successCallback, errorCallback) {
        exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Reader', // mapped to our native Java class called "Calendar"
            'init', // with this action name
            []
        );
    };

    module.exports = new Reader();
});