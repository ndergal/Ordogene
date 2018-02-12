package org.ordogene.file.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Const {

	private static Map<String, String> resourcesMap;

	public static boolean loadConfig(String configFilePath) {

		System.out.println("Loading " + configFilePath);

		Map<String, String> tmpResourcesMap;

		ObjectMapper objectMapper = new ObjectMapper();
		try (FileInputStream fis = new FileInputStream(configFilePath)) {
			tmpResourcesMap = objectMapper.readValue(fis, new TypeReference<HashMap<String, String>>() {
			});

		} catch (IOException e1) {
			// e1.printStackTrace();
			System.err.println("Error : the configuration file is missing or invalid. The application will fail.");
			tmpResourcesMap = new HashMap<>();
			return false;
		}
		if (!tmpResourcesMap.isEmpty()) {
			System.out.println("Configuraiton well loaded !");
		} else {
			System.err.println("Configuraiton not loaded...");
			return false;
		}
		resourcesMap = tmpResourcesMap;
		String appliPath = resourcesMap.get("ApplicationPath");
		if (appliPath == null) {
			System.err.println("Error : 'ApplicationPath' is missing the config file.");
		} else {
			try {
				Files.createDirectories(Paths.get(appliPath));
			} catch (IOException e) {
				System.err.println("Error while creating the directory " + appliPath);
				e.printStackTrace();
			}
		}
		return true;

	}

	public static Map<String, String> getConst() {
		return Collections.unmodifiableMap(resourcesMap);
	}
}
 
//  