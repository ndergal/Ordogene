package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.ordogene.file.utils.Const;

public class UserManager {

	boolean checkUserExists(String username) {
		if (username == null || username.isEmpty()) {
			return false;
		} else {
			Path path = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
			return (path.toFile().exists());
		}
	}

	boolean createAnUser(String username) {
		if (username == null || username.isEmpty()) {
			return false;
		} else {
			Path newUserPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
			if(Files.exists(newUserPath)) {
				return false;
			}
			try {
				System.out.println("Create new user " + username);
				Files.createDirectories(newUserPath);
			} catch (IOException e) {
				System.err.println("... failed :");
				e.printStackTrace();
				return false;
			}
			return (Files.exists(Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username)));
		}
	}

	boolean removeUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		}

		File todelete = new File(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
		if(!todelete.exists()) {
			return false;
		}
		try {
			Files.walk(todelete.toPath())
		    .sorted(Comparator.reverseOrder())
		    .map(Path::toFile)
		    .forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
