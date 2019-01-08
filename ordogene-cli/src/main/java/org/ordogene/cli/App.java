package org.ordogene.cli;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.ordogene.cli.errorHandle.ErrorHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class App {
	private static String addr;
	private static final Logger log = LoggerFactory.getLogger(App.class);

	@Bean
	public PromptProvider myPromptProvider() {
		return () -> new AttributedString("ordogene:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}

	@Bean
	public RestTemplate myRestTemplate(RestTemplateBuilder builder) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);

		RestTemplate rt = builder.rootUri(addr).build();
		rt.setErrorHandler(new ErrorHandle());
		rt.setRequestFactory(requestFactory);
		return rt;
	}

	public static void main(String[] args) {
		SimpleCommandLinePropertySource ps = new SimpleCommandLinePropertySource(args);
		if (!ps.containsProperty("url")) {
			log.error("Missing argument --url=<server_url>");
			return;
		}
		addr = (String) ps.getProperty("url");
		if (addr.isEmpty()) {
			log.error("URL is empty");
			return;
		}
		SpringApplication.run(App.class, args);
	}
}
