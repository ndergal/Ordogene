package org.ordogene.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
	public void launchCalcJsonParseExceptionTest() throws Exception {
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");

		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		jsonContentPost = jsonContentPost.replace("[", "@");

		MvcResult result = mvc.perform(put("/" + usertest + "/calculations").content(jsonContentPost)).andExpect(status().isBadRequest()).andReturn();
 
		ApiJsonResponse ajrWaited = new ApiJsonResponse("tester", 0, "Invalid JSON (JsonParseException) ", null,
				null);
		String errorResponse = result.getResponse().getContentAsString();
		System.out.println("launchCalcJsonParseExceptionTest "+errorResponse);
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

		MvcResult result = mvc.perform(put("/" + usertest + "/calculations").content(jsonContentPost)).andExpect(status().isBadRequest()).andReturn();
 
		ApiJsonResponse ajrWaited = new ApiJsonResponse("tester", 0, "Invalid JSON (Missing fields in the JSON) ", null,
				null);
		String errorResponse = result.getResponse().getContentAsString();
		System.out.println("launchCalcJsonParseExceptionTest "+errorResponse);
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
 
		MvcResult result = mvc.perform(put("/" + unknowUserId + "/calculations").content(jsonContentPost)).andExpect(status().isNotFound()).andReturn();
 
		ApiJsonResponse ajrWaited = new ApiJsonResponse(null, 0, "'" + unknowUserId + "' does not exist", null,
				null);
		String errorResponse = result.getResponse().getContentAsString();
 		String jsonResponseWaited = mapper.writeValueAsString(ajrWaited);
		assertEquals(jsonResponseWaited, errorResponse);
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