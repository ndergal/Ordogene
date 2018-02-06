package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Const {

	public final static Map<String, String> resourcesMap;

	static {

		ClassLoader classLoader = Const.class.getClassLoader();
		File file = new File(classLoader.getResource("config.json").getFile());
		URL pathUrl = classLoader.getResource("config.json");
		Path appPath;
		Map<String, String> tmpResourcesMap;
		byte[] mapData = null;
		try {
			appPath = Paths.get(pathUrl.toURI());
			mapData = Files.readAllBytes(appPath);
		} catch (URISyntaxException | IOException e1) {
			System.err.println("Erreur : le fichier resources/config.json absent ou erroné.");
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			tmpResourcesMap = objectMapper.readValue(mapData, new TypeReference<HashMap<String, String>>() {
			});
		} catch (IOException e) {
			System.err.println("Erreur : le fichier resources/config.json invalide.");
			tmpResourcesMap = new HashMap<>();
		}
		resourcesMap = tmpResourcesMap;
	}
}
