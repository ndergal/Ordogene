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

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

/**
 * Unit test for simple App.
 */
public class FileServiceTest {

	@Before
	public void init() throws URISyntaxException {
		String configFileLocation = FileServiceTest.class.getClassLoader().getResource("ordogene.conf.json").toURI()
				.toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
	}

	@Test
	public void noUserTest() {
		FileService fs = new FileService();
		assertFalse(fs.userExist("edsvinubb"));
	}

	@Test
	public void createUserTest() {
		FileService fs = new FileService();
		if (fs.userExist("bwana"))
			fs.removeUser("bwana");
		assertTrue(fs.addUser("bwana"));
		fs.removeUser("bwana");
	}

	@Test
	public void removesUserTest() {
		FileService fs = new FileService();
		if (!fs.userExist("edsvinubb"))
			fs.addUser("bwana");

		assertTrue(fs.removeUser("bwana"));
	}

	@Test
	public void removesCalculationTest() throws IOException {
		FileService fs = new FileService();
		String user = "bwana";
		if (fs.userExist(user))
			fs.removeUser(user);
		assertTrue(fs.addUser(user));

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
		assertTrue(fs.removeUserCalculation(user, c));
		assertFalse(Files.exists(newCalcPath));
	}

	@Test // (expected=IOException.class)
	public void removesCalculationBadUserTest() throws IOException {
		FileService fs = new FileService();
		String user = "bwana2";
		if (fs.userExist(user))
			fs.removeUser(user);

		int calcID = -1234567;
		String calcName = "Test";

		Path newCalcPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + user
				+ File.separator + calcID + "_" + calcName);

		Path newCalcStatePath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + user
				+ File.separator + "-123456_Test" + File.separator + "state.json");

		FileUtils.deleteDirectory(newCalcPath.getParent().toFile());

		Calculation c = new Calculation();
		c.setId(calcID);
		c.setName(calcName);
		assertFalse(fs.removeUserCalculation(user, c));
		assertFalse(Files.exists(newCalcPath));
	}

	@Test
	public void removesCalculationUserNullTest() throws IOException {
		FileService fs = new FileService();
		String user = "";

		assertFalse(fs.removeUserCalculation(null, new Calculation()));
		assertFalse(fs.removeUserCalculation(user, new Calculation()));
	}

	@Test
	public void getRandomUid() {
		FileService fs = new FileService();
		int size = 89;
		String ruid = fs.generateRandomUserId(size);
		assertFalse(ruid == null);
		assertEquals(size, ruid.length());
	}

	@Test
	public void testEncodeAndDecode() throws IOException, URISyntaxException {
		Path path1 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-image/doge_test.png").toURI());
		Path path2 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-image/result.png").toURI());

		String ref = FileService.encodeB64File(path1);
		FileService.decodeAndSaveImage(ref, path2.toString());
		String end = FileService.encodeB64File(path2);
		assertEquals(ref, end);
	}

	@Test
	public void testDecodeWithFolder() throws IOException, URISyntaxException {
		Path path1 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-image/doge_test.png").toURI());
		Path path2 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-image/").toURI());

		String ref = FileService.encodeB64File(path1);
		assertFalse(FileService.decodeAndSaveImage(ref, path2.toString()));
	}

	@Test
	public void testDecodePng() throws IOException, URISyntaxException {
		Path path1 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-image/doge_test.png").toURI());
		Path path2 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-image/result.png").toURI());
		String ref = FileService.encodeB64File(path1);
		assertTrue(FileService.decodeAndSaveImage(ref, path2.toString()));
	}

	@Test
	public void testDecodeHtml() throws IOException, URISyntaxException {
		Path path1 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-html/result.html").toURI());
		Path path2 = Paths.get(FileServiceTest.class.getClassLoader().getResource("./test-html/result2.html").toURI());
		String ref = FileService.encodeB64File(path1);
		assertTrue(FileService.decodeAndSaveHtml(ref, path2.toString()));
		assertEquals(new String(Files.readAllBytes(path1)), new String(Files.readAllBytes(path2)));
	}

	@Test(expected = IOException.class)
	public void testEncodeWithFolder() throws IOException, URISyntaxException {
		Path path1 = Paths.get(FileServiceTest.class.getClassLoader().getResource("test-image/").toURI());

		FileService.encodeB64File(path1);
	}
}
