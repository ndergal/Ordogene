package org.ordogene.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.file.utils.ApiJsonResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class CommandsTest {
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private Commands commands;

	@Test
	public void testLogin() {
		/*InputStream is = Mockito.mock(InputStream.class);
		commands.login();
		System.setIn(in);
		fail("Not yet implemented");*/
	}

	@Test
	public void testGetUserFalse() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(HttpClientErrorException.class);
		assertFalse(commands.getUser("tartampion"));
	}
	
	@Test
	public void testGetUserTrue() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenReturn(new ResponseEntity<ApiJsonResponse>(HttpStatus.OK));
		assertTrue(commands.getUser("tartampion"));
	}

	@Test
	public void testCreateUserException() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		commands.createUser();
	}
	
	@Test
	public void testCreateUser() {
		when(restTemplate.exchange(
				anyString(),
				any(HttpMethod.class), 
				any(HttpEntity.class), 
				any(Class.class)))
		.thenThrow(RestClientException.class);
		commands.createUser();
	}

	@Test
	public void testListCalculations() {
		fail("Not yet implemented");
	}

	@Test
	public void testLaunchCalculation() {
		fail("Not yet implemented");
	}

	@Test
	public void testStopCalculation() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveCalculation() {
		fail("Not yet implemented");
	}

	@Test
	public void testResultCalculation() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsHttpCodeValid() {
		fail("Not yet implemented");
	}

}
