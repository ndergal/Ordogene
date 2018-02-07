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

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	// @RequestMapping(value = "/checkUserId/{user_ID}", produces =
	// "application/json")
	@ResponseBody
	public ResponseEntity<String> exists(@PathVariable String id) {

		if (id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " n'existe pas");
		}

		boolean exist = fs.userExist(id);

		if (!exist) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " n'existe pas");
		} else {
			return ResponseEntity.ok().body(id + " existe");
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public ResponseEntity<String> createUserGivenId(@PathVariable String id) {
		if (id == null || id.equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'id ne peut pas être vide.");
		}
		if(fs.addUser(id)) {
			return ResponseEntity.ok().body(id);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " : erreur...");
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/")
	@ResponseBody
	public ResponseEntity<String> createUserRandomId() {
		int nbCharRandom = 7;
		String randomId = fs.generateRandomUserId(nbCharRandom);
		while (fs.userExist(randomId)) {
			randomId = fs.generateRandomUserId(nbCharRandom);
		}
		if(fs.addUser(randomId)) {
			return ResponseEntity.ok().body(randomId);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(randomId + " : erreur...");
	}
	
 
}