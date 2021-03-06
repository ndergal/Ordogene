package org.ordogene.api.utils;

import java.util.List;

import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;

public class ApiJsonResponseCreator {
	private ApiJsonResponseCreator() {
	}
	
	public static ApiJsonResponse userIdNull() {
		return new ApiJsonResponse(null, 0, "The userId to check is null or empty", null, null);
	}
	public static ApiJsonResponse userIdNotExist(String userId) {
		return new ApiJsonResponse(null, 0, "'" + userId + "' does not exist", null, null);
	}
	public static ApiJsonResponse userIdNotCreated() {
		return new ApiJsonResponse(null, 0, "Error : user not created ", null, null);
	}
	public static ApiJsonResponse userIdCreated(String userId) {
		return new ApiJsonResponse(userId, 0, null, null, null);
	}
	public static ApiJsonResponse listCalculation(List<Calculation> calculations) {
		return new ApiJsonResponse(null, 0, null, calculations, null);
	}
	public static ApiJsonResponse jsonBodyNull() {
		return new ApiJsonResponse(null, 0, "the query can't be null, should send JSON object.", null, null);
	}
	public static ApiJsonResponse jsonInvalid(String userId, String description) {
		return new ApiJsonResponse(userId, 0, "Invalid JSON (" + description + ") ", null, null);
	}
	public static ApiJsonResponse InternalServerError() {
		return new ApiJsonResponse(null, 0, "Internal server error", null, null);
	}
	public static ApiJsonResponse calculationIDNull() {
		return new ApiJsonResponse(null, 0, "The calculationId can't be null or empty", null, null);
	}
	public static ApiJsonResponse calculationIDNotExist(int cid) {
		return new ApiJsonResponse(null, 0, "The calculation "+cid+" doest not exist", null, null);
	}
	public static ApiJsonResponse serverFull() {
		return new ApiJsonResponse(null, 0, "The server can not run your calculation (the server is full)", null, null);
	}
	
	public static ApiJsonResponse calculationIDisRunning(int cid) {
		return new ApiJsonResponse(null, 0, "The calculation "+cid+" cannot be removed because it is currently running", null, null);
	}
	
	
	
}
