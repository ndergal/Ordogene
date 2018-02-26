package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	@Test
	public void should_cover_hashCode_method() {
		ApiJsonResponse ajr1 = new ApiJsonResponse();
		ApiJsonResponse ajr2 = new ApiJsonResponse();

		assertEquals(ajr1.hashCode(), ajr2.hashCode());
		
		ajr1 = new ApiJsonResponse("", 0, "", new ArrayList<>(), "");
		ajr2 = new ApiJsonResponse("", 0, "", new ArrayList<>(), "");

		assertEquals(ajr1.hashCode(), ajr2.hashCode());
	}

	@Test
	public void test_all_possible_state_of_equals() {
		ApiJsonResponse ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");

		// same
		assertTrue(ajr.equals(ajr));

		// null
		assertFalse(ajr.equals(null));

		ApiJsonResponse ajr2 = new ApiJsonResponse();
		assertFalse(ajr.equals(ajr2));
		
		// != class
		assertFalse(ajr.equals(new Object()));

		// != cid
		ajr2 = new ApiJsonResponse("user", 1, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));

		// error null && other.error != null
		ajr = new ApiJsonResponse("user", 0, null, new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, null, new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));
		
		// list null
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", null, "a");
		assertFalse(ajr.equals(ajr2));
		ajr = new ApiJsonResponse("user", 0, "err", null, "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));

		//base64 img
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), null);
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), null);
		assertFalse(ajr.equals(ajr2));
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "b");
		assertFalse(ajr.equals(ajr2));

		//userid img
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse(null, 0, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));
		ajr = new ApiJsonResponse(null, 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));
		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user1", 0, "err", new ArrayList<>(), "a");
		assertFalse(ajr.equals(ajr2));

		ajr = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		ajr2 = new ApiJsonResponse("user", 0, "err", new ArrayList<>(), "a");
		assertTrue(ajr.equals(ajr2));
	}
}
