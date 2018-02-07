package org.ordogene.api;

import org.ordogene.file.FileService;
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

	@RequestMapping(value = "/checkUserId/{user_ID}")
	// @RequestMapping(value = "/checkUserId/{user_ID}", produces =
	// "application/json")
	@ResponseBody
	public ResponseEntity<String> exists(@PathVariable String user_ID) {

		if (user_ID == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user_ID + " n'existe pas");
		}

		boolean exist = fs.userExist(user_ID);

		if (!exist) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user_ID + " n'existe pas");
		} else {
			return ResponseEntity.ok().body(user_ID + " existe");
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/createUserId")
	@ResponseBody
	public ResponseEntity<String> create() {
		String randomId = fs.generateRandomUserId(5);
		while (fs.userExist(randomId)) {
			randomId = fs.generateRandomUserId(5);
		}
		if(fs.addUser(randomId)) {
			return ResponseEntity.ok().body(randomId);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(randomId + " : erreur...");
	}
}