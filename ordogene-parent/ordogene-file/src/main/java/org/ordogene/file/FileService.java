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

	public boolean removeUserCalculation(String username, Calculation c) {
		return ch.removeCalculation(username, c.getId(), c.getName());
	}

	public static boolean createCalculationDirectory(String userId, int cId, String cName) {
		Path calculationPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + userId
				+ File.separatorChar + "" + cId + "_" + cName);
		try {
			Files.createDirectories(calculationPath);
			return true;
		} catch (IOException e) {
			return false;
		}

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

	public static String encodeB64File(Path pathSrcImg) throws IOException {
		Objects.requireNonNull(pathSrcImg);
		byte[] imageData = Files.readAllBytes(pathSrcImg);
		return Base64.getEncoder().encodeToString(imageData);
	}

	public static boolean decodeAndSaveImage(String base64Img, String pathDstImg) {
		Objects.requireNonNull(base64Img);
		Objects.requireNonNull(pathDstImg);
		byte[] imageData = Base64.getDecoder().decode(base64Img);
		try (FileOutputStream imageFile = new FileOutputStream(pathDstImg)) {
			imageFile.write(imageData);
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Destination path is not a file : " + e);
			return false;
		} catch (IOException e) {
			System.err.println("cannot write to the path : " + e);
			return false;
		}
	}

	public static boolean decodeAndSaveHtml(String base64Html, String pathDstHtml) {
		Objects.requireNonNull(base64Html);
		Path destPath = Paths.get(Objects.requireNonNull(pathDstHtml));
		try (BufferedWriter writer = Files.newBufferedWriter(destPath, Charset.forName("UTF-8"))) {
			byte[] decodedBytes = Base64.getDecoder().decode(base64Html);
			String decodedStr = new String(decodedBytes);
			writer.write(decodedStr);
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Destination path is not a file : " + e);
			return false;
		} catch (IOException e) {
			System.err.println("cannot write to the path : " + e);
			return false;
		}
	}

	public static String getCalculationPngPath(String userId, Calculation calcul) {
		return Const.getConst().get("ApplicationPath") + File.separator + userId + File.separator + calcul.getId() + "_"
				+ calcul.getName() + File.separator + "result.png";
	}

	public static String getCalculationHtmlPath(String userId, Calculation calcul) {
		return Const.getConst().get("ApplicationPath") + File.separator + userId + File.separator + calcul.getId() + "_"
				+ calcul.getName() + File.separator + "result.html";
	}

	public static boolean saveHtmlAndPng(String html, Path pngPath, Path htmlPath) {
		try {
			Files.createDirectories(pngPath.getParent());
			Files.createDirectories(htmlPath.getParent());
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
}
