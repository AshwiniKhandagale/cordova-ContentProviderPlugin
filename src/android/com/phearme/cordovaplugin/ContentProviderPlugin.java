package com.phearme.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.content.ContentValues;
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
		}
		if (action.equals("insert")) {
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
		ContentValues Data = new ContentValues();
        
		try {
			if (!queryArgs.isNull("contentUri")) {
				contentUri = Uri.parse(queryArgs.getString("contentUri"));
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
				Data.put("data", queryArgs.getString("data"));
				//Data = queryArgs.getString("data");
			}else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e1) {
			callback.error(WRONG_PARAMS);
			return;
		}
		if (Data == null) {
			callback.error(WRONG_PARAMS);
			return;
		}
		// run query
		Cursor result = cordova.getActivity().getContentResolver().insert(contentUri, Data);
		resultJSONArray = new JSONArray();
		
		// Some providers return null if an error occurs, others throw an exception
		if(result == null) {
			callback.error(UNKNOWN_ERROR);
		} else {
			try {
				
			} finally {
				if(result != null) result.close();
	        }
			callback.success(resultJSONArray);
		}	
	}
	private void runQuery(JSONObject queryArgs, CallbackContext callback) {
		Uri contentUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		JSONArray resultJSONArray;

		try {
			if (!queryArgs.isNull("contentUri")) {
				contentUri = Uri.parse(queryArgs.getString("contentUri"));
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
			if (!queryArgs.isNull("projection")) {
				JSONArray projectionJsonArray = queryArgs.getJSONArray("projection");
				projection = new String[projectionJsonArray.length()];
				for (int i = 0; i < projectionJsonArray.length(); i++) {
					projection[i] = projectionJsonArray.getString(i);
				}
			}
		} catch (JSONException e1) {
			projection = null;
		}

		try {
			if (!queryArgs.isNull("selection")) {
				selection = queryArgs.getString("selection");
			}
		} catch (JSONException e1) {
			selection = null;
		}

		try {
			if (!queryArgs.isNull("selectionArgs")) {
				JSONArray selectionArgsJsonArray = queryArgs.getJSONArray("selectionArgs");
				selectionArgs = new String[selectionArgsJsonArray.length()];
				for (int i = 0; i < selectionArgsJsonArray.length(); i++) {
					selectionArgs[i] = selectionArgsJsonArray.getString(i);
				}
			}
		} catch (JSONException e1) {
			selectionArgs = null;
		}

		try {
			if (!queryArgs.isNull("sortOrder")) {
				sortOrder = queryArgs.getString("sortOrder");
			}
		} catch (JSONException e1) {
			sortOrder = null;
		}

		// run query
		Cursor result = cordova.getActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, sortOrder);
		resultJSONArray = new JSONArray();
		
		// Some providers return null if an error occurs, others throw an exception
		if(result == null) {
			callback.error(UNKNOWN_ERROR);
		} else {
		
			try {
		
				while (result != null && result.moveToNext()) {
					JSONObject resultRow = new JSONObject();
					int colCount = result.getColumnCount();
					for (int i = 0; i < colCount; i++) {
						try {
							resultRow.put(result.getColumnName(i), result.getString(i));
						} catch (JSONException e) {
							resultRow = null;
						}
					}
					resultJSONArray.put(resultRow);
				}
			} finally {
				if(result != null) result.close();
	        	}
		
			callback.success(resultJSONArray);
			
		}	
	}
}
