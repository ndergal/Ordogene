package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Const {

	private final static Map<String, String> resourcesMap;
	private final static Map<String, String> unmodifiableResourcesMap;

	static {

		URL currentUrl = Const.class.getProtectionDomain().getCodeSource().getLocation();
		URI currentUri;
		URI configUri;
		try {
			currentUri = currentUrl.toURI();
			configUri = currentUri.resolve(currentUri.getPath() + /*File.separator +*/ "ordogene.conf.json");
			System.out.println("Loading "+configUri.toString());
		} catch (URISyntaxException e2) {
			System.err.println(
					"Error while retrieving the configuration file... Looking in " + System.getProperty("user.home"));
			try {
				configUri = new URI(System.getProperty("user.home") + '/' + "ordogene.conf.json");
			} catch (URISyntaxException e) {
				System.err.println("Error while looking for the current user folder. Application will shutdown");
				configUri = null;
				System.exit(0);
			}
			// e2.printStackTrace();
		}

		Map<String, String> tmpResourcesMap;

		ObjectMapper objectMapper = new ObjectMapper();
		try (InputStream configFile = configUri.toURL().openStream()) {
			tmpResourcesMap = objectMapper.readValue(configFile, new TypeReference<HashMap<String, String>>() {
			});

		} catch (IOException e1) {
			// e1.printStackTrace();
			System.err.println("Error : the configuration file is missing or invalid. The application will fail.");
			tmpResourcesMap = new HashMap<>();
		}

		resourcesMap = tmpResourcesMap;
		unmodifiableResourcesMap = Collections.unmodifiableMap(resourcesMap);
		String appliPath = resourcesMap.get("ApplicationPath");
		if (appliPath == null) {
			System.err.println("Error : 'ApplicationPath' is missing config.json");
		} else {
			try {
				Files.createDirectories(Paths.get(appliPath));
			} catch (IOException e) {
				System.err.println("Error while creating the directory " + appliPath);
				e.printStackTrace();
			}
		}

	}

	public static Map<String, String> getConst() {
		return unmodifiableResourcesMap;
	}
}
