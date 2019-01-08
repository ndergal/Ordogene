package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Unit test for simple App.
 */
public class ApiJsonResponseTest {
	private final static ObjectMapper mapper = new ObjectMapper();

	@Before
	public void init() {
		Const.loadConfig("./src/test/resources/ordogene.conf.json");
	}
	
	@Test
	public void ApiJsonResponseCreationTest() {
		new ApiJsonResponse("userId", 10, "Error string", Collections.emptyList(), "base64String");
	}
	
	@Test
	public void testAllSetter() {
		ApiJsonResponse ajr = new ApiJsonResponse();
		ajr.setCid(000);
		ajr.setError("test error ?");
		ajr.setBase64img("Random");
		ajr.setUserId("tester");
		ajr.setList(new ArrayList<Calculation>());
	}

	@Test
	public void SerializeApiJsonResponse() throws JsonProcessingException {
		ApiJsonResponse ajr = new ApiJsonResponse("userId", 10, "Error string", Collections.emptyList(), "base64String");
		String jsonInString = mapper.writeValueAsString(ajr);
		// System.out.println(jsonInString);
		assertTrue(jsonInString != null);
	}

	@Test
	public void UnserializeApiJsonResponse() throws IOException {
		ApiJsonResponse ajr = new ApiJsonResponse("tester", 0, "test error ?", Collections.emptyList(), "Random");
		String jsonAjr = "{\"userId\":\"tester\",\"cid\":0,\"error\":\"test error ?\",\"list\":[],\"base64img\":\"Random\"}\n"
				+ "";
		ApiJsonResponse ajr2 = mapper.readValue(jsonAjr, ApiJsonResponse.class);
		assertEquals(ajr, ajr2);
	}
}
