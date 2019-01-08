package org.ordogene.cli.errorHandle;

import java.io.IOException;

import javax.xml.bind.UnmarshalException;

import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.ApiJsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

public class ErrorHandle extends DefaultResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = getHttpStatusCode(response);
		byte[] responseBody = getResponseBody(response);
		String error = extractResponseError(responseBody);
		switch (statusCode.series()) {
			case CLIENT_ERROR:
				if(error == null) {
					throw new HttpClientErrorException(statusCode, error,
							response.getHeaders(), responseBody, getCharset(response));
				} else {
					throw new HttpClientErrorException(statusCode, response.getStatusText(),
							response.getHeaders(), responseBody, getCharset(response));
				}
			case SERVER_ERROR:
				if(error == null) {
					throw new HttpServerErrorException(statusCode, error,
							response.getHeaders(), responseBody, getCharset(response));
				} else {
					throw new HttpServerErrorException(statusCode, response.getStatusText(),
							response.getHeaders(), responseBody, getCharset(response));
				}
			default:
				throw new RestClientException("Unknown status code [" + statusCode + "]");
		}
	}
	
	private String extractResponseError(byte[] responseBody) {
		try {
			ApiJsonResponse ajr = (ApiJsonResponse) Parser.parseJsonFile(responseBody, ApiJsonResponse.class);
			return ajr.getError();
		} catch (UnmarshalException | IOException e) {
			return null;
		}
	}

}
