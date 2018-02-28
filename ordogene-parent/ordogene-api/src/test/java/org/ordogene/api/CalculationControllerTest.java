package org.ordogene.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.ordogene.algorithme.master.Master;
import org.ordogene.api.utils.ApiJsonResponseCreator;
import org.ordogene.file.FileUtils;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger log = LoggerFactory.getLogger(CalculationControllerTest.class);

	@Autowired
	private MockMvc mvc;

	private static final ObjectMapper mapper = new ObjectMapper();

	@Mock
	private Master master;

	@InjectMocks
	private CalculationController cc;

	private final String userTest = "tester";
	private final int cidTest = -624472280;
	private final String calNameTest = "dummy-calc-test";

	@Before
	public void init() throws URISyntaxException, IOException {
		String configFileLocation = CalculationControllerTest.class.getClassLoader().getResource("ordogene.conf.json").toURI()
				.toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
		String applicationPath = Const.getConst().get("ApplicationPath");
		String userDirectory = applicationPath + File.separator + userTest;
		try {
			Files.createDirectories(Paths.get(userDirectory));
		} catch (IOException e) {
			log.error("Error while creating the directory " + userDirectory);
			e.printStackTrace();
		}

		Path sourcePath = (Paths.get(
				CalculationControllerTest.class.getClassLoader().getResource(cidTest + "_" + calNameTest).toURI()));
		Path destinationPath = Paths.get(userDirectory + File.separator + cidTest + "_" + calNameTest);

		Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

	}

	@Test
	public void launchCalcOKTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_10.json");
		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);

		mvc.perform(put("/" + userTest + "/calculations").content(jsonContentPost))
				.andExpect(status().isOk()).andReturn();
	}

	@Test
	public void launchCalcJsonMappingExceptionTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		jsonContentPost = jsonContentPost.replace("name", "noname");

		MvcResult result = mvc.perform(put("/" + userTest + "/calculations").content(jsonContentPost))
				.andExpect(status().isBadRequest()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse(userTest, 0, "Invalid JSON (JsonMappingException) ", null,
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

		when(FileUtils.userExist(userId)).thenReturn(false);

		assertEquals(expected, cc.getUserCalculations(userId));
	}

	@Test
	public void getUserCalculationsTest_userId_exist_emptyList() {
		String userId = "test";
		List<Calculation> result = Collections.emptyList();

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.listCalculation(result), HttpStatus.OK);

		when(FileUtils.userExist(userId)).thenReturn(true);
		when(FileUtils.getUserCalculations(userId)).thenReturn(result);

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

		when(FileUtils.userExist(userId)).thenReturn(true);
		when(FileUtils.getUserCalculations(userId)).thenReturn(calculationsToTest);

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

		MvcResult result = mvc.perform(put("/" + userTest + "/calculations").content(jsonContentPost))
				.andExpect(status().isBadRequest()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse(userTest, 0, "Invalid JSON (JsonParseException) ", null, null);
		String errorResponse = result.getResponse().getContentAsString();
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

		MvcResult result = mvc.perform(put("/" + userTest + "/calculations").content(jsonContentPost))
				.andExpect(status().isBadRequest()).andReturn();

		ApiJsonResponse ajrWaited = new ApiJsonResponse("tester", 0, "Invalid JSON (Missing fields in the JSON) ", null,
				null);
		String errorResponse = result.getResponse().getContentAsString();
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
		ResponseEntity<ApiJsonResponse> responseAjr = cc.launchCalculation(userTest, null);
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
		ResponseEntity<ApiJsonResponse> responseAjr = cc.getCalculationPng(null, 0);
		ResponseEntity<ApiJsonResponse> resWaited = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		assertEquals(resWaited, responseAjr);

		responseAjr = cc.getCalculationPng("", 0);
		assertEquals(resWaited, responseAjr);
	}

	@Test
	public void getCalculationSuccess() throws Exception {
		MvcResult result = mvc.perform(put("/" + userTest + "/calculations/" + cidTest)).andExpect(status().isOk())
				.andReturn();

		String calculationGotStr = result.getResponse().getContentAsString();

		ApiJsonResponse ajrGot = mapper.readValue(calculationGotStr, ApiJsonResponse.class);
		ApiJsonResponse ajrWaited = new ApiJsonResponse(userTest, cidTest, null, null, null);
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
			FileUtils.removeUserCalculation(userTest, cidTest, calNameTest);
			Files.createDirectories(newUserPath);
		} catch (IOException e) {
			// problem !?
			e.printStackTrace();
		}

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

		FileUtils.removeUser(userTest2);

		MvcResult result = mvc.perform(put("/" + userTest2 + "/calculations/" + cid)).andExpect(status().isNotFound())
				.andReturn();
		String responseJson = result.getResponse().getContentAsString();
		ApiJsonResponse ajrGot = mapper.readValue(responseJson, ApiJsonResponse.class);
		ApiJsonResponse ajrEexpected = ApiJsonResponseCreator.userIdNotExist(userTest2);
		assertEquals(ajrEexpected, ajrGot);

	}

	@Test
	public void removeCalculationTest() throws Exception {
		mvc.perform(delete("/" + userTest + "/calculations/" + cidTest)).andExpect(status().isOk())
				.andReturn();

	}

	@Test
	public void removeCalculationUserNullTest() throws Exception {

		CalculationController cc = new CalculationController();
		ResponseEntity<ApiJsonResponse> responseAjr = cc.removeCalculation(null, 0);
		ResponseEntity<ApiJsonResponse> resWaited = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		assertEquals(resWaited, responseAjr);

		responseAjr = cc.removeCalculation("", 0);
		assertEquals(resWaited, responseAjr);
	}

	@Test
	public void removeCalculationUserNotExistTest() throws Exception {
		FileUtils.removeUserCalculation(userTest, cidTest, calNameTest);
		
		mvc.perform(delete("/" + userTest + "/calculations/" + 2354)).andExpect(status().isNotFound())
				.andReturn();
	}

	@Test
	public void removeCalculationDontExistTest() throws Exception {
		mvc.perform(delete("/" + userTest + "/calculations/" + cidTest))
				.andExpect(status().isBadRequest()).andReturn();

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
		when(FileUtils.userExist(userTest)).thenReturn(false);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				ApiJsonResponseCreator.userIdNotExist(userTest), HttpStatus.NOT_FOUND);

		assertEquals(expected, cc.stopCalculation(userTest, 0));
	}

	@Test
	public void stopCalculationTest_userId_isNot_launcherId() {
		List<Calculation> cals = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		cals.add(c);

		when(c.getId()).thenReturn(30);

		when(FileUtils.userExist(userTest)).thenReturn(true);
		when(FileUtils.getUserCalculations(userTest)).thenReturn(cals);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(userTest, 20, "The calculationId is wrong", null, null), HttpStatus.FORBIDDEN);

		assertEquals(expected, cc.stopCalculation(userTest, 20));
	}

	@Test
	public void stopCalculationTest_calculation_isNotRunning() {
		List<Calculation> cals = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		cals.add(c);

		when(c.getId()).thenReturn(20);

		when(FileUtils.userExist(userTest)).thenReturn(true);
		when(FileUtils.getUserCalculations(userTest)).thenReturn(cals);

		when(master.interruptCalculation(20)).thenReturn(false);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(userTest, 20, "The calcul is not running.", null, null), HttpStatus.NOT_FOUND);

		assertEquals(expected, cc.stopCalculation(userTest, 20));
	}

	@Test
	public void stopCalculationTest_OK() {
		List<Calculation> cals = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		cals.add(c);

		when(c.getId()).thenReturn(20);

		when(FileUtils.userExist(userTest)).thenReturn(true);
		when(FileUtils.getUserCalculations(userTest)).thenReturn(cals);

		when(master.interruptCalculation(20)).thenReturn(true);

		ResponseEntity<ApiJsonResponse> expected = new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(userTest, 20, null, null, null), HttpStatus.OK);

		assertEquals(expected, cc.stopCalculation(userTest, 20));
	}

}