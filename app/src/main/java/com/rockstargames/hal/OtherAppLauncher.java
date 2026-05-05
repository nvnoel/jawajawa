package com.rockstargames.hal;

import android.content.Intent;
import android.net.Uri;
import java.util.Locale;

public class OtherAppLauncher {
    private static final InternationalVersion[] PLAY_gta3 = {new InternationalVersion("com.rockstar.gta3", null), new InternationalVersion("com.rockstar.gta3ger", "DE"), new InternationalVersion("com.rockstar.gta3jpn", "JP"), new InternationalVersion("com.rockstar.gta3aus", "AU")};
    private static final InternationalVersion[] PLAY_vc = {new InternationalVersion("com.rockstargames.gtavc", null), new InternationalVersion("com.rockstargames.gtavcger", "DE")};

    /* JADX INFO: Access modifiers changed from: private */
    public enum Market {
        PLAY("market://details?id="),
        AMAZON("amzn://apps/android?asin=");

        public final String prefix;

        Market(String prefix_) {
            this.prefix = prefix_;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static class InternationalVersion {
        public String locale;
        public String marketIdentifier;
        public String packageIdentifer;

        public InternationalVersion(String p, String l) {
            this.packageIdentifer = p;
            this.locale = l;
            this.marketIdentifier = p;
        }

        public InternationalVersion(String p, String m, String l) {
            this.packageIdentifer = p;
            this.locale = l;
            this.marketIdentifier = m;
        }
    }

    private static InternationalVersion[] getVersions(Market market, String packageId, String marketId) {
        if (market == Market.PLAY && packageId.equalsIgnoreCase(PLAY_gta3[0].packageIdentifer)) {
            return PLAY_gta3;
        }
        return (market == Market.PLAY && packageId.equalsIgnoreCase(PLAY_vc[0].packageIdentifer)) ? PLAY_vc : new InternationalVersion[]{new InternationalVersion(packageId, marketId, null)};
    }

    public static void openAppOrStorePage(String whichMarket, String packageID, String marketID) {
        Market market = Market.valueOf(whichMarket);
        InternationalVersion[] versions = getVersions(market, packageID, marketID);
        Intent intent = null;
        for (InternationalVersion iv : versions) {
            intent = ActivityWrapper.getActivity().getPackageManager().getLaunchIntentForPackage(iv.packageIdentifer);
            if (intent != null) {
                break;
            }
        }
        if (intent != null) {
            ActivityWrapper.getActivity().startActivity(intent);
            return;
        }
        String locale = Locale.getDefault().getCountry();
        int i = 1;
        while (true) {
            if (i < versions.length) {
                if (!locale.equalsIgnoreCase(versions[i].locale)) {
                    i++;
                } else {
                    marketID = versions[i].marketIdentifier;
                    break;
                }
            } else {
                break;
            }
        }
        Intent i2 = new Intent("android.intent.action.VIEW");
        i2.setData(Uri.parse(market.prefix + marketID));
        ActivityWrapper.getActivity().startActivity(i2);
    }
}