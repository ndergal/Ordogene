package org.ordogene.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.master.Master;
import org.ordogene.file.FileService;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class CalculationController {

	@Autowired
	FileService fs;

	@Autowired
	Master masterAlgo;

	private static final Map<Integer, String> currentCalculation = new HashMap<>();
	private final Object token = new Object();

	@RequestMapping(value = "/{id}/calculations",method = RequestMethod.GET, consumes = "text/plain")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getUserCalculations(@PathVariable String id) {

		if (id == null) { // never
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not
			// exist");
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(null, 0, "id can't be null", null, null),
					HttpStatus.BAD_REQUEST);
		}

		if (!fs.userExist(id)) {
			// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not
			// exist");
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(null, 0, id + " does not exist", null, null),
					HttpStatus.NOT_FOUND);
		} else {
			List<Calculation> calculations = fs.getUserCalculations(id);
			StringBuilder sb = new StringBuilder();
			calculations.forEach(c -> sb.append(c).append('\n'));
			// return ResponseEntity.ok().body(sb.toString());
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(null, 0, null, calculations, null),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{uid}/calculations", method = RequestMethod.PUT /*, consumes = MediaType.APPLICATION_JSON_VALUE */)
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> launchCalculation(@PathVariable String uid, @RequestBody String jsonBody) {

		if (uid == null) {
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not
			// exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, "the user can't be null", null, null), HttpStatus.BAD_REQUEST);
		}

		if (jsonBody == null) {
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not
			// exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, "the query can't be null, should send JSON object.", null, null),
					HttpStatus.BAD_REQUEST);
		}

		boolean exist = fs.userExist(uid);

		if (!exist) {
			// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not
			// exist");
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(null, 0, uid + " does not exist", null, null),
					HttpStatus.NOT_FOUND);
		}

		int calculationId = 0;
		try {
			calculationId= this.masterAlgo.compute(uid, jsonBody);
		} catch (JsonParseException e) {
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, "Invalid JSON (JsonParseException) ", null, null),
					HttpStatus.NOT_FOUND);
		} catch (JsonMappingException e) {
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, "Invalid JSON (JsonMappingException) ", null, null),
					HttpStatus.NOT_FOUND);
		} catch (InstantiationException e) {
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, "Invalid JSON (InstantiationException) ", null, null),
					HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, sStackTrace, null, null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UnmarshalException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, sStackTrace, null, null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, sStackTrace, null, null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InterruptedException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, sStackTrace, null, null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(uid, calculationId, null, null, null), HttpStatus.OK);
	}
}