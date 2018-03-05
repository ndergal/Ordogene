package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

public class UserManager {
	private final static Logger log = LoggerFactory.getLogger(UserManager.class);

	/**
	 * check if the given user exist on the server
	 * 
	 * @param username
	 *            : name of the user to check
	 * @return true if the user exists, false else.
	 */
	public boolean checkUserExists(String username) {
		if (username == null || username.isEmpty()) {
			return false;
		} else {
			Path path = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
			return (path.toFile().exists());
		}
	}

	/**
	 * add an user on the server
	 * 
	 * @param username
	 *            : name of the user to add
	 * @return true if the user has been added, false else.
	 */
	public boolean createAnUser(String username) {
		if (username == null || username.isEmpty()) {
			return false;
		} else {
			Path newUserPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
			if(newUserPath.toFile().exists()) {
				return false;
			}
			try {
				log.info("Create new user {}", username);
				Files.createDirectories(newUserPath);
			} catch (IOException e) {
				log.error("... failed :");
				log.debug(Arrays.toString(e.getStackTrace()));
				return false;
			}
			return (Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username).toFile().exists());
		}
	}

	/**
	 * remove an user on the server
	 * 
	 * @param username
	 *            : name of the user to add
	 * @return true if the user has been added, false else.
	 */
	public boolean removeUser(String username) {
		if (username == null || username.equals("")) {
			return false;
		}

		File todelete = new File(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
		if(!todelete.exists()) {
			return false;
		}
		try(Stream<Path> paths = Files.walk(todelete.toPath())) {
		    paths.sorted(Comparator.reverseOrder())
		    .map(Path::toFile)
		    .forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
