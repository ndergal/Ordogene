package org.ordogene.api;

import org.ordogene.file.FileService;
import org.ordogene.file.utils.ApiJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	FileService fs;

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	// @RequestMapping(value = "/checkUserId/{user_ID}", produces =
	// "application/json")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> exists(@PathVariable String id) {

		if (id == null) {
			//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The id checked is null");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, "The id checked is null", null, null),
					HttpStatus.BAD_REQUEST
				);
		}

		boolean exist = fs.userExist(id);

		if (!exist) {
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, id + " does not exist", null, null),
					HttpStatus.NOT_FOUND
				);
		} else {
			//return ResponseEntity.ok().body(id + " exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, null, null, null),
					HttpStatus.OK
				);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> createUserGivenId(@PathVariable String id) {
		if (id == null || id.equals("")) {
			//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The id can't be empty.");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, "The id can't be empty.", null, null),
					HttpStatus.BAD_REQUEST
				);
		}
		if(fs.addUser(id)) {
			//return ResponseEntity.ok().body(id);
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, null, null, null),
					HttpStatus.OK
				);
		}
		//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : "+"user" + " not created ");
		return new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(null, 0, "Error : user not created ", null, null),
				HttpStatus.BAD_REQUEST
			);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> createUserRandomId() {
		int nbCharRandom = 7;
		String randomId = fs.generateRandomUserId(nbCharRandom);
		while (fs.userExist(randomId)) {
			randomId = fs.generateRandomUserId(nbCharRandom);
		}
		if(fs.addUser(randomId)) {
			//return ResponseEntity.ok().body(randomId);
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(randomId, 0, null, null, null),
					HttpStatus.OK
				);
		}
		//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : "+randomId + " not created ");
		return new ResponseEntity<ApiJsonResponse>(
				new ApiJsonResponse(null, 0, "Error : " + randomId + " not created", null, null),
				HttpStatus.BAD_REQUEST
			);
	}
	
 
}