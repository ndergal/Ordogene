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

/**
 * Handle files for all the project : create, write in, check, remove, get path
 * @author darwinners team
 *
 */
public class FileUtils {

	private final static String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private final static UserManager uh = new UserManager();
	private final static CalculationManager ch = new CalculationManager();

	private FileUtils() {

	}

	/**
	 * call the method to check if the given user exist on the server
	 * 
	 * @param username
	 *            : name of the user to check
	 * @return true if the user exists, false otherwise.
	 */
	public static boolean userExist(String username) {
		return uh.checkUserExists(username);
	}

	/**
	 * call the method to add an user on the server
	 * 
	 * @param username
	 *            : name of the user to add
	 * @return true if the user has been added, false otherwise.
	 */
	public static boolean addUser(String username) {
		return uh.createAnUser(username);
	}

	/**
	 * call the method to remove an user on the server
	 * 
	 * @param username
	 *            : name of the user to remove
	 * @return true if the user has been deleted, false otherwise.
	 */
	public static boolean removeUser(String username) {
		return uh.removeUser(username);
	}

	/**
	 * call the method to get user's calculations
	 * @param username
	 * @return the calculations of the specified user in argument
	 */
	public static List<Calculation> getUserCalculations(String username) {
		return ch.getCalculations(username);
	}

	/**
	 * call the method to remove user's calculations
	 * @param username
	 *            : owner of the calculation to deleter
	 * @param cid
	 *            : id of the calculation to delete
	 * @param calName
	 *            : name of the calculation to delete
	 * @return true if success, false otherwise
	 */
	public static boolean removeUserCalculation(String username, int cid, String calName) {
		return ch.removeCalculation(username, cid, calName);
	}

	/**
	 * 
	 * @param content
	 *            : object to write in json
	 * @param username
	 *            : id of the calculation owner
	 * @param cid
	 *            : id of the calculation to write in
	 * @param calName
	 *            : name of the calculation to write in
	 */
	public static void writeJsonInFile(Object content, String username, int cid, String calName) throws IOException {
		Path dest = Paths.get(getCalculationStatePath(username, cid, calName));
		if (dest.toFile().exists()) {
			Files.delete(dest);
		}
		Files.createDirectories(dest.getParent());
		Files.createFile(dest);

		ObjectMapper mapper = new ObjectMapper();

		// Object to JSON in file
		mapper.writeValue(dest.toFile(), content);

	}

	/**
	 * 
	 * @param path
	 *            : file to encode in base 64
	 * @return : file encoded in base 64
	 */
	public static String encodeFile(Path path) throws IOException {
		Objects.requireNonNull(path);
		byte[] imageData = Files.readAllBytes(path);
		return Base64.getEncoder().encodeToString(imageData);
	}

	/**
	 * 
	 * @param base64Img
	 *            : image in base 64
	 * @param pathDstImg
	 *            : destination path (relative or absolute, in String) to write the
	 *            image on the disk
	 * @throws IOException
	 *             : if it is not possible to write on the folder
	 * @throws FileNotFoundException
	 *             : if the destination Path doesn't exists
	 */
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

	/**
	 * Save a result in .html and .png format
	 * 
	 * @param html
	 *            : html content to write
	 * @param directory
	 *            : destination Path for the .png and .html file
	 * @return true if the files has been writed successfully, false otherwise.
	 */
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

	/**
	 * 
	 * @param model
	 *            : file to Read
	 * @return : file read as a String
	 * @throws IOException
	 *             : if it's impossible to read the model
	 * @throws IllegalArgumentException
	 *             : if model is a folder or does not exists
	 */
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

	/**
	 * 
	 * @param directory
	 *            to create
	 * @return true if directory is created, false otherwise
	 */
	public static boolean createCalculationDirectory(Path directory) {
		try {
			Files.createDirectories(directory);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param base64Html : html code in base 64
	 * @param pathDstHtml : path to save the base 64 html decoded
	 * @throws IOException : if it's not possible to write (rights, incorrect path,...)
	 */
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

	/**
	 * 
	 * @param userId : owner of the calculation
	 * @param cid : id of the calculation
	 * @param calName : name of the calculation
	 * @return : path of the state.json file for this calculation
	 */
	public static String getCalculationStatePath(String userId, int cid, String calName) {
		return getCalculationDirectoryPath(userId, cid, calName) + File.separator + "state.json";
	}

	/**
	 * 
	 * @param userId : owner of the calculation
	 * @param cid : id of the calculation
	 * @param calName : name of the calculation
	 * @return : path of the calculation 
	 */
	public static String getCalculationDirectoryPath(String userId, int cid, String cName) {
		return Const.getConst().get("ApplicationPath") + File.separatorChar + userId + File.separatorChar + "" + cid
				+ "_" + cName;
	}
}
