package com.rockstargames.hal;

import android.widget.ProgressBar;

public class andSpinner extends andView {
    static int staticCount = 0;

    public andSpinner(int handle) {
        super(handle);
        setView(new andSpinnerImpl());
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andSpinner createView(int handle) {
        return new andSpinner(handle);
    }

    protected class andSpinnerImpl extends ProgressBar {
        public andSpinnerImpl() {
            super(ActivityWrapper.getActivity());
            setIndeterminate(true);
        }
    }
}