package org.ordogene.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class CommandsTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private Commands commands;

	@Ignore
	@Test
	public void testLogin() {
		/*
		 * InputStream is = Mockito.mock(InputStream.class); commands.login();
		 * System.setIn(in);
		 */
		fail("Not yet implemented");
	}

	@Test
	public void testListCalculationsServerException() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(HttpServerErrorException.class);
		assertNull(commands.listCalculations());
	}

	@Test
	public void testListCalculationsClientException() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(HttpClientErrorException.class);
		assertNull(commands.listCalculations());
	}

	@Test
	public void testListCalculationsRestException() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(RestClientException.class);
		assertNull(commands.listCalculations());
	}

	@Test
	public void testListCalculationsNull() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		List<Calculation> list = mock(List.class);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getList()).thenReturn(null);
		assertNull(commands.listCalculations());
	}

	@Test
	public void testListCalculationsEmpty() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getList()).thenReturn(Collections.emptyList());
		assertNull(commands.listCalculations());
	}

	@Test
	public void testListCalculations() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		List<Calculation> list = new ArrayList<>();
		Calculation c = mock(Calculation.class);
		
		list.add(c);
		
		when(c.getFitnessSaved()).thenReturn(10L);
		when(c.getId()).thenReturn(84923213);
		when(c.getIterationNumber()).thenReturn(42);
		when(c.getLastIterationSaved()).thenReturn(31L);
		when(c.getMaxIteration()).thenReturn(100);
		when(c.getName()).thenReturn("name");
		when(c.getStartTimestamp()).thenReturn((new Date()).getTime());
		
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getList()).thenReturn(list);
		assertNotNull(commands.listCalculations());
	}

	@Test
	public void testLaunchCalculationServerException() {
		HttpServerErrorException e = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test status text");
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(e);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " -- test status text", commands.launchCalculation(
				new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}

	@Test
	public void testLaunchCalculationClientException() {
		HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "test status text");
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(e);
		assertEquals(HttpStatus.BAD_REQUEST.value() + " -- test status text", commands.launchCalculation(
				new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}

	@Test
	public void testLaunchCalculationRestException() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(RestClientException.class);
		assertEquals("Problem with the communication between client and server", commands.launchCalculation(
				new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}

	@Test
	public void testLaunchCalculation() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getCid()).thenReturn(666);
		assertEquals("Calculation '666' launched", commands.launchCalculation(
				new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}

	@Test
	public void testStopCalculationServerException() {
		HttpServerErrorException e = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test status text");
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(e);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " -- test status text", commands.stopCalculation(666));
	}

	@Test
	public void testStopCalculationClientException() {
		HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "test status text");
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(e);
		assertEquals(HttpStatus.BAD_REQUEST.value() + " -- test status text", commands.stopCalculation(666));
	}

	@Test
	public void testStopCalculationRestException() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(RestClientException.class);
		assertEquals("Problem with the communication between client and server", commands.stopCalculation(666));
	}

	@Test
	public void testStopCalculation() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(re);
		assertEquals("Calculation '666' stopped", commands.stopCalculation(666));
	}

	@Test
	public void testResultCalculationServerException() {
		HttpServerErrorException e = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test status text");
		String dst = "/tmp/result.png";
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(e);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " -- test status text",  commands.resultCalculation(666, new File(dst), true,false));
	}

	@Test
	public void testResultCalculationClientException() {
		HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "test status text");
		String dst = "/tmp/result.png";
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(e);
		assertEquals(HttpStatus.BAD_REQUEST.value() + " -- test status text", commands.resultCalculation(666, new File(dst), true,false));
	}

	@Test
	public void testResultCalculationRestException() {
		String dst = "/tmp/result.png";
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenThrow(RestClientException.class);
		assertEquals("Problem with the communication between client and server", commands.resultCalculation(666, new File(dst), true,false));
	}

	@Test
	public void testResultCalculationPng() {
		String dst = "/tmp/result.png";
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		String base64img = "tartampion";
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getBase64img()).thenReturn(base64img);
		assertEquals("The image of the result is downloaded at " + dst, commands.resultCalculation(666, new File(dst), true,false));
	}
	
	@Test
	public void testRemoveCalculationServerException() {
		HttpServerErrorException e = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test status text");
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
		.thenThrow(e);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " -- test status text", commands.removeCalculation(0));
	}
	
	@Test
	public void testRemoveCalculationClientException() {
		HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "test status text");
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
		.thenThrow(e);

		assertEquals(HttpStatus.BAD_REQUEST.value() + " -- test status text", commands.removeCalculation(0));
	}
	
	@Test
	public void testRemoveCalculationRestException() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
		.thenThrow(
				RestClientException.class);

		assertEquals("Problem with the communication between client and server", commands.removeCalculation(0));
	}

	@Test
	public void testRemoveCalculation() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		
		when(re.getBody()).thenReturn(ajr);
		
		when(restTemplate.exchange(anyString(), 
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class))).thenReturn(re);
		
		assertEquals("Calculation '0' has been deleted.", commands.removeCalculation(0));
	}

}
