package com.reports.cordova.plugins;
 
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Reader extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try {
		    if ("test".equals(action)) { 
		             JSONObject arg_object = args.getJSONObject(0);
		             
		       cordova.getThreadPool().execute(new Runnable() {
                   public void run() {
                       // Main Code goes here
                       callbackContext.success(arg_object);
                   }
               });
		       return true;
		    }
		    callbackContext.error("Invalid action");
		    return false;
		} catch(Exception e) {
		    System.err.println("Exception: " + e.getMessage());
		    callbackContext.error(e.getMessage());
		    return false;
		} 
	}
}
/*
cordova.getThreadPool().execute(new Runnable() {
    public void run() {
        // Main Code goes here
        callbackContext.success();
    }
});
cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        // Main Code goes here
                        callbackContext.success();
                    }
                }
*/