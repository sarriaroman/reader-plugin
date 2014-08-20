cordova.define("reports/cordova/plugins/reader", function(require, exports, module) {
    var exec = require('cordova/exec');

    var callbacks = {
        empty: function () {},
        err: function (err) {
            console.error(err);
        }
    };

    function successCallback(arg) {
        var callback = callbacks[arg.event || 'empty'] || callbacks.empty;
        callback.call(undefined, arg);
    }

    function errorCallback(err) {
        callbacks.err.call(undefined, err);
    }

    var Reader = function() {
        exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Reader', // mapped to our native Java class called "Calendar"
            'init', // with this action name
            []
        );
    };

    //noinspection JSUnusedGlobalSymbols
    Reader.prototype.on = function(event, callback) {
        callbacks[event] = callback;
    };

    //noinspection JSUnusedGlobalSymbols
    Reader.prototype.off = function(event) {
        callbacks[event] = undefined;
    };

    module.exports = new Reader();
});