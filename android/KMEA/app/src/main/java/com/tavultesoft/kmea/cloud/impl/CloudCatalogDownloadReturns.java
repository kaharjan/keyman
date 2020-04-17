package com.tavultesoft.kmea.cloud.impl;

import com.tavultesoft.kmea.cloud.CloudApiTypes;
import com.tavultesoft.kmea.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import android.util.Log;

/**
 * Result type for catalogue download.
 */
public class CloudCatalogDownloadReturns {
  private static final String TAG = "CloudReturns";
  public JSONObject keyboardJSON;
  public JSONArray lexicalModelJSON;

  // Used by the CloudCatalogDownloadTask, as it fits well with doInBackground's param structure.
  public CloudCatalogDownloadReturns(List<CloudApiTypes.CloudApiReturns> returns) {
    JSONObject kbd = new JSONObject();
    JSONArray lex = null;

    //TODO: Seems to be wrong because only the last result for each type will be processed
    for(CloudApiTypes.CloudApiReturns ret: returns) {
      switch(ret.target) {
        case Keyboard:
          String keyboardID;
          try {
            String kmpLink = ret.jsonObject.getString("kmp");
            String filename = FileUtils.getFilename(kmpLink);
            keyboardID = filename.substring(0, filename.indexOf(FileUtils.KEYMANPACKAGE));
            kbd.put(keyboardID, ret.jsonObject);
           } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e);
          }
        break;
        case LexicalModels:
          lex = ret.jsonArray;
        break;
      }
    }

    // Errors are thrown if we try to do this assignment within the loop.
    this.keyboardJSON = kbd;
    this.lexicalModelJSON = lex;
  }

  public CloudCatalogDownloadReturns(JSONObject keyboardJSON, JSONArray lexicalModelJSON) {
    this.keyboardJSON = keyboardJSON; // TODO: Add keyboard ID
    this.lexicalModelJSON = lexicalModelJSON;
  }

  public boolean isEmpty() {
    boolean emptyKbd = keyboardJSON == null || keyboardJSON.length() == 0;
    boolean emptyLex = lexicalModelJSON == null || lexicalModelJSON.length() == 0;

    return emptyKbd && emptyLex;
  }
}
