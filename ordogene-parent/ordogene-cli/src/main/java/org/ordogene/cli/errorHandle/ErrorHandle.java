package org.ordogene.cli.errorHandle;

import java.io.IOException;

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
		switch (statusCode.series()) {
		case CLIENT_ERROR:
			throw new HttpClientErrorException(statusCode, response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		case SERVER_ERROR:
			throw new HttpServerErrorException(statusCode, response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		default:
			throw new RestClientException("Unknown status code [" + statusCode + "]");
	}
	}

}
