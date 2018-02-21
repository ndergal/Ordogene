package org.ordogene.cli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.cli.errorHandle.ErrorHandle;
import org.ordogene.file.utils.ApiJsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandleTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private Commands commands;

	private static final ObjectMapper mapper = new ObjectMapper();

	@Test(expected = HttpClientErrorException.class)
	public void handleError401Test() throws IOException {
 		ApiJsonResponse ajr = new ApiJsonResponse(null,0,"TestError : CLIENT_ERROR",null,null);
 		String strBody = mapper.writeValueAsString(ajr);
		InputStream is = new ByteArrayInputStream(strBody.getBytes(StandardCharsets.UTF_8));
		ClientHttpResponse errorHttpResponse = new MockClientHttpResponse(is, HttpStatus.BAD_REQUEST);
		ErrorHandle eh = new ErrorHandle();
		eh.handleError(errorHttpResponse);
	}

	@Test(expected = HttpServerErrorException.class)
	public void handleError500Test() throws IOException {
		ApiJsonResponse ajr = new ApiJsonResponse(null, 0, "TestError : SERVER_ERROR", null, null);
 		String strBody = mapper.writeValueAsString(ajr);
		InputStream is = new ByteArrayInputStream(strBody.getBytes(StandardCharsets.UTF_8));
		ClientHttpResponse errorHttpResponse = new MockClientHttpResponse(is, HttpStatus.INTERNAL_SERVER_ERROR);
		ErrorHandle eh = new ErrorHandle();
		eh.handleError(errorHttpResponse);
	}
	
	@Test(expected = RestClientException.class)
	public void handleErrorNoHandledTest() throws IOException {
		ApiJsonResponse ajr = new ApiJsonResponse(null, 0, "TestError : Carabistouille_Error", null, null);
 		String strBody = mapper.writeValueAsString(ajr);
		InputStream is = new ByteArrayInputStream(strBody.getBytes(StandardCharsets.UTF_8));
		ClientHttpResponse errorHttpResponse = new MockClientHttpResponse(is, HttpStatus.CHECKPOINT);
		ErrorHandle eh = new ErrorHandle();
		eh.handleError(errorHttpResponse);
	}
}
