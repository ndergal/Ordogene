package org.ordogene.api;

import org.ordogene.api.utils.ApiJsonResponseCreator;
import org.ordogene.file.FileUtils;
import org.ordogene.file.utils.ApiJsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	
	/**
	 * Check if the user exist on file system and give a ResponseEntity<ApiJsonResponse>
	 * @param userId The user to check
	 * @return A ResponseEntity<ApiJsonResponse>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{userId}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> exists(@PathVariable String userId) {
		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (!FileUtils.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId), HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(userId, 0, null, null, null),HttpStatus.OK);
		}
	}
	
	/**
	 * Create a user on file system and give a ResponseEntity<ApiJsonResponse>
	 * @param userId The user to create
	 * @return A ResponseEntity<ApiJsonResponse>
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> createUserGivenId(@PathVariable String userId) {
		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if(FileUtils.addUser(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdCreated(userId),HttpStatus.OK);
		}
		return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotCreated(),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Create a random user on file system and give a ResponseEntity<ApiJsonResponse>
	 * @return A ResponseEntity<ApiJsonResponse>
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> createUserRandomId() {
		int nbCharRandom = 7;
		String randomId = FileUtils.generateRandomUserId(nbCharRandom);
		do {
			randomId = FileUtils.generateRandomUserId(nbCharRandom);
		} while (FileUtils.userExist(randomId));
		return createUserGivenId(randomId);
	}
	
 
}