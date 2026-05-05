package com.rockstargames.hal;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;

public class PresenceConnection implements Runnable {
    private byte[] data;
    private andHttp http;
    private InputStream inputStream;
    private HttpRequestBase request;

    public PresenceConnection(andHttp http, HttpRequestBase request, byte[] data) {
        this.http = http;
        this.request = request;
        this.data = data;
        http.setPresenceConnection(this);
    }

    public void cancel() {
        try {
            this.inputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Header[] allHeaders;
        int readAmount = 0;
        andHttp.log("Connecting presence system...");
        InputStream input = null;
        URLConnection connection = null;
        try {
            try {
                connection = this.request.getURI().toURL().openConnection();
                for (Header h : this.request.getAllHeaders()) {
                    connection.setRequestProperty(h.getName(), h.getValue());
                }
                connection.setDoInput(true);
                if (this.data != null && this.data.length > 0) {
                    connection.setDoOutput(true);
                    OutputStream output = connection.getOutputStream();
                    output.write(this.data);
                    output.close();
                }
                try {
                    final HttpURLConnection huc = (HttpURLConnection) connection;
                    final StringBuilder sb = new StringBuilder();
                    Map<String, List<String>> headers = huc.getHeaderFields();
                    for (String headerName : headers.keySet()) {
                        List<String> values = headers.get(headerName);
                        for (String headerValue : values) {
                            sb.append(headerName + '\n' + headerValue + '\n');
                        }
                    }
                    ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                PresenceConnection.this.http.onReceivedResponse(PresenceConnection.this.http.getId(), huc.getResponseCode(), huc.getResponseMessage(), sb.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                input = connection.getInputStream();
                this.inputStream = input;
                byte[] buffer = new byte[102400];
                while (true) {
                    try {
                        readAmount = input.read(buffer);
                    } catch (EOFException e) {
                        if (this.http.isCancelled()) {
                            andHttp.log("Presence system cancelling...");
                        } else {
                            andHttp.log("Presence system shut down from remote end...");
                        }
                    } catch (SocketTimeoutException e2) {
                        if (this.http.isCancelled()) {
                            andHttp.log("Presence system cancelling...");
                            break;
                        }
                        andHttp.log("Presence system still alive...");
                    }
                    if (readAmount == -1) {
                        andHttp.log("Presence system disconnected by remote end.");
                        break;
                    } else if (readAmount > 0) {
                        final byte[] finalBuffer = new byte[readAmount];
                        System.arraycopy(buffer, 0, finalBuffer, 0, readAmount);
                        ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.2
                            @Override // java.lang.Runnable
                            public void run() {
                                int length = finalBuffer.length;
                                PresenceConnection.this.http.onReceivedData(PresenceConnection.this.http.getId(), finalBuffer, length);
                            }
                        });
                    }
                }
                ActivityWrapper.getLayout().postDelayed(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.3
                    @Override // java.lang.Runnable
                    public void run() {
                        PresenceConnection.this.http.onConnectionFinished(PresenceConnection.this.http.getId());
                    }
                }, 500L);
                if (input != null) {
                    try {
                        input.close();
                        andHttp.log("Presence system successfully closed input stream.");
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                if (connection != null) {
                }
                andHttp.removeConnection(this.http.getId());
            } catch (Exception e4) {
                ActivityWrapper.getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.PresenceConnection.4
                    @Override // java.lang.Runnable
                    public void run() {
                        PresenceConnection.this.http.onError(PresenceConnection.this.http.getId(), 0);
                    }
                });
                e4.printStackTrace();
                if (input != null) {
                    try {
                        input.close();
                        andHttp.log("Presence system successfully closed input stream.");
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                if (connection != null) {
                }
                andHttp.removeConnection(this.http.getId());
            }
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                    andHttp.log("Presence system successfully closed input stream.");
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            if (connection != null) {
            }
            andHttp.removeConnection(this.http.getId());
            throw th;
        }
    }
}