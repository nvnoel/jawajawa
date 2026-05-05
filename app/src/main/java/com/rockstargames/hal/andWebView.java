package com.rockstargames.hal;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class andWebView extends andView {
    static int staticCount = 0;

    public native void onLoaded(int i);

    public andWebView(int handle) {
        super(handle);
        setView(new andWebViewImp(this));
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public void setUrl(String url) {
        getImpl().setUrl(url);
    }

    public void navigateBack() {
        getImpl().goBack();
    }

    public boolean canNavigateBack() {
        return getImpl().canGoBack();
    }

    public void navigateForward() {
        getImpl().goForward();
    }

    public boolean canNavigateForward() {
        return getImpl().canGoForward();
    }

    public void refresh() {
        getImpl().reload();
    }

    public static andWebView createView(int handle) {
        return new andWebView(handle);
    }

    private andWebViewImp getImpl() {
        return (andWebViewImp) getView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class andWebViewImp extends WebView {
        andWebView parent;
        private String url;

        @SuppressLint({"SetJavaScriptEnabled"})
        public andWebViewImp(andWebView p) {
            super(ActivityWrapper.getActivity());
            this.parent = null;
            setId(andViewManager.genID());
            this.parent = p;
            setWebViewClient(new WebViewClient() { // from class: com.rockstargames.hal.andWebView.andWebViewImp.1
                @Override // android.webkit.WebViewClient
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                    Log.i("andWebView", "Loading resource: " + url);
                }

                @Override // android.webkit.WebViewClient
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Log.i("andWebView", "Page started: " + url);
                }

                @Override // android.webkit.WebViewClient
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    andWebViewImp.this.parent.onLoaded(andWebViewImp.this.parent.handle);
                    Log.i("andWebView", "Page finished: " + url);
                }

                @Override // android.webkit.WebViewClient
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Log.i("andWebView", "Received error " + errorCode + ": " + description + " (" + failingUrl + ")");
                }
            });
            getSettings().setJavaScriptEnabled(true);
            getSettings().setBuiltInZoomControls(true);
            getSettings().setSupportZoom(true);
            setInitialScale(80);
            getSettings().setUseWideViewPort(true);
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            loadUrl(this.url);
        }

        public void navigateBack() {
            goBack();
        }

        public boolean canNavigateBack() {
            return canGoBack();
        }

        public void navigateForward() {
            goForward();
        }

        public boolean canNavigateForward() {
            return canGoForward();
        }

        public void refresh() {
            reload();
        }
    }
}