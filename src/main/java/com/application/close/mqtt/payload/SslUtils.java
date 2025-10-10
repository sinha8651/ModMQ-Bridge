package com.application.close.mqtt.payload;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SslUtils {

	public static SSLSocketFactory getSocketFactory(String caCrtFile, String crtFile, String keyFile, String password) {

		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream(keyFile), password.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, password.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init((KeyStore) null);

			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			return context.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create SSL Socket Factory: " + e.getMessage(), e);
		}
	}

}
