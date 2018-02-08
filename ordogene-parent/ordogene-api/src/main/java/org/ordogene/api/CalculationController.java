package org.ordogene.api;

import java.util.List;

import org.ordogene.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculationController {

	@Autowired
	FileService fs;

	@RequestMapping(value = "/{id}/calculations")
	@ResponseBody
	public ResponseEntity<String> getUserCalculations(@PathVariable String id) {

		if (id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not exist");
		}

		boolean exist = fs.userExist(id);

		if (!exist) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not exist");
		} else {
			List<String> calculations = fs.getUserCalculations(id);
			StringBuilder sb = new StringBuilder();
			calculations.forEach(c -> sb.append(c).append('\n'));
			return ResponseEntity.ok().body(sb.toString());
		}
	}

}