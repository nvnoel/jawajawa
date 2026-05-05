package com.rockstargames.hal;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.util.StringTokenizer;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class andHttp {
    private static SparseArray<andHttp> connections = new SparseArray<>();
    private boolean cancelled = false;
    private int id;
    private PresenceConnection presenceConnection;
    private HttpRequestBase request;

    public native void onConnectionFinished(int i);

    public native void onError(int i, int i2);

    public native void onReceivedData(int i, byte[] bArr, int i2);

    public native void onReceivedResponse(int i, int i2, String str, String str2);

    public static void log(String l) {
    }

    public static void HEAD(int id, String url) {
        log("HEAD: " + url);
        processRequest(id, new HttpHead(url));
    }

    public static void GET(int id, String url, String headers) {
        log("GET: " + url);
        HttpGet get = new HttpGet(url);
        log("Header string: " + headers);
        StringTokenizer tokenizer = new StringTokenizer(headers, ":\n");
        while (tokenizer.hasMoreTokens()) {
            String key = tokenizer.nextToken();
            String value = tokenizer.nextToken();
            log("    " + key + ": " + value);
            get.addHeader(key, value);
        }
        processRequest(id, get);
    }

    public static void POST(int id, String url, String headers, byte[] data) {
        log("POST: " + url);
        HttpPost post = new HttpPost(url);
        log("Header string: " + headers);
        StringTokenizer tokenizer = new StringTokenizer(headers, ":\n");
        while (tokenizer.hasMoreTokens()) {
            String key = tokenizer.nextToken();
            String value = tokenizer.nextToken();
            log("    " + key + ": " + value);
            post.addHeader(key, value);
        }
        if (!url.contains("Presence.asmx/WaitMessage")) {
            log("with data:");
            logDataSafe(data);
            post.setEntity(new ByteArrayEntity(data));
            processRequest(id, post);
            return;
        }
        andHttp http = new andHttp(id, post);
        addConnection(id, http);
        new Thread(new PresenceConnection(http, post, data), "Presence Connection Thread").start();
    }

    public static void cancelConnection(int id) {
        andHttp connection = getConnection(id);
        if (connection != null) {
            log("Cancelling connection " + id);
            connection.cancelled = true;
            if (connection.presenceConnection != null) {
                connection.presenceConnection.cancel();
            }
            removeConnection(id);
            return;
        }
        log("Can't find connection " + id + " to cancel.");
    }

    public static void logDataSafe(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length && i < 4096; i++) {
            char c = (char) data[i];
            if (c != '\r') {
                if (c == '\n') {
                    log("    " + sb.toString());
                    sb.setLength(0);
                } else if ((c < ' ' && c != '\t') || c >= 127) {
                    sb.append("{" + c + "}");
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 100) {
                log("    " + sb.toString());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) {
            log("    " + sb.toString());
            if (data.length > 4096) {
                log("    ...plus " + (data.length - 4096) + " more bytes.");
            }
        }
    }

    private static synchronized void addConnection(int id, andHttp connection) {
        synchronized (andHttp.class) {
            connections.put(id, connection);
        }
    }

    public static synchronized void removeConnection(int id) {
        synchronized (andHttp.class) {
            connections.remove(id);
        }
    }

    private static synchronized andHttp getConnection(int id) {
        andHttp andhttp;
        synchronized (andHttp.class) {
            andhttp = connections.get(id);
        }
        return andhttp;
    }

    private static void processRequest(int id, HttpRequestBase request) {
        andHttp http = new andHttp(id, request);
        addConnection(id, http);
        http.execute();
    }

    public andHttp(int id, HttpRequestBase request) {
        this.id = id;
        this.request = request;
    }

    public int getId() {
        return this.id;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setPresenceConnection(PresenceConnection p) {
        this.presenceConnection = p;
    }

    public void execute() {
        onPreExecute();
        new Thread(new Runnable() { // from class: com.rockstargames.hal.andHttp.1
            @Override // java.lang.Runnable
            public void run() {
                final Pair<HttpResponse, byte[]> result = andHttp.this.doInBackground();
                ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.andHttp.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        andHttp.this.onPostExecute(result);
                    }
                });
            }
        }).start();
    }

    protected void onPreExecute() {
        Header[] allHeaders;
        log("Starting HTTP request: " + this.request.getRequestLine());
        for (Header h : this.request.getAllHeaders()) {
            log("    " + h.getName() + ": " + h.getValue());
        }
    }

    protected Pair<HttpResponse, byte[]> doInBackground() {
        Header[] allHeaders;
        try {
            BasicHttpParams basicHttpParams = new BasicHttpParams();
            if (!this.request.getRequestLine().getUri().contains("Presence.asmx/WaitMessage")) {
                HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
                HttpConnectionParams.setSoTimeout(basicHttpParams, 30000);
                HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, true);
            } else {
                HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, true);
            }
            DefaultHttpClient client = new DefaultHttpClient(basicHttpParams);
            log("Executing request...");
            org.apache.http.HttpResponse response = client.execute(this.request);
            log("Response obtained.");
            log("Status line: " + response.getStatusLine());
            for (Header h : response.getAllHeaders()) {
                log("    " + h.getName() + " : " + h.getValue());
            }
            HttpEntity ent = response.getEntity();
            byte[] data = ent != null ? EntityUtils.toByteArray(ent) : new byte[0];
            logDataSafe(data);
            if (this.cancelled) {
                return null;
            }
            return new Pair<>(response, data);
        } catch (Exception ex) {
            Log.e("HTTP", "Exception!", ex);
            ActivityWrapper.handleException(ex);
            return null;
        }
    }

    protected void onPostExecute(Pair<HttpResponse, byte[]> result) {
        Header[] allHeaders;
        if (result == null) {
            onError(this.id, -1);
        } else {
            StatusLine status = ((HttpResponse) result.first).getStatusLine();
            StringBuilder headers = new StringBuilder();
            for (Header head : ((HttpResponse) result.first).getAllHeaders()) {
                headers.append(head.getName() + '\n' + head.getValue() + '\n');
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ((byte[]) result.second).length && i < 1024; i++) {
                byte b = ((byte[]) result.second)[i];
                if (b >= 32 && b < 128) {
                    sb.append((char) b);
                } else {
                    sb.append((int) b);
                    sb.append(" ");
                }
                if (sb.length() > 100) {
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) {
                sb.setLength(0);
            }
            onReceivedResponse(this.id, status.getStatusCode(), status.getReasonPhrase(), headers.toString());
            onReceivedData(this.id, (byte[]) result.second, ((byte[]) result.second).length);
            onConnectionFinished(this.id);
        }
        removeConnection(getId());
    }
}