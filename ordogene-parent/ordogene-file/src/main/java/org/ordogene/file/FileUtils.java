package org.ordogene.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import com.fasterxml.jackson.databind.ObjectMapper;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class FileUtils {
	
	private final static String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private final static UserManager uh = new UserManager();
	private final static CalculationManager ch = new CalculationManager();

	public static boolean userExist(String username) {
		return uh.checkUserExists(username);
	}

	public static boolean addUser(String username) {
		return uh.createAnUser(username);
	}

	public static boolean removeUser(String username) {
		return uh.removeUser(username);
	}

	public static String generateRandomUserId(int nbChar) {
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(nbChar);
		for (int i = 0; i < nbChar; i++)
			sb.append(ALPHA_NUM.charAt(rnd.nextInt(ALPHA_NUM.length())));
		return sb.toString();
	}

	public static List<Calculation> getUserCalculations(String username) {
		return ch.getCalculations(username);
	}

	public static boolean removeUserCalculation(String username, int cid, String calName) {
		return ch.removeCalculation(username, cid, calName);
	}

	public static void writeJsonInFile(Object content, String userId, int cid, String calName) throws IOException {
		Path dest = Paths.get(getCalculationStatePath(userId, cid, calName));
		if (Files.exists(dest)) {
			Files.delete(dest);
		}
		Files.createDirectories(dest.getParent());
		Files.createFile(dest);

		ObjectMapper mapper = new ObjectMapper();

		// Object to JSON in file
		mapper.writeValue(dest.toFile(), content);

	}

	public static String encodeFile(Path path) throws IOException {
		Objects.requireNonNull(path);
		byte[] imageData = Files.readAllBytes(path);
		return Base64.getEncoder().encodeToString(imageData);
	}

	public static void saveImageFromBase64(String base64Img, String pathDstImg)
			throws IOException, FileNotFoundException {
		Objects.requireNonNull(base64Img);
		Objects.requireNonNull(pathDstImg);
		byte[] imageData = Base64.getDecoder().decode(base64Img);
		try (FileOutputStream imageFile = new FileOutputStream(pathDstImg)) {
			imageFile.write(imageData);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Destination path is not a file or cannot be created/opened : " + e);
		} catch (IOException e) {
			throw new IOException("Cannot write to the path : " + e);
		}
	}

	public static boolean saveResult(String html, Path directory) {
		Path pngPath = directory.resolve("result.png");
		Path htmlPath = directory.resolve("result.html");
		try {
			Files.createDirectories(directory);
			if (htmlPath != null) {
				Files.write(htmlPath, html.getBytes(StandardCharsets.UTF_8));
			}

			HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
			imageGenerator.loadHtml(html);
			imageGenerator.saveAsImage(pngPath.toString());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String readFile(File model) throws IOException {
		Path jsonPath = model.toPath();
		if (!jsonPath.toFile().exists()) {
			throw new IllegalArgumentException("The path does not exist. Try again.");
		}
		if (jsonPath.toFile().isDirectory()) {
			throw new IllegalArgumentException(jsonPath + " is a directory. Try again.");
		}
		return new String(Files.readAllBytes(jsonPath));
	}

	public static boolean createCalculationDirectory(Path directory) {
		try {
			Files.createDirectories(directory);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void saveHtmlFromBase64(String base64Html, String pathDstHtml) throws IOException {
		Objects.requireNonNull(base64Html);
		Objects.requireNonNull(pathDstHtml);
		Path path = Paths.get(pathDstHtml);
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			byte[] decodedBytes = Base64.getDecoder().decode(base64Html);
			String decodedStr = new String(decodedBytes);
			writer.write(decodedStr);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Destination path is not a file : " + e);
		} catch (IOException e) {
			throw new IOException("cannot write to the path : " + e);
		}
	}

	public static String getCalculationStatePath(String userId, int cid, String calName) {
		return getCalculationDirectoryPath(userId, cid, calName) + File.separator + "state.json";
	}

	public static String getCalculationDirectoryPath(String userId, int cid, String cName) {
		return Const.getConst().get("ApplicationPath") + File.separatorChar + userId + File.separatorChar + "" + cid
				+ "_" + cName;
	}
}
