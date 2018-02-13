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
	private FileService fs;
	
	@Autowired
	private Master masterAlgorithme;
	
	private static String JSONTest = "{\n" + 
			"    \"snaps\" : [5,10,20,100],\n" + 
			"    \"slots\" : 300,\n" + 
			"    \"exec_time\" : 10000,\n" + 
			"    \"environment\" : [\n" + 
			"		{\"name\" : \"FUEL\", \"quantity\" : 200},\n" + 
			"		{\"name\" : \"BIG_GOOD\", \"quantity\" : 0},\n" + 
			"		{\"name\" : \"SMALL_BAD\", \"quantity\" : 0}\n" + 
			"    ],\n" + 
			"    \"actions\" : [\n" + 
			"		{\n" + 
			"			\"name\" : \"MAKE_GOOD\", \"time\" : 5,\n" + 
			"			\"input\" : [\n" + 
			"		     	{ \"name\" : \"FUEL\", \"quantity\" : 60, \"relation\" : \"c\" }\n" + 
			"		 	],\n" + 
			"		 	\"output\" : [\n" + 
			"		    	{\"name\" : \"BIG_GOOD\", \"quantity\" : 1}\n" + 
			"		 	]\n" + 
			"		},\n" + 
			"		{\n" + 
			"			\"name\" : \"MAKE_BAD\", \"time\" : 2,\n" + 
			"		 	\"input\" : [\n" + 
			"		     	{ \"name\" : \"FUEL\", \"quantity\" : 6, \"relation\" : \"c\" }\n" + 
			"		 	],\n" + 
			"		 	\"output\" : [\n" + 
			"		     	{\"name\" : \"SMALL_BAD\", \"quantity\" : 1}\n" + 
			"		 	]\n" + 
			"		}\n" + 
			"    ],\n" + 
			"    \"fitness\" : {\n" + 
			"		\"type\" : \"max\",\n" + 
			"		\"operands\" : [\n" + 
			"		    {\"name\" : \"BIG_GOOD\", \"coef\" : 11},\n" + 
			"		    {\"name\" : \"SMALL_BAD\", \"coef\" : 1}\n" + 
			"		]\n" + 
			"    }\n" + 
			"}";
	
	

	@Autowired
	Master masterAlgo;

	private static final Map<Integer, String> currentCalculation = new HashMap<>();
	private final Object token = new Object();


	@RequestMapping(method = RequestMethod.GET, value = "/{userId}/calculations", produces = "application/json")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getUserCalculations(@PathVariable String userId) {
		if (userId == null) { // never
			//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, "id can't be null", null, null),
					HttpStatus.BAD_REQUEST
				);
		}
		if (!fs.userExist(userId)) {
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, userId + " does not exist", null, null),
					HttpStatus.NOT_FOUND
				);
		} else {
			try {
				masterAlgorithme.compute(userId, JSONTest);
			} catch (InstantiationException | IllegalAccessException | UnmarshalException | IOException
					| InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			List<Calculation> calculations = fs.getUserCalculations(userId);
			StringBuilder sb = new StringBuilder();
			calculations.forEach(c -> {
				try {
					masterAlgorithme.updateCalculation(c);
				} catch (InternalError e) {
					System.err.println("Problem with calculation format informations");
					return;
				}
				sb.append(c).append('\n');
			});
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, null, calculations, null),
					HttpStatus.OK
				);
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