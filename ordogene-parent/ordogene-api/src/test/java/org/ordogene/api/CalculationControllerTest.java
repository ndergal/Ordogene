package org.ordogene.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordogene.file.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CalculationControllerTest {

	@Autowired
	private MockMvc mvc;

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
	public void launchCalcTest() throws Exception {
		String username = usertest;
		// MvcResult result =
		// mvc.perform(get("/"+username).accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();
		URL urlTestFile = CalculationControllerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_100.json");
		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		/*
		 * System.out.println("Send to " + "/" + username + "/calculations");
		 * System.out.println(jsonContentPost);
		 */
		MvcResult result = mvc.perform(put("/" + username + "/calculations").content(jsonContentPost))
				.andExpect(status().isOk()).andReturn();

		// System.out.println(result.getResponse().getContentAsString());

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