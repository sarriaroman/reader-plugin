package com.reports.cordova.plugins;
 
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.content.Context;

//Import for reader SDK
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeHandler;
import com.imagpay.SwipeListener;

import java.lang.String;

public class Reader extends CordovaPlugin {
    private static final String TAG = "CardReaderPlugin";
    private SwipeHandler _handler;
    //private Handler _uiHandler;
    private boolean done_init;
    private CallbackContext callback = null;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try {
		    if ("init".equals(action)) {
		       //JSONObject arg_object = args.getJSONObject(0);
               initDevice(callbackContext);
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

	/*public void echo(final JSONObject obj, final CallbackContext callbackContext) {
	    cordova.getThreadPool().execute(new Runnable() {
                           public void run() {
                               // Main Code goes here
                               callbackContext.success(obj);
                           }
                       });
	} */

    private void ret(JSONObject parameter) {
        if(callback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, parameter);
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }
    }

    private void initDevice(final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Log.d(TAG, "Init Reader Device");
                callback = callbackContext;
                if (done_init) return;

                Log.d(TAG, "get Activity");
                Context context = cordova.getActivity();//.getApplicationContext();
                Log.d(TAG, String.format("%b", (context != null)));

                Log.d(TAG, "Create swipe handler");
                _handler = new SwipeHandler(context);
                _handler.setReadonly(true);
                _handler.setDownloadEnvironment(true);
                _handler.setUploadEnvironment(true);
                //_uiHandler = new Handler(Looper.myLooper());

                Log.d(TAG, "Add swipe listener");
                _handler.addSwipeListener(new SwipeListener() {
                    @Override
                    public void onReadData(SwipeEvent event) {
                    }

                    @Override
                    public void onParseData(final SwipeEvent event) {
                        //String result = event.getValue();
                        // hex string message
                        //sendMessage("Final(16)=>% " + result);
                        cordova.getThreadPool().execute(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject parameter = new JSONObject();
                                    parameter.put("event", "read");

                                    String[] tmps = event.getValue().split(" ");
                                    StringBuilder sbf = new StringBuilder();
                                    for (String str : tmps) {
                                        sbf.append((char) Integer.parseInt(str, 16));
                                    }

                                    String decoded = sbf.toString();
                                    parameter.put("raw", decoded);
                                    int len = decoded.length();

                                    if (len > 30) {
                                        String card1 = decoded.substring(0, 17).replaceAll("[^0-9]+", "");
                                        if (card1.length() == 16) {
                                            parameter.put("card", card1);
                                        } else {
                                            int start = decoded.indexOf(";") + 1;
                                            int fin = decoded.indexOf("=");
                                            if (start > 0 && fin > 0 && start < fin) {
                                                String card = decoded.substring(start, fin);
                                                parameter.put("card", card);
                                            }
                                        }
                                    } else {
                                        decoded = decoded.replaceAll("[^0-9]+", "");
                                        if (decoded.length() == 16) {
                                            parameter.put("card", decoded);
                                        }
                                    }
                                    ret(parameter);
                                } catch (JSONException e) {
                                    //TODO add retError
                                }
                            }
                        });

                        /*String[] tmps = event.getValue().split(" ");
                        StringBuilder sbf = new StringBuilder();
                        for (String str : tmps) {
                            sbf.append((char) Integer.parseInt(str, 16));
                        }
                        if (cl != null) {
                            cl.onCardRead(sbf.toString().replaceAll("[^0-9]+", ""));
                        }*/
                    }

                    @Override
                    public void onDisconnected(SwipeEvent event) {
                        //sendMessage("Device is disconnected!");
                        Log.d(TAG, "Reader Device is disconnected!");
                        try {
                            JSONObject parameter = new JSONObject();
                            parameter.put("event", "disconnect");
                            ret(parameter);
                        } catch (JSONException e) {
                            //TODO add retError
                        }
                    }

                    @Override
                    public void onConnected(SwipeEvent event) {
                        //sendMessage("Device is connected!");
                        Log.d(TAG, "Reader Device is connected!");
                        try {
                            JSONObject parameter = new JSONObject();
                            parameter.put("event", "connect");
                            try {
                                if (!_handler.isConnected()) {
                                    Log.d(TAG, "Device is not connected!");
                                    return;
                                }
                                if (_handler.isPowerOn()) {
                                    return;
                                }
                                if (_handler.isReadable()) {
                                    _handler.powerOn();
                                    ret(parameter);
                                } else {
                                    Log.d(TAG, "Please wait! testing parameter now");
                                    if (_handler.test() && _handler.isReadable()) {
                                        Log.d(TAG, "Device is ready");
                                        _handler.powerOn();
                                        ret(parameter);
                                    } else {
                                        Log.d(TAG, "Device is not supported or Please insert device and try again!");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO add retError
                            }
                        } catch (JSONException e) {
                            //TODO add retError
                        }
                    }

                    @Override
                    public void onStarted(SwipeEvent event) {
                        Log.d(TAG, "started");
                    }

                    @Override
                    public void onStopped(SwipeEvent event) {
                        Log.d(TAG, "stopped");
                    }
                });
                done_init = true;
                Log.d(TAG, "Create ret object");
                try {
                    JSONObject parameter = new JSONObject();
                    parameter.put("event", "init");
                    parameter.put("status", "ok");
                    ret(parameter);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
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