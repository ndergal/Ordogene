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

	private final static Map<String, String> resourcesMap;
	private final static Map<String, String> unmodifiableResourcesMap;

	static {

		PermissionCollection permCollection = Const.class.getProtectionDomain().getPermissions();
		String location = null;
		if (permCollection != null) {
			Enumeration<Permission> en = permCollection.elements();
			while (en.hasMoreElements() && location == null) {
				Permission perm = en.nextElement();
				if (perm.toString().startsWith("(\"java.io.FilePermission\"")) {
					location = perm.getName();
				}
			}
		}
		if (location.endsWith("-")) {
			location = location.substring(0, location.length() - 1);
		}

		URI configUri;

		try {
			// System.out.println("PATH !!!!!!!!! = " + location);
			if (location.endsWith(File.separator)) {
				configUri = new URI(location + /* File.separator + */ "ordogene.conf.json");
			} else {
				configUri = new URI(location + File.separator + "ordogene.conf.json");
			}
			System.out.println("Loading " + configUri.toString());
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

		//System.out.println("configUri = " + configUri);

		Map<String, String> tmpResourcesMap;

		ObjectMapper objectMapper = new ObjectMapper();
		try (FileInputStream fis = new FileInputStream(location + /* File.separator + */ "ordogene.conf.json")) {
			tmpResourcesMap = objectMapper.readValue(fis, new TypeReference<HashMap<String, String>>() {
			});

		} catch (IOException e1) {
			// e1.printStackTrace();
			System.err.println("Error : the configuration file is missing or invalid. The application will fail.");
			tmpResourcesMap = new HashMap<>();
		}
		if (!tmpResourcesMap.isEmpty()) {
			System.out.println("Configuraiton well loaded !");
		}
		else {
			System.err.println("Configuraiton not loaded...");
		}
		resourcesMap = tmpResourcesMap;
		unmodifiableResourcesMap = Collections.unmodifiableMap(resourcesMap);
		String appliPath = resourcesMap.get("ApplicationPath");
		if (appliPath == null) {
			System.err.println("Error : 'ApplicationPath' is missing ordogene.conf.json");
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
