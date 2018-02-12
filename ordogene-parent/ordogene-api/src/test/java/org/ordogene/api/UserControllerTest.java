package org.ordogene.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URISyntaxException;

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
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@Before
	public void init() throws URISyntaxException {
		String configFileLocation = UserController.class.getClassLoader().getResource("ordogene.conf.json").toURI().toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
	}
	
	@Test
	public void createRandomUserOk() throws Exception {
		MvcResult result = mvc.perform(put("/").accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();
	}
	
//	@Test
//	public void createAndGetRandomUserOk() throws Exception {
//		MvcResult result = mvc.perform(put("/").accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();
//		String content = result.getResponse().getContentAsString();
//		mvc.perform(put("/" + content).accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();
//	}

}