package org.ordogene.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.file.FileService;
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
		/*InputStream is = Mockito.mock(InputStream.class);
		commands.login();
		System.setIn(in);*/
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserClientException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);
		assertFalse(commands.getUser("tartampion"));
	}
	
	@Test
	public void testGetUserServerException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpServerErrorException.class);
		assertFalse(commands.getUser("tartampion"));
	}
	
	@Test
	public void testGetUserRestException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		assertFalse(commands.getUser("tartampion"));
	}
	
	@Test
	public void testGetUser() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(new ResponseEntity<ApiJsonResponse>(HttpStatus.OK));
		assertTrue(commands.getUser("tartampion"));
	}

	@Test
	public void testCreateUserRestException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		assertFalse(commands.createUser());
	}
	
	@Test
	public void testCreateUserClientException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);
		assertFalse(commands.createUser());
	}
	
	@Test
	public void testCreateUserServerException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpServerErrorException.class);
		assertFalse(commands.createUser());
	}
	
	@Test
	public void testCreateUser() {
		ResponseEntity<ApiJsonResponse> response = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(response);
		when(response.getBody())
		.thenReturn(ajr);
		when(ajr.getUserId())
		.thenReturn("tartampion");
		assertTrue(commands.createUser());
	}

	@Test
	public void testListCalculationsServerException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpServerErrorException.class);
		assertNull(commands.listCalculations());
	}
	
	@Test
	public void testListCalculationsClientException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);
		assertNull(commands.listCalculations());
	}
	
	@Test
	public void testListCalculationsRestException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		assertNull(commands.listCalculations());
	}
	
	@Test
	public void testListCalculationsNull() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		List<Calculation> list = mock(List.class);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getList()).thenReturn(null);
		assertNull(commands.listCalculations());
	}
	
	@Test
	public void testListCalculationsEmpty() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		List<Calculation> list = mock(List.class);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getList()).thenReturn(list);
		when(list.isEmpty()).thenReturn(true);
		assertNull(commands.listCalculations());
	}
	
	@Test
	public void testListCalculations() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		List<Calculation> list = mock(List.class);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getList()).thenReturn(list);
		assertNotNull(commands.listCalculations());
	}
	
	@Test
	public void testLaunchCalculationServerException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpServerErrorException.class);
		assertFalse(commands.launchCalculation(new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}
	
	@Test
	public void testLaunchCalculationClientException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);		
		assertFalse(commands.launchCalculation(new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}
	
	@Test
	public void testLaunchCalculationRestException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);		
		assertFalse(commands.launchCalculation(new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}
	
	@Test
	public void testLaunchCalculation() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getCid()).thenReturn(666);
		assertTrue(commands.launchCalculation(new File("../ordogene-api/src/test/resources/OrdogeneCalculationExamples/fitness1.json")));
	}

	@Test
	public void testStopCalculationServerException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpServerErrorException.class);
		assertFalse(commands.stopCalculation(666));
	}
	
	@Test
	public void testStopCalculationClientException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);
		assertFalse(commands.stopCalculation(666));
	}
	
	@Test
	public void testStopCalculationRestException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		assertFalse(commands.stopCalculation(666));
	}
	
	@Test
	public void testStopCalculation() {
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(re);
		assertTrue(commands.stopCalculation(666));
	}

	@Test
	public void testResultCalculationServerException() {
		String dst = "/tmp/result.png";
		Path path = mock(Path.class);
		when(Paths.get(dst)).thenReturn(path);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpServerErrorException.class);
		assertFalse(commands.resultCalculation(666, dst, true));
	}
	
	@Test
	public void testResultCalculationClientException() {
		String dst = "/tmp/result.png";
		Path path = mock(Path.class);
		when(Paths.get(dst)).thenReturn(path);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);
		assertFalse(commands.resultCalculation(666, dst, true));
	}
	
	@Test
	public void testResultCalculationRestException() {
		String dst = "/tmp/result.png";
		Path path = mock(Path.class);
		when(Paths.get(dst)).thenReturn(path);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		assertFalse(commands.resultCalculation(666, dst, true));
	}
	
	@Test
	public void testResultCalculation() {
		String dst = "/tmp/result.png";
		Path path = mock(Path.class);
		ResponseEntity<ApiJsonResponse> re = mock(ResponseEntity.class);
		ApiJsonResponse ajr = mock(ApiJsonResponse.class);
		String base64img = mock(String.class);
		when(Paths.get(dst)).thenReturn(path);
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(re);
		when(re.getBody()).thenReturn(ajr);
		when(ajr.getBase64img()).thenReturn(base64img);
		when(FileService.decodeAndSaveImage(base64img, dst)).thenReturn(true);
		assertTrue(commands.resultCalculation(666, dst, true));
	}

	@Ignore
	@Test
	public void testRemoveCalculation() {
		fail("Not yet implemented");
	}

}
