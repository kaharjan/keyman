/**
 * Copyright (C) 2017 SIL International. All rights reserved.
 */

package com.tavultesoft.kmea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tavultesoft.kmea.data.Dataset;
import com.tavultesoft.kmea.data.Keyboard;
import com.tavultesoft.kmea.data.adapters.AdapterFilter;
import com.tavultesoft.kmea.data.adapters.NestedAdapter;
import com.tavultesoft.kmea.util.FileUtils;
import com.tavultesoft.kmea.util.MapCompat;

final class KMKeyboardPickerAdapter extends NestedAdapter<Keyboard, Dataset.Keyboards, Void> implements OnClickListener {
  private final static int KEYBOARD_LAYOUT_RESOURCE = R.layout.list_row_layout3;

  private static class ViewHolder {
    TextView textLang;
    TextView textKbd;
    ImageButton imgDetails;
  }

  protected Typeface listFont;

  public KMKeyboardPickerAdapter(final Context context, Dataset.Keyboards adapter) {
    // TODO:  (13.0) Swap the inline AdapterFilter definition out for Dataset.keyboardPickerSorter
    //        once KeyboardPickerActivity is sufficiently refactored.  (All references to keyboardList
    //        should instead refer to this adapter.)
    super(context, KEYBOARD_LAYOUT_RESOURCE, adapter, new AdapterFilter<Keyboard, Dataset.Keyboards, Void>() {

      // Yeah, so this is a MASSIVE hack.  Right now, it's either this or refactor up to 60
      // separate references to keyboardList within KeyboardPickerActivity.  Yikes.
      public List<Keyboard> selectFrom(Dataset.Keyboards adapter, Void dummy) {
        List<HashMap<String, String>> kbdMapList = KeyboardPickerActivity.getKeyboardsList(context);
        List<Keyboard> kbdList = new ArrayList<>(kbdMapList.size());

        for(HashMap<String, String> kbdMap: kbdMapList) {
          boolean isCustom = kbdMap.containsKey(KMManager.KMKey_CustomKeyboard) &&
            kbdMap.get(KMManager.KMKey_CustomKeyboard).equals("Y");
          boolean isNewKeyboard = kbdMap.containsKey(KeyboardPickerActivity.KMKEY_INTERNAL_NEW_KEYBOARD) &&
            kbdMap.get(KeyboardPickerActivity.KMKEY_INTERNAL_NEW_KEYBOARD).equals(KeyboardPickerActivity.KMKEY_INTERNAL_NEW_KEYBOARD);

          Keyboard k = new Keyboard(
            kbdMap.get(KMManager.KMKey_PackageID),
            kbdMap.get(KMManager.KMKey_KeyboardID),
            kbdMap.get(KMManager.KMKey_KeyboardName),
            kbdMap.get(KMManager.KMKey_LanguageID),
            kbdMap.get(KMManager.KMKey_LanguageName),
            isCustom,
            isNewKeyboard,
            MapCompat.getOrDefault(kbdMap, KMManager.KMKey_Font, null),
            MapCompat.getOrDefault(kbdMap, KMManager.KMKey_OskFont, null),
            MapCompat.getOrDefault(kbdMap, KMManager.KMKey_Version, "1.0"),
            MapCompat.getOrDefault(kbdMap, KMManager.KMKey_CustomHelpLink, "")
          );
          kbdList.add(k);
        }

        return kbdList;
      }
    }, null);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Keyboard kbd = getItem(position);
    ViewHolder holder;

    // If we're being told to reuse an existing view, do that.  It's automatic optimization.
    if(convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(KEYBOARD_LAYOUT_RESOURCE, parent, false);
      holder = new ViewHolder();

      holder.textLang = convertView.findViewById(R.id.text1);
      holder.textKbd = convertView.findViewById(R.id.text2);
      holder.imgDetails = convertView.findViewById(R.id.imageButton1);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    if(kbd.isNewKeyboard())
      holder.textLang.setText(String.format("[%s]", getContext().getText(R.string.keyboard_picker_new_keyboard_prefix))
        + " " + kbd.getLanguageName());
    else
      holder.textLang.setText(kbd.getLanguageName());
    holder.textKbd.setText(kbd.getResourceName());

    if (listFont != null) {
      holder.textLang.setTypeface(listFont, Typeface.BOLD);
      holder.textKbd.setTypeface(listFont, Typeface.NORMAL);
    }

    if (holder.textKbd.getText().toString().equals(holder.textLang.getText().toString())) {
      holder.textKbd.setVisibility(View.INVISIBLE);
    }

    holder.imgDetails.setTag(kbd);
    holder.imgDetails.setOnClickListener(this);
    return convertView;
  }

  @Override
  public boolean areAllItemsEnabled() {
    return true;
  }

  @Override
  public void onClick(View v) {
    Keyboard kbInfo = (Keyboard) v.getTag();
    Intent i = new Intent(this.getContext(), KeyboardInfoActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    i.putExtra(KMManager.KMKey_Keyboard, kbInfo);
    KeyboardInfoActivity.titleFont = listFont;
    this.getContext().startActivity(i);
  }
}
