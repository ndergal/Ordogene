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
		switch (statusCode.series()) {
		case CLIENT_ERROR:
			byte[] responseBody = getResponseBody(response);
			String statusText = "";
			try {
				ApiJsonResponse ajr = (ApiJsonResponse) Parser.parseJsonFile(responseBody, ApiJsonResponse.class);
				statusText = ajr.getError();
			} catch (UnmarshalException e) {
				// Nothing to do because never append (normally)
			}
			throw new HttpClientErrorException(statusCode, statusText,
					response.getHeaders(), responseBody, getCharset(response));
		case SERVER_ERROR:
			throw new HttpServerErrorException(statusCode, response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		default:
			throw new RestClientException("Unknown status code [" + statusCode + "]");
	}
	}

}
