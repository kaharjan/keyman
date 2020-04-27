package com.tavultesoft.kmea.data;

import android.os.Bundle;
import android.util.Log;

import com.tavultesoft.kmea.KMKeyboardDownloaderActivity;
import com.tavultesoft.kmea.KMManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LexicalModel extends LanguageResource implements Serializable {
  private static final String TAG = "lexicalModel";

  // Needed to build download bundle
  private String modelURL;

  public LexicalModel(JSONObject lexicalModelJSON, String modelURL) {
    try {
      this.packageID = lexicalModelJSON.optString(KMManager.KMKey_PackageID, KMManager.KMDefault_UndefinedPackageID);

      this.resourceID = lexicalModelJSON.getString(KMManager.KMKey_ID);

      this.resourceName = lexicalModelJSON.getString(KMManager.KMKey_Name);

      // language ID and language name from lexicalModelJSON
      Object obj = lexicalModelJSON.getJSONArray("languages");
      if (((JSONArray) obj).get(0) instanceof String) {
        // language name not provided so re-use language ID
        this.languageID = lexicalModelJSON.getJSONArray("languages").getString(0).toLowerCase();
        this.languageName = languageID;
      } else if (((JSONArray) obj).get(0) instanceof JSONObject) {
        JSONObject languageObj = lexicalModelJSON.getJSONArray("languages").getJSONObject(0);
        this.languageID = languageObj.getString(KMManager.KMKey_ID).toLowerCase();
        this.languageName = languageObj.getString(KMManager.KMKey_Name);
      }

      this.version = lexicalModelJSON.optString(KMManager.KMKey_KeyboardVersion, "1.0");
      this.helpLink = ""; // TOODO: Handle help links
      this.modelURL = modelURL;
    } catch (JSONException e) {
      Log.e(TAG, "Lexical model exception parsing JSON: " + e);
    }
  }

  public LexicalModel(String packageID, String lexicalModelID, String lexicalModelName, String languageID, String languageName,
                      String version, String helpLink,
                      String modelURL) {

    this.packageID = (packageID != null) ? packageID : KMManager.KMDefault_UndefinedPackageID;
    this.resourceID = lexicalModelID;
    this.resourceName = lexicalModelName;
    this.languageID = languageID.toLowerCase();
    this.languageName = languageName;
    this.version = (version != null) ? version : "1.0";
    this.helpLink = ""; // TODO: Handle help links
    this.modelURL = modelURL;
  }

  public String getLexicalModelID() { return getResourceID(); }
  public String getLexicalModelName() { return getResourceName(); }

  public Bundle buildDownloadBundle() {
    Bundle bundle = new Bundle();

    // Make sure we have an actual download URL.  If not, we can't build a proper download bundle -
    // the downloader conditions on this URL's existence in 12.0!
    if(modelURL == null) {
      return null;
    } else if (modelURL.equals("")) {
      return null;
    }

    bundle.putString(KMKeyboardDownloaderActivity.ARG_PKG_ID, packageID);
    bundle.putString(KMKeyboardDownloaderActivity.ARG_MODEL_ID, resourceID);
    bundle.putString(KMKeyboardDownloaderActivity.ARG_LANG_ID, languageID);
    bundle.putString(KMKeyboardDownloaderActivity.ARG_MODEL_NAME, resourceName);
    bundle.putString(KMKeyboardDownloaderActivity.ARG_LANG_NAME, languageName);
    bundle.putString(KMKeyboardDownloaderActivity.ARG_MODEL_URL, modelURL);

    bundle.putString(KMKeyboardDownloaderActivity.ARG_CUSTOM_HELP_LINK, helpLink);

    return bundle;
  }

  public boolean equals(Object obj) {
    if(obj instanceof LexicalModel) {
      boolean lgCodeMatch = ((LexicalModel) obj).getLanguageID().equals(this.getLanguageID());
      boolean idMatch = ((LexicalModel) obj).getLexicalModelID().equals(this.getLexicalModelID());

      return lgCodeMatch && idMatch;
    }

    return false;
  }

}
