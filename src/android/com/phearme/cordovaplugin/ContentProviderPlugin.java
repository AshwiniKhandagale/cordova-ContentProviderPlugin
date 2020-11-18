package com.phearme.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.net.Uri;

public class ContentProviderPlugin extends CordovaPlugin {
	private String WRONG_PARAMS = "Wrong parameters.";
	private String UNKNOWN_ERROR = "Unknown error.";

	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		final JSONArray methodArgs = args;
		final CallbackContext callback = callbackContext;
		
		if (action.equals("query")) {
			final JSONObject queryArgs = methodArgs.getJSONObject(0);
			if (queryArgs == null) {
				callback.error(WRONG_PARAMS);
				return false;
			}
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					runQuery(queryArgs, callback);
				}
			});
			return true;
		}else if(action.equals("insert")){
       final JSONObject queryArgs = methodArgs.getJSONObject(0);
			if (queryArgs == null) {
				callback.error(WRONG_PARAMS);
				return false;
			}
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					insertQuery(queryArgs, callback);
				}
			});
			return true;
		}
		return false;
	}

	private void insertQuery(JSONObject queryArgs, CallbackContext callback) {
		Uri contentUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		JSONArray resultJSONArray;

		try {
			if (!queryArgs.isNull("contentUri")) {
				contentUri = Uri.parse(queryArgs.getString("contentUri")+"/1");
			} else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e) {
			callback.error(WRONG_PARAMS);
			return;
		}
		if (contentUri == null) {
			callback.error(WRONG_PARAMS);
			return;
		}
      try { 
	   Cursor result = cordova.getActivity().getContentResolver().query(contentUri, null, null, null, null);  
	    if (result.moveToFirst()) { 
		try { 
		String userData = result.getString(result.getColumnIndex("data"));  
		 callback.success(userData);
		} catch (Exception e) { 
		  }
		}
      }catch (Exception e){ e.printStackTrace();
		   }
	}
		private void runQuery(JSONObject queryArgs, CallbackContext callback) {
		Uri contentUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		JSONArray resultJSONArray;
		String data = null ;

		try {
			if (!queryArgs.isNull("contentUri")) {
				contentUri = Uri.parse(queryArgs.getString("contentUri")+"/1");
			} else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e) {
			callback.error(WRONG_PARAMS);
			return;
		}
		if (contentUri == null) {
			callback.error(WRONG_PARAMS);
			return;
		}

		try {
			if (!queryArgs.isNull("data")) {
			data = queryArgs.getString("data");
				
			}
		} catch (JSONException e1) {
			data = null;
		}

	try { 

	  ContentValues values = new cordova.getActivity().ContentValues();
	   values.put("data", data); 
	   cordova.getActivity().getContentResolver().insert(contentUri, values);
	   callback.success(values);
      }catch (Exception e){ e.printStackTrace();
		   }
	}
}