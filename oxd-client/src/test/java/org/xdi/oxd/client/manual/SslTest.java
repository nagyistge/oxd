package org.xdi.oxd.client.manual;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 21/07/2016
 */

public class SslTest {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, IOException {
        String url = "https://sso.lobosstudios.com/.well-known/openid-configuration";

        HttpClient httpclient = createHttpClientTrustAll();

        HttpResponse response = httpclient.execute(new HttpGet(url));
        System.out.println(response.getStatusLine().getStatusCode());
    }

    public static HttpClient createHttpClientTrustAll() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }, new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }
        );

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, sf));
        ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
        return new DefaultHttpClient(ccm);
    }
}