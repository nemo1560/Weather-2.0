package com.example.nemo1.weather21.model;

import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttp {
    private static String json = null;
    private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final long MCC_TIMEOUT = 5 * 60 * 1000; /* 5 seconds */
    private static int SOCKET_TIMEOUT = 5 * 60 * 1000;

    public OkHttp() {

    }
    public static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }
                }
        };
    }

    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = null;
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getWrappedTrustManagers(tmf.getTrustManagers()), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return HttpsURLConnection.getDefaultSSLSocketFactory();
        }
    }

    private static OkHttpClient okHttpClient(){
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SOCKET_TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(getSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        return hv.verify(hostname,session);
                    }
                });
        return okHttpClient;
    }

    public static String getOKHttp (String url, Map<String, String> paramaters) throws IOException {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if(paramaters != null && paramaters.size() > 0){
            for (String key : paramaters.keySet()) {
                if (paramaters.get(key) != null) {
                    builder.addQueryParameter(key, paramaters.get(key).toString());
                }
            }
        }
        Request request = new Request.Builder()
                .url(builder.build())
                .build();
        return okHttpClient().newCall(request).execute().body().string();
    }

    public static String postOKHttp (String url, Map<String, String> paramaters) throws IOException {
        RequestBody requestBody = null;
        MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(paramaters != null && paramaters.size() > 0){
            for (String key : paramaters.keySet()) {
                if (paramaters.get(key) != null) {
                    body.addFormDataPart(key, URLEncoder.encode((String)paramaters.get(key), "utf-8"));
                }
            }
        }
        requestBody = body.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return okHttpClient().newCall(request).execute().body().string();
    }

    public static String loginByParameter(String url, Map<String, String> paramaters) throws IOException {
        RequestBody requestBody = null;

        MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(paramaters != null){
            for(String key: paramaters.keySet()){
                body.addFormDataPart(key, URLEncoder.encode((String)paramaters.get(key), "utf-8"));
            }
            requestBody = body.build();
        }
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return okHttpClient().newCall(request).execute().body().string();
    }

    public static String loginByJson (String url, JSONObject login ) throws IOException {
        RequestBody requestBody = null;
        requestBody = RequestBody.create(MediaType.parse("application/json"),login.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return okHttpClient().newCall(request).execute().body().string();
    }

    public static String postFile(String url, Map<String, String> paramaters, File file){
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if(paramaters != null && paramaters.size() > 0){
            for (String key : paramaters.keySet()) {
                if (paramaters.get(key) != null) {
                    builder.addQueryParameter(key, paramaters.get(key).toString());
                }
            }
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("","",RequestBody.create(MediaType.parse("image/jpg"),file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("LoiOkHttp",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("OkHttpOK",response.toString());
            }
        });
        return json;
    }

}
