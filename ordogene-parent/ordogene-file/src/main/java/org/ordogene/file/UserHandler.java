package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class UserHandler {

	boolean checkUserExists(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {
			Path path = Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username);
			return (Files.exists(path));
		}
	}

	boolean createAnUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {
			try {
				System.out.println("Création de l'utilisateur " + username);
				Files.createDirectories(
						Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username));
			} catch (IOException e) {
				System.err.println("... échec :");
				e.printStackTrace();
				return false;
			}
			return (Files.exists(Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username)));
		}
	}

	boolean removeUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {

			Path rootPath = Paths.get(Const.resourcesMap.get("ApplicationPath") + File.separatorChar + username);
			try {
				System.out.println("Suppressions :");
				Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile)
						.peek(System.out::println).forEach(File::delete);
				return true;
			} catch (IOException e) {
				System.err.println("Erreur lors de la suppression de " + rootPath.toString());
				e.printStackTrace();
				return false;
			}
		}
	}
}
