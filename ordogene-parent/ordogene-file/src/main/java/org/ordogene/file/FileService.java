package org.ordogene.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	public List<Calculation> getUserCalculations(String username) {
		return ch.getCalculations(username);
	}

	public static void writeInFile(Object content, Path dest) throws IOException {

		if (Files.exists(dest)) {
			Files.delete(dest);
		}
		Files.createDirectories(dest.getParent());
		Files.createFile(dest);

		ObjectMapper mapper = new ObjectMapper();

		// Object to JSON in file
		mapper.writeValue(dest.toFile(), content);

	}

	public static String encodeImage(Path pathImg) throws FileNotFoundException, IOException {
		byte[] imageData = Files.readAllBytes(pathImg);
		return Base64.getEncoder().encodeToString(imageData);
	}

	public static boolean decodeAndSaveImage(String base64Img, String pathImg) {
		byte[] imageData = Base64.getDecoder().decode(base64Img);
		try (FileOutputStream imageFile = new FileOutputStream(pathImg)) {
			imageFile.write(imageData);
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Image not found: " + e);
			return false;
		} catch (IOException e) {
			System.err.println("cannot read : " + e);
			return false;
		}
	}

	public static String getCalculationPath(String userId, Calculation calcul) {
		return Const.getConst().get("ApplicationPath") + File.separator + userId + File.separator + calcul.getId() + "_"
				+ calcul.getName() + File.separator + "result.png";
	}
}
