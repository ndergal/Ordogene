package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

/**
 * Unit test for simple App.
 */
public class FileUtilsTest {

	@Before
	public void init() throws URISyntaxException {
 		String configFileLocation = FileUtilsTest.class.getClassLoader().getResource("ordogene.conf.json").toURI().toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
	}

	@Test
	public void noUserTest() {
		assertFalse(FileUtils.userExist("edsvinubb"));
	}

	@Test
	public void createUserTest() {
		if (FileUtils.userExist("bwana"))
			FileUtils.removeUser("bwana");
		assertTrue(FileUtils.addUser("bwana"));
		FileUtils.removeUser("bwana");
	}

	@Test
	public void removesUserTest() {
		if (!FileUtils.userExist("edsvinubb"))
			FileUtils.addUser("bwana");

		assertTrue(FileUtils.removeUser("bwana"));
	}

	@Test
	public void removesCalculationTest() throws IOException {
		String user = "bwana";
		if (FileUtils.userExist(user))
			FileUtils.removeUser(user);
		assertTrue(FileUtils.addUser(user));

		int calcID = -123456;
		String calcName = "Test";

		Path newCalcPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + user
				+ File.separator + calcID + "_" + calcName);

		Path newCalcStatePath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + user
				+ File.separator + "-123456_Test" + File.separator + "state.json");

		if (!Files.exists(newCalcPath)) {
			Files.createDirectories(newCalcPath);
			System.out.println("Create fake calculation for " + user);
		}
		Files.write(newCalcStatePath, Arrays.asList("Coucou", "Les copains"), Charset.forName("UTF-8"));
		Calculation c = new Calculation();
		c.setId(calcID);
		c.setName(calcName);
		assertTrue(FileUtils.removeUserCalculation(user, c.getId(), c.getName()));
		assertFalse(Files.exists(newCalcPath));
	}

	@Test // (expected=IOException.class)
	public void removesCalculationBadUserTest() throws IOException {
		String user = "bwana2";
		if (FileUtils.userExist(user))
			FileUtils.removeUser(user);


		int calcID = -1234567;
		String calcName = "Test";

		Path newCalcPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + user
				+ File.separator + calcID + "_" + calcName);

		FileUtils.removeUserCalculation(user, calcID, calcName);

		assertFalse(FileUtils.removeUserCalculation(user, calcID, calcName));
		assertFalse(Files.exists(newCalcPath));
	}

	@Test
	public void removesCalculationUserNullTest() throws IOException {
		assertFalse(FileUtils.removeUserCalculation(null, 0, "calName"));
		assertFalse(FileUtils.removeUserCalculation("", 0, "calName"));
	}

	@Test
	public void getRandomUid() {
		int size = 89;
		String ruid = FileUtils.generateRandomUserId(size);
		assertFalse(ruid == null);
		assertEquals(size, ruid.length());
	}

//	@Test
//	public void testEncodeAndDecode() throws IOException {
//		String ref = FileUtils.encodeImage(Paths.get("../ordogene-file/test-image/doge_test.png"));
//		FileUtils.saveImageFromBase64(ref, "../ordogene-file/test-image/result.png");
//		String end = FileUtils.encodeImage(Paths.get("../ordogene-file/test-image/result.png"));
//		assertEquals(ref, end);
//	}
//
//	@Test
//	public void testDecodeWithFolder() throws IOException {
//		String ref = FileUtils.encodeImage(Paths.get("../ordogene-file/test-image/doge_test.png"));
//		assertFalse(FileUtils.saveImageFromBase64(ref, "../ordogene-file/test-image/"));
//	}
//
//	@Test
//	public void testDecode() throws IOException {
//		String ref = FileUtils.encodeImage(Paths.get("../ordogene-file/test-image/doge_test.png"));
//		assertTrue(FileUtils.saveImageFromBase64(ref, "../ordogene-file/test-image/result.png"));
//	}
//
//	@Test(expected = IOException.class)
//	public void testEncodeWithFolder() throws IOException {
//		FileUtils.encodeImage(Paths.get("../ordogene-file/test-image/"));
//	}
}
