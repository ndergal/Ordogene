package org.ordogene.file;

import java.security.SecureRandom;
import java.util.List;

import org.ordogene.file.utils.Calculation;

public class FileService {

	private final UserHandler uh = new UserHandler();
	private final CalculationHandler ch = new CalculationHandler();

	public boolean userExist(String username) {
		return uh.checkUserExists(username);
	}

	public boolean addUser(String username) {
		return uh.createAnUser(username);
	}

	public boolean removeUser(String username) {
		return uh.removeUser(username);
	}

	public String generateRandomUserId(int nbChar) {

		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(nbChar);
		for (int i = 0; i < nbChar; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
	
	public List<Calculation> getUserCalculations(String username){
		return ch.getCallculations(username);
	}

}
