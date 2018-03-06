package org.ordogene.file.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Class to get Const, loaded from the configuration file.
 * @author darwinners team
 *
 */
public class Const {
	private final static Logger log = LoggerFactory.getLogger(Const.class);

	private static Map<String, String> resourcesMap;

	private Const() {

	}

	/**
	 * 
	 * @param configFilePath
	 *            : path to the config file to load. The file content must be in
	 *            Json format
	 * @return true if the config file has been loaded and parsed, false otherwise.
	 */

	public static boolean loadConfig(String configFilePath) {

		Map<String, String> tmpResourcesMap;

		ObjectMapper objectMapper = new ObjectMapper();
		try (FileInputStream fis = new FileInputStream(configFilePath)) {
			tmpResourcesMap = objectMapper.readValue(fis, new TypeReference<HashMap<String, String>>() {
			});

		} catch (IOException e1) {
			log.error("Error : the configuration file is missing or invalid. The application will fail.");
			tmpResourcesMap = new HashMap<>();
			return false;
		}
		if (!tmpResourcesMap.isEmpty()) {
			log.info("Configuraiton well loaded !");
		} else {
			log.error("Configuraiton not loaded....");
			return false;
		}
		resourcesMap = tmpResourcesMap;
		String appliPath = resourcesMap.get("ApplicationPath");
		if (appliPath == null) {
			log.error("Error : 'ApplicationPath' is missing the config file.");
		} else {
			try {
				Files.createDirectories(Paths.get(appliPath));
			} catch (IOException e) {
				log.error("Error while creating the directory " + appliPath);
				log.debug(Arrays.toString(e.getStackTrace()));
			}
		}
		return true;
	}

	public static Map<String, String> getConst() {
		if (resourcesMap == null || resourcesMap.size() == 0) {
			log.error("No configuration loaded.");
			return new HashMap<>();
		}
		return Collections.unmodifiableMap(resourcesMap);

	}
}

//