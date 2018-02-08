package org.ordogene.file;

import java.io.File;
import java.io.IOException;
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

		ClassLoader classLoader = Const.class.getClassLoader();
		URL configFileUrl = classLoader.getResource("config.json");
		Path configFilePath;
		Map<String, String> tmpResourcesMap;
		byte[] mapData = null;
		try {
			configFilePath = Paths.get(configFileUrl.toURI());
			mapData = Files.readAllBytes(configFilePath);
		} catch (URISyntaxException | IOException e1) {
			System.err.println("Error : the file resources/config.json is absent or invalid.");
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			tmpResourcesMap = objectMapper.readValue(mapData, new TypeReference<HashMap<String, String>>() {
			});
		} catch (IOException e) {
			System.err.println("Error : the file resources/config.json is not valid.");
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
