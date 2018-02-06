package org.ordogene.file;

public class FileService {

	private final UserHandler uh = new UserHandler();

	public boolean userExist(String username) {
		return uh.checkUserExists(username);
	}

	public boolean addUser(String username) {
		return uh.createAnUser(username);
	}

	public boolean removeUser(String username) {
		return uh.removeUser(username);
	}

}
