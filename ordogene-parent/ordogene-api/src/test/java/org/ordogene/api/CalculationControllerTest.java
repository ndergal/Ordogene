package org.ordogene.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.ordogene.algorithme.master.Master;
import org.ordogene.api.utils.ApiJsonResponseCreator;
import org.ordogene.file.FileService;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CalculationControllerTest {

	@Autowired
	private MockMvc mvc;

	private static final ObjectMapper mapper = new ObjectMapper();

	@Mock
	private FileService fs;
	@Mock
	private Master master;

	@InjectMocks
	private CalculationController cc;

	String usertest = "tester";

	@Before
	public void init() throws URISyntaxException {
		String configFileLocation = UserController.class.getClassLoader().getResource("ordogene.conf.json").toURI()
				.toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
		try {
			Files.createDirectories(Paths.get(Const.getConst().get("ApplicationPath") + File.separator + usertest));
		} catch (IOException e) {
			System.err.println("Error while creating the directory " + Const.getConst().get("ApplicationPath")
					+ File.separator + "tester");
			e.printStackTrace();
		}

		Path sourcePath = (Paths.get(
				CalculationControllerTest.class.getClassLoader().getResource("-624472280_dummy-calc-test").toURI()));
		Path destinationPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separator + usertest
				+ File.separator + "-624472280_dummy-calc-test");

		System.out.println("Move " + sourcePath.toAbsolutePath().toString() + " in "
				+ destinationPath.toAbsolutePath().toString());
		try {
			if (Files.exists(destinationPath)) {
				FileUtils.deleteDirectory(destinationPath.toFile());
				Files.deleteIfExists(destinationPath);
			}
			Files.createDirectories(destinationPath);

			FileUtils.copyDirectory(sourcePath.toFile(), destinationPath.toFile());
		} catch (IOException e) {
			// problem !?
			e.printStackTrace();
		}

	}

	@Test
	public void launchCalcOKTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);

		MvcResult result = mvc.perform(put("/" + usertest + "/calculations").content(jsonContentPost))
				.andExpect(status().isOk()).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void launchCalcJsonMappingExceptionTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		jsonContentPost = jsonContentPost.replace("name", "noname");

		MvcResult result = mvc.perform(put("/" + usertest + "/calculations").content(jsonContentPost))
				.andExpect(status().isBadRequest()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse("tester", 0, "Invalid JSON (JsonMappingException) ", null,
				null);
		String errorResponse = result.getResponse().getContentAsString();
		String jsonResponseWaited = mapper.writeValueAsString(ajrWaited);
		assertEquals(jsonResponseWaited, errorResponse);
	}

	@Test
	public void getUserCalculationsTest_null_userId() {
		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		assertEquals(expected, cc.getUserCalculations(null));
	}

	@Test
	public void getUserCalculationsTest_empty_userId() {
		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		assertEquals(expected, cc.getUserCalculations(""));
	}

	@Test
	public void getUserCalculationsTest_userId_notExist() {
		String userId = "test";
		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNotExist(userId), HttpStatus.NOT_FOUND);

		when(fs.userExist(userId)).thenReturn(false);

		assertEquals(expected, cc.getUserCalculations(userId));
	}

	@Test
	public void getUserCalculationsTest_userId_exist_emptyList() {
		String userId = "test";
		List<Calculation> result = Collections.emptyList();

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.listCalculation(result), HttpStatus.OK);

		when(fs.userExist(userId)).thenReturn(true);
		when(fs.getUserCalculations(userId)).thenReturn(result);

		assertEquals(expected, cc.getUserCalculations(userId));
	}

	@Test
	public void getUserCalculationsTest_userId_exist_withResult() {
		String userId = "test";
		List<Calculation> calculationsToTest = new ArrayList<>();
		List<Calculation> result = new ArrayList<>();
		Calculation cm1 = new Calculation();
		Calculation cm2 = new Calculation();
		Calculation cf1 = new Calculation();
		Calculation cf2 = new Calculation();

		calculationsToTest.add(cm1);
		calculationsToTest.add(cm2);

		result.add(cf1);
		result.add(cf2);

		cm1.setCalculation(0, 2, 1, 3, 1, "c1", 42);
		cm1.setRunning(true);

		cm2.setCalculation(10, 12, 11, 13, 2, "c2", 142);
		cm2.setRunning(false);

		cf1.setCalculation(0, 2, 1, 3, 1, "c1", 42);
		cf1.setRunning(true);

		cf2.setCalculation(10, 12, 11, 13, 2, "c2", 142);
		cf2.setRunning(false);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.listCalculation(result), HttpStatus.OK);

		when(fs.userExist(userId)).thenReturn(true);
		when(fs.getUserCalculations(userId)).thenReturn(calculationsToTest);

		doNothing().when(master).updateCalculation(any(Calculation.class), anyString());

		assertEquals(expected, cc.getUserCalculations(userId));
	}

	@Test
	public void launchCalcJsonParseExceptionTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		jsonContentPost = jsonContentPost.replace("[", "@");

		MvcResult result = mvc.perform(put("/" + usertest + "/calculations").content(jsonContentPost))
				.andExpect(status().isBadRequest()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse("tester", 0, "Invalid JSON (JsonParseException) ", null, null);
		String errorResponse = result.getResponse().getContentAsString();
		// System.out.println("launchCalcJsonParseExceptionTest "+errorResponse);
		String jsonResponseWaited = mapper.writeValueAsString(ajrWaited);
		assertEquals(jsonResponseWaited, errorResponse);
	}

	@Test
	public void launchCalcUnmarshalExceptionTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		jsonContentPost = jsonContentPost.replace("\"exec_time\" : 10000,", "");

		MvcResult result = mvc.perform(put("/" + usertest + "/calculations").content(jsonContentPost))
				.andExpect(status().isBadRequest()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse("tester", 0, "Invalid JSON (Missing fields in the JSON) ", null,
				null);
		String errorResponse = result.getResponse().getContentAsString();
		// System.out.println("launchCalcJsonParseExceptionTest "+errorResponse);
		String jsonResponseWaited = mapper.writeValueAsString(ajrWaited);
		assertEquals(jsonResponseWaited, errorResponse);
	}

	@Test
	public void launchUnknowUserCalcTest() throws Exception {

		String unknowUserId = "ZESFD";
		Path usrTestPath = Paths.get((Const.getConst().get("ApplicationPath") + File.separator + unknowUserId));

		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}

		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);

		MvcResult result = mvc.perform(put("/" + unknowUserId + "/calculations").content(jsonContentPost))
				.andExpect(status().isNotFound()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse(null, 0, "'" + unknowUserId + "' does not exist", null, null);
		String errorResponse = result.getResponse().getContentAsString();
		String jsonResponseWaited = mapper.writeValueAsString(ajrWaited);
		assertEquals(jsonResponseWaited, errorResponse);
	}

	@Test
	public void launchNoRouteCalcBodyNullTest() throws Exception {
		CalculationController cc = new CalculationController();
		ResponseEntity<ApiJsonResponse> responseAjr = cc.launchCalculation("tester", null);
		ResponseEntity<ApiJsonResponse> resWaited = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.jsonBodyNull(), HttpStatus.BAD_REQUEST);
		assertEquals(resWaited, responseAjr);

		responseAjr = cc.launchCalculation("tester", "");
		assertEquals(resWaited, responseAjr);
	}

	@Test
	public void launchNoRouteCalcUserNullTest() throws Exception {
		CalculationController cc = new CalculationController();
		ResponseEntity<ApiJsonResponse> responseAjr = cc.launchCalculation(null, null);
		ResponseEntity<ApiJsonResponse> resWaited = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		assertEquals(resWaited, responseAjr);

		responseAjr = cc.launchCalculation("", null);
		assertEquals(resWaited, responseAjr);
	}

	@Test
	public void getCalculationUserNull() throws Exception {
		CalculationController cc = new CalculationController();
		ResponseEntity<ApiJsonResponse> responseAjr = cc.getCalculation(null, 0);
		ResponseEntity<ApiJsonResponse> resWaited = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		assertEquals(resWaited, responseAjr);

		responseAjr = cc.getCalculation("", 0);
		assertEquals(resWaited, responseAjr);
	}

	@Test
	public void getCalculationSuccess() throws Exception {
		final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		// to don't print it in the shell
		System.setOut(new PrintStream(outContent));

		CalculationController cc = new CalculationController();
		MvcResult result = mvc.perform(put("/" + usertest + "/calculations/-624472280")).andExpect(status().isOk())
				.andReturn();

		String calculationGotStr = result.getResponse().getContentAsString();
		System.setOut(System.out);

		ApiJsonResponse ajrGot = mapper.readValue(calculationGotStr, ApiJsonResponse.class);
		ApiJsonResponse ajrWaited = new ApiJsonResponse(usertest, -624472280, null, null, null);
		assertEquals(ajrWaited.getCid(), ajrGot.getCid());
		assertEquals(ajrWaited.getUserId(), ajrGot.getUserId());
		assertNotEquals(null, ajrGot.getBase64img());

	}

	@Test
	public void getCalculationNotFoundTest() throws Exception {
		String userTest2 = "rsijgivbusiuddbgvzauzefv";
		int cid = ThreadLocalRandom.current().nextInt(-923549, 67175772);
		Path newUserPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separator + userTest2);
		try {
			if (Files.exists(newUserPath)) {
				FileUtils.deleteDirectory(newUserPath.toFile());
				Files.deleteIfExists(newUserPath);
			}
			Files.createDirectories(newUserPath);
		} catch (IOException e) {
			// problem !?
			e.printStackTrace();
		}

		CalculationController cc = new CalculationController();
		MvcResult result = mvc.perform(put("/" + userTest2 + "/calculations/" + cid)).andExpect(status().isNotFound())
				.andReturn();
		String responseJson = result.getResponse().getContentAsString();
		ApiJsonResponse ajrGot = mapper.readValue(responseJson, ApiJsonResponse.class);
		ApiJsonResponse ajrEexpected = new ApiJsonResponse(userTest2, 0,
				"calculation " + cid + " does not exist for user " + userTest2, null, null);
		assertEquals(ajrEexpected, ajrGot);

	}
	
	@Test
	public void getCalculationNotExistTest() throws Exception {
		String userTest2 = "rsijgiffuzefv";
		int cid = ThreadLocalRandom.current().nextInt(-923599, 87175772);
		Path newUserPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separator + userTest2);
		try {
			if (Files.exists(newUserPath)) {
				FileUtils.deleteDirectory(newUserPath.toFile());
				Files.deleteIfExists(newUserPath);
			}
		} catch (IOException e) {
			// problem !?
			e.printStackTrace();
		}

		CalculationController cc = new CalculationController();
		MvcResult result = mvc.perform(put("/" + userTest2 + "/calculations/" + cid)).andExpect(status().isNotFound())
				.andReturn();
		String responseJson = result.getResponse().getContentAsString();
		ApiJsonResponse ajrGot = mapper.readValue(responseJson, ApiJsonResponse.class);
		ApiJsonResponse ajrEexpected =ApiJsonResponseCreator.userIdNotExist(userTest2);
		assertEquals(ajrEexpected, ajrGot);

	}

	@Test
	public void stopCalculationTest_null_UserId() {
		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);

		assertEquals(expected, cc.stopCalculation(null, 0));
	}

	@Test
	public void stopCalculationTest_empty_UserId() {
		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);

		assertEquals(expected, cc.stopCalculation("", 0));
	}

	@Test
	public void stopCalculationTest_UserId_notExist() {
		when(fs.userExist(usertest)).thenReturn(false);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNotExist(usertest), HttpStatus.NOT_FOUND);

		assertEquals(expected, cc.stopCalculation(usertest, 0));
	}

	@Test
	public void stopCalculationTest_userId_isNot_launcherId() {
		List<Calculation> cals = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		cals.add(c);

		when(c.getId()).thenReturn(30);

		when(fs.userExist(usertest)).thenReturn(true);
		when(fs.getUserCalculations(usertest)).thenReturn(cals);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(usertest, 20, "The calculationId is wrong", null, null), HttpStatus.FORBIDDEN);

		assertEquals(expected, cc.stopCalculation(usertest, 20));
	}

	@Test
	public void stopCalculationTest_calculation_isNotRunning() {
		List<Calculation> cals = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		cals.add(c);

		when(c.getId()).thenReturn(20);

		when(fs.userExist(usertest)).thenReturn(true);
		when(fs.getUserCalculations(usertest)).thenReturn(cals);

		when(master.interruptCalculation(20)).thenReturn(false);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(usertest, 20, "The calcul is not running.", null, null), HttpStatus.NOT_FOUND);

		assertEquals(expected, cc.stopCalculation(usertest, 20));
	}

	@Test
	public void stopCalculationTest_OK() {
		List<Calculation> cals = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		cals.add(c);

		when(c.getId()).thenReturn(20);

		when(fs.userExist(usertest)).thenReturn(true);
		when(fs.getUserCalculations(usertest)).thenReturn(cals);

		when(master.interruptCalculation(20)).thenReturn(true);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(usertest, 20, null, null, null), HttpStatus.OK);

		assertEquals(expected, cc.stopCalculation(usertest, 20));
	}

	// @Test
	// public void createAndGetRandomUserOk() throws Exception {
	// MvcResult result =
	// mvc.perform(put("/").accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();
	// String content = result.getResponse().getContentAsString();
	// mvc.perform(put("/" +
	// content).accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();
	// }

}