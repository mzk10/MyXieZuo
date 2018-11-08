package com.meng.hui.android.xiezuo.util.http;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

class HttpRequest extends AsyncTask<String, Integer, Object> {

    private final Map<String, Object> params;
    private final HttpRequester.OnRequestListener listener;
    private final String url;
    private final int method;
    private static final String TAG = "HttpRequest";
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 4000;

    protected HttpRequest(
            String url,
            Map<String, Object> params,
            int method,
            HttpRequester.OnRequestListener listener
    ) {
        this.url = url;
        this.method = method;
        this.params = params;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... strings) {
        String result = doHttp(method, url, params);
        //LogUtils.i(TAG, "result=" + result);
        if (listener!=null){
            return listener.onRequestInBackground(result);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (listener!=null){
            //listener.onRequestCallback(result);
            listener.onRequest(result);
            //LogUtils.i(TAG, "result=" + result);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private String doHttp(int method, String url, Map<String, Object> params) {
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            boolean isSsl = false;
            if ("https".equals(scheme)) {
                isSsl = true;
            }

            if (method == HttpRequester.METHOD_GET) {
                if (params!=null && params.size()>0){
                    String getParams = makeGetParams(params);
                    url = url + "?" + getParams;
                }
            }
            URL u = new URL(url);
            if (isSsl) {
                TrustManager myX509TrustManager = new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(
                            X509Certificate[] chain,
                            String authType
                    ) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(
                            X509Certificate[] chain,
                            String authType
                    ) throws CertificateException {
                    }
                };
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                kmf.init(trustStore, "password".toCharArray());
                HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                // 设置忽略证书
                conn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                // 设置SSLContext
                SSLContext sslcontext = SSLContext.getInstance("SSL", "AndroidOpenSSL");
                sslcontext.init(kmf.getKeyManagers(), new TrustManager[]
                        {
                                myX509TrustManager
                        }, new java.security.SecureRandom());

                // 设置套接工厂
                conn.setSSLSocketFactory(sslcontext.getSocketFactory());
                conn.setInstanceFollowRedirects(true);
                if (method == HttpRequester.METHOD_GET) {
                    return getResult(conn);
                } else if (method == HttpRequester.METHOD_POST) {
                    return postResult(conn, params);
                }
            } else {
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setInstanceFollowRedirects(true);
                if (method == HttpRequester.METHOD_GET) {
                    return getResult(conn);
                } else if (method == HttpRequester.METHOD_POST) {
                    return postResult(conn, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String makeGetParams(Map<String, Object> params) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = params.keySet();
        Iterator<String> iterator = keySet.iterator();
        if (iterator.hasNext()) {
            String key = iterator.next();
            String value = String.valueOf(params.get(key));
            sb.append(key);
            sb.append("=");
            sb.append(URLEncoder.encode(value, HttpRequester.URL_ENCODE));
        }
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = String.valueOf(params.get(key));
            sb.append("&");
            sb.append(key);
            sb.append("=");
            sb.append(URLEncoder.encode(value, HttpRequester.URL_ENCODE));
        }
        return sb.toString();
    }

    private String getResult(URLConnection conn) throws IOException {
        conn.setDoInput(true);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        return getString(conn);
    }

    private String postResult(URLConnection conn, Map<String, Object> params) throws IOException {
        conn.setDoInput(true);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        //OutputStreamWriter osw = new OutputStreamWriter(os, HttpRequester.URL_ENCODE);
        String getParams = makeGetParams(params);
        byte[] data = getParams.getBytes(HttpRequester.URL_ENCODE);
        //osw.write(getParams);
        os.write(data);
        return getString(conn);
    }

    @NonNull
    private String getString(URLConnection conn) throws IOException {
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, HttpRequester.URL_ENCODE);
        int len;
        char[] buf = new char[1024];
        StringBuilder sb = new StringBuilder();
        while ((len = isr.read(buf)) != -1) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

}