package com.wardrumstudios.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

public class WarHttp {
    private static boolean AddLineFeeds = false;
    private int timeoutSeconds = 3;

    protected WarHttp(WarBase activity) {
    }

    public void AddHttpGetLineFeeds(boolean value) {
        AddLineFeeds = value;
    }

    public void SetHttpTimeout(int seconds) {
    }

    public String HttpPost(String url) {
        try {
            DefaultHttpClient tolerantClient = getTolerantClient();
            HttpConnectionParams.setConnectionTimeout(tolerantClient.getParams(), this.timeoutSeconds * 1000);
            HttpConnectionParams.setSoTimeout(tolerantClient.getParams(), this.timeoutSeconds * 1000);
            HttpPost request = new HttpPost();
            List<NameValuePair> nameValuePair = new ArrayList<>(2);
            nameValuePair.add(new BasicNameValuePair("username", "eeewardrum@gmail.com"));
            nameValuePair.add(new BasicNameValuePair("password", "*"));
            URI website = new URI(url);
            request.setURI(website);
            try {
                request.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpResponse response = tolerantClient.execute(request);
            Log.e("log_tag", "HttpPost send " + url);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                instream.close();
                Log.e("log_tag", "HttpPost " + result);
                return result;
            }
        } catch (Exception e2) {
            Log.e("log_tag", "Error in http connection " + e2.toString());
        }
        Log.e("log_tag", "return blank string");
        return "";
    }

    public String HttpGet(String url) {
        try {
            DefaultHttpClient tolerantClient = getTolerantClient();
            HttpConnectionParams.setConnectionTimeout(tolerantClient.getParams(), this.timeoutSeconds * 1000);
            HttpConnectionParams.setSoTimeout(tolerantClient.getParams(), this.timeoutSeconds * 1000);
            HttpGet request = new HttpGet();
            URI website = new URI(url);
            request.setURI(website);
            HttpResponse response = tolerantClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                instream.close();
                Log.e("log_tag", "HttpGet " + result);
                return result;
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        Log.e("log_tag", "return blank string");
        return "";
    }

    public byte[] HttpGetData(String url) {
        try {
            DefaultHttpClient tolerantClient = getTolerantClient();
            HttpConnectionParams.setConnectionTimeout(tolerantClient.getParams(), this.timeoutSeconds * 1000);
            HttpConnectionParams.setSoTimeout(tolerantClient.getParams(), this.timeoutSeconds * 1000);
            HttpGet request = new HttpGet();
            URI website = new URI(url);
            request.setURI(website);
            HttpResponse response = tolerantClient.execute(request);
            HttpEntity entity = response.getEntity();
            Log.e("log_tag", "entity len=" + entity.getContentLength() + "getContentType=" + entity.getContentType());
            if (entity != null) {
                InputStream instream = entity.getContent();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                while (true) {
                    int bytesRead = instream.read(b);
                    if (bytesRead != -1) {
                        bos.write(b, 0, bytesRead);
                    } else {
                        byte[] byteArray = bos.toByteArray();
                        String result = convertStreamToString(instream);
                        instream.close();
                        Log.e("log_tag", "HttpGet " + result);
                        return byteArray;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        Log.e("log_tag", "return blank string");
        return null;
    }

    public DefaultHttpClient getTolerantClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client.getConnectionManager().getSchemeRegistry().getScheme("https").getSocketFactory();
        X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
        if (!(delegate instanceof MyVerifier)) {
            sslSocketFactory.setHostnameVerifier(new MyVerifier(delegate));
        }
        return client;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder("");
        while (true) {
            try {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        sb.append(line);
                        if (AddLineFeeds) {
                            sb.append("\n");
                        }
                    } else {
                        try {
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        try { is.close(); } catch(Exception e) {}
        Log.e("log_tag", "convertStreamToString " + sb.toString());
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public class MyVerifier extends AbstractVerifier {
        private final X509HostnameVerifier delegate;

        public MyVerifier(X509HostnameVerifier delegate) {
            this.delegate = delegate;
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            boolean ok = false;
            try {
                this.delegate.verify(host, cns, subjectAlts);
            } catch (SSLException e) {
                for (String cn : cns) {
                    if (cn.startsWith("*.")) {
                        try {
                            if (cn.substring(2).equals("onmodulus.net")) {
                            }
                            this.delegate.verify(host, new String[]{cn.substring(2)}, subjectAlts);
                            ok = true;
                        } catch (Exception e2) {
                        }
                    }
                }
                if (!ok) {
                    throw e;
                }
            }
        }
    }
}