package com.yang.network;

import com.yang.basic.LogUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpRequest {

    private static final String TAG = HttpRequest.class.getSimpleName();
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String SSL_CONTENT = "TLS";
    private static final int TIMEOUT = 3000;

    private static final String CHARSET_KEY = "charset";
    private static final String CHARSET_VALUE = "utf-8";

    private static final String CONTENT_LENGTH_KEY = "Content-Length";

    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    private static final String RETURN_CODE_MSG = "server network error responseCode= ";
    private static final String CONNECTEXCEPTION = "ConnectException:";
    private static final String FILENOTFOUNDEXCEPTION = "FileNotFoundException:";
    private static final String SOCKETEXCEPTION = "SocketException:";
    private static final String EXCEPTION = "Exception:";

    private static final String RW = "rw";

    public static String getRequestParams(HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            sb.append(next.getKey()).append("=").append(next.getValue());
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static void requestNetwork(final String url, final String requestParams, final HttpRequestListener
            listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = null;
                HttpURLConnection conn = null;
                BufferedReader bufferedReader = null;
                OutputStream os = null;

                LogUtils.d(TAG, "requestNetwork: url = " + url + " requestParams = " + requestParams);
                try {
                    byte[] postData = requestParams.getBytes(StandardCharsets.UTF_8);
                    int postDataLength = postData.length;
                    SSLContext sc = SSLContext.getInstance(SSL_CONTENT);
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                    conn = (HttpURLConnection) new URL(url).openConnection();

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);

                    conn.setRequestMethod(HTTP_METHOD_POST);
                    conn.setConnectTimeout(TIMEOUT);

                    conn.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
                    conn.setRequestProperty(CHARSET_KEY, CHARSET_VALUE);
                    conn.setRequestProperty(CONTENT_LENGTH_KEY, Integer.toString(postDataLength));

                    os = conn.getOutputStream();

                    os.write(requestParams.getBytes());
                    os.flush();


                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(is));
                        StringBuffer stringBuffer = new StringBuffer();
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                        json = stringBuffer.toString();
                        if (null != listener) {
                            listener.onResponse(json);
                        }
                    } else {
                        if (null != listener) {
                            listener.onErrorResponse(RETURN_CODE_MSG + responseCode);
                        }
                    }
                } catch (ConnectException connectException) {
                    if (null != listener) {
                        listener.onErrorResponse(CONNECTEXCEPTION + connectException.getMessage());
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    if (null != listener) {
                        listener.onErrorResponse(FILENOTFOUNDEXCEPTION + fileNotFoundException.getMessage());
                    }
                } catch (SocketException socketException) {
                    if (null != listener) {
                        listener.onErrorResponse(SOCKETEXCEPTION + socketException.getMessage());
                    }
                } catch (Exception exception) {
                    if (null != listener) {
                        listener.onErrorResponse(EXCEPTION + exception.getMessage());
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    private static class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
