package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

public class UserHandler {

	boolean checkUserExists(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {
			Path path = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
			return (path.toFile().exists());
		}
	}

	boolean createAnUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {
			try {
				System.out.println("Création de l'utilisateur " + username);
				Files.createDirectories(
						Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username));
			} catch (IOException e) {
				System.err.println("... échec :");
				e.printStackTrace();
				return false;
			}
			return (Files.exists(Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username)));
		}
	}

	boolean removeUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		} else {

			Path rootPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);

			try (Stream<Path> paths = Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)) {
				System.out.println("Suppressions :");
				paths.sorted(Comparator.reverseOrder()).map(Path::toFile).peek(System.out::println)
						.forEach(File::delete);
				return true;
			} catch (IOException e) {
				System.err.println("Erreur lors de la suppression de " + rootPath.toString());
				e.printStackTrace();
				return false;
			}
		}
	}
}
