package org.ordogene.api;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	private static final ObjectMapper mapper = new ObjectMapper();

	@Before
	public void init() throws URISyntaxException {
		String configFileLocation = UserController.class.getClassLoader().getResource("ordogene.conf.json").toURI()
				.toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
	}

	@Test
	public void createRandomUserOk() throws Exception {
		MvcResult result = mvc.perform(put("/").accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
				.andReturn();
	}

	@Test
	public void createNamedUser() throws Exception {
		String username = "zesngzushg";
		Path usrTestPath = Paths.get((Const.getConst().get("ApplicationPath") + File.separator + username));
		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}
		ApiJsonResponse ajr = new ApiJsonResponse(username, 0, null, null, null);

		String jsonResponseWaited = mapper.writeValueAsString(ajr);
		// ApiJsonResponse ajr2 = mapper.readValue(jsonInString, ApiJsonResponse.class);

		mvc.perform(MockMvcRequestBuilders.put("/" + username).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string(equalTo(jsonResponseWaited)));
		assertTrue(Files.exists(usrTestPath));

		// and remove the created user
		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}

	}

	@Test
	public void createTwoSameNamedUser() throws Exception {
		String username = "zesngzushg";
		Path usrTestPath = Paths.get((Const.getConst().get("ApplicationPath") + File.separator + username));
		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}
		ApiJsonResponse ajr = new ApiJsonResponse(username, 0, null, null, null);

		String jsonResponseWaited = mapper.writeValueAsString(ajr);
		// ApiJsonResponse ajr2 = mapper.readValue(jsonInString, ApiJsonResponse.class);

		mvc.perform(MockMvcRequestBuilders.put("/" + username).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string(equalTo(jsonResponseWaited)));
		assertTrue(Files.exists(usrTestPath));

		mvc.perform(MockMvcRequestBuilders.put("/" + username).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andReturn();

		// and remove the created user
		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}

	}

	@Test
	public void createRandomUser() throws Exception {

		MvcResult res = mvc.perform(MockMvcRequestBuilders.put("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String resStr = res.getResponse().getContentAsString();
		ApiJsonResponse ajr2 = mapper.readValue(resStr, ApiJsonResponse.class);
		Path usrTestPath = Paths.get((Const.getConst().get("ApplicationPath") + File.separator + ajr2.getUserId()));
		assertTrue(Files.exists(usrTestPath));
		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}
	}

	@Test
	public void createAndGetRandomUser() throws Exception {

		MvcResult res = mvc.perform(MockMvcRequestBuilders.put("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String resStr = res.getResponse().getContentAsString();
		ApiJsonResponse ajr2 = mapper.readValue(resStr, ApiJsonResponse.class);
		String newUserId = ajr2.getUserId();
		Path usrTestPath = Paths.get((Const.getConst().get("ApplicationPath") + File.separator + newUserId));
		assertTrue(Files.exists(usrTestPath));

		res = mvc.perform(MockMvcRequestBuilders.get("/" + newUserId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}

	}

	@Test
	public void getNullUser() throws Exception {

		UserController uc = new UserController();
		ResponseEntity<ApiJsonResponse> res = uc.exists(null);
		ApiJsonResponse ajr = res.getBody();
		assertEquals("The userId to check is null or empty", ajr.getError());
	}

	@Test
	public void getEmptyUser() throws Exception {

		UserController uc = new UserController();
		ResponseEntity<ApiJsonResponse> res = uc.exists("");
		ApiJsonResponse ajr = res.getBody();
		assertEquals("The userId to check is null or empty", ajr.getError());
	}

	@Test
	public void createNullAndEmptyUser() throws Exception {

		UserController uc = new UserController();
		ResponseEntity<ApiJsonResponse> res = uc.createUserGivenId("");
		ApiJsonResponse ajr = res.getBody();
		assertEquals("The userId to check is null or empty", ajr.getError());

		res = uc.createUserGivenId(null);
		ajr = res.getBody();
		assertEquals("The userId to check is null or empty", ajr.getError());
	}

	@Test
	public void getNonExistingUser() throws Exception {
		String username = "bananes";
		Path usrTestPath = Paths.get((Const.getConst().get("ApplicationPath") + File.separator + username));
		if (Files.exists(usrTestPath)) {
			FileSystemUtils.deleteRecursively(usrTestPath.toFile());
		}
		MvcResult res = mvc.perform(MockMvcRequestBuilders.get("/" + username).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
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