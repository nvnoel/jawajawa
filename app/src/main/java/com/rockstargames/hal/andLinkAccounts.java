package com.rockstargames.hal;

import android.content.Intent;
import android.net.Uri;

public class andLinkAccounts {
    public static void LinkAccount(String url) {
        Intent i = new Intent("android.intent.action.VIEW");
        i.setData(Uri.parse(url));
        ActivityWrapper.getActivity().startActivity(i);
    }
}