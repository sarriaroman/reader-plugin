var Reader = {
    test: function(str, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Reader', // mapped to our native Java class called "Calendar"
            'test', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "title": str
            }]
        ); 
     }
}

module.exports = Reader;