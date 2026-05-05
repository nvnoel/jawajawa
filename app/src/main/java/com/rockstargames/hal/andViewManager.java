package com.rockstargames.hal;

import android.util.Log;
import com.rockstargames.gtasa.GTASA;
import java.util.Iterator;

public class andViewManager {
    public static andView root = null;
    private static long spam_delay = 0;
    private static int uniqueID = 0;

    public static void staticExitSocialClub() {
        GTASA.staticExitSocialClub();
    }

    public static void staticEnterSocialClub() {
        GTASA.staticEnterSocialClub();
    }

    public static void addViewToScreen(andView v) {
        if (v != null) {
            try {
                root = v;
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        ActivityWrapper.getLayout().addView(v.getOuterView());
    }

    public static void removeViewFromScreen(andView v) {
        ActivityWrapper.getLayout().removeView(v.getOuterView());
    }

    public static void printLayout() {
        if (root != null) {
            long time = System.currentTimeMillis();
            Log.i("andViewManager", "Printing handle hierarchy...");
            root.printHierarchy(0);
            spam_delay = 1000 + time;
        }
    }

    public static void invalidateHierarchy() {
        Log.w("andViewManager", "Invalidating hierarchy!");
        if (root != null) {
            root.invalidateHierarchy();
        }
    }

    public static int genID() {
        int i = uniqueID;
        uniqueID = i + 1;
        return i;
    }

    public static void setLandscape(boolean landscape) {
        ActivityWrapper.setTransitioning(true);
        ActivityWrapper.getActivity().setRequestedOrientation(landscape ? 6 : 7);
    }

    public static String getStaticCounts() {
        int attachedViews = countViewsRecursive(root);
        return "V: " + attachedViews + "/" + andView.staticCount + " S: " + andScreen.staticCount + " IV: " + andImageView.staticCount + " B: " + andButton.staticCount + " L: " + andLabel.staticCount + " SV: " + andScrollView.staticCount + " T: " + andTable.staticCount + " TI: " + andTextInput.staticCount + " WV: " + andWebView.staticCount + " CP: " + andColourPicker.staticCount + " DV: " + andDrawingView.staticCount + " SP: " + andSpinner.staticCount;
    }

    private static int countViewsRecursive(andView v) {
        if (v == null) {
            return 0;
        }
        int count = 1;
        if (v.children == null) {
            return 1;
        }
        Iterator<andView> it = v.children.iterator();
        while (it.hasNext()) {
            andView c = it.next();
            count += countViewsRecursive(c);
        }
        return count;
    }
}