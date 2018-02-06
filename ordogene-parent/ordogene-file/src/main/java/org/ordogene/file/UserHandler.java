package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserHandler {

	public boolean checkUserExists(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {
			Path path = Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username);
			return (Files.exists(path));
		}
	}

	public boolean createAnUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {
			try {
				System.out.println("Création de l'utilisateur "+username);
				Files.createDirectories(Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username));
			} catch (IOException e) {
				System.err.println("... échec :");
				e.printStackTrace();
				return false;
			}
			return (Files.exists(Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username)));
		}
	}
}
