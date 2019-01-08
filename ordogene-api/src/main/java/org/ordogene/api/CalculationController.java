package org.ordogene.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.master.Master;
import org.ordogene.api.utils.ApiJsonResponseCreator;
import org.ordogene.file.FileUtils;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * API Class containing routes to interact with calculations (get as list, launch, remove, stop, download)
 * @author darwinners team
 *
 */
@RestController
public class CalculationController {
	private static final Logger log = LoggerFactory.getLogger(CalculationController.class);

	@Autowired
	private Master masterAlgorithme;

	/**
	 *
	 * @param userId : owner of the calculation listed
	 * @return ResponseEntity<APiJsonResponse> which contains calculation list for the user, with Http code : 200 (or Http code 400 if the userId given is null, or 404 if the user does not exists) 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{userId}/calculations", produces = "application/json")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getUserCalculations(@PathVariable String userId) {

		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (!FileUtils.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);
		} else {
			// Do list
			List<Calculation> calculations = FileUtils.getUserCalculations(userId);
			calculations.forEach(c -> {
				try {
					masterAlgorithme.updateCalculation(c, userId);
				} catch (InternalError e) {
					log.error("Problem with calculation format informations");
					return;
				}
			});
			// Return it
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.listCalculation(calculations),
					HttpStatus.OK);
		}
	}
	

	/**
	 *
	 * @param userId : owner of the calculation to delete
	 * @param calculationId : id of the calculation to delete
	 * @return ResponseEntity<APiJsonResponse> with Http code : 200 (or Http code 400 if the userId given is null or the calculationId invalid, 
	 * or 404 if the user does not exists) 
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/calculations/{calculationId}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> removeCalculation(@PathVariable String userId,
			@PathVariable int calculationId) {
		if (userId == null || "".equals(userId))
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);

		if (!FileUtils.userExist(userId))
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);

		Optional<Calculation> optCalc = FileUtils.getUserCalculations(userId).stream().filter(c -> c.getId() == calculationId)
				.findFirst();

		if (!optCalc.isPresent()) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.calculationIDNotExist(calculationId),
					HttpStatus.NOT_FOUND);
		} else {
			Calculation calcToDelete = optCalc.get();
			if(masterAlgorithme.isRunning(calculationId)) {
				return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.calculationIDNotExist(calculationId),
						HttpStatus.BAD_REQUEST);
			}
			if (FileUtils.removeUserCalculation(userId,calcToDelete.getId(),calcToDelete.getName())) {
				return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(userId, calculationId, null, null, null),
						HttpStatus.OK);
			} else {
				return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.InternalServerError(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
		}

	}

	/**
	 *
	 * @param userId : owner of the calculation to stop
	 * @param calculationId : id of the calculation to stop
	 * @return ResponseEntity<APiJsonResponse> with Http code : 200 (or Http code 400 if the userId given is null or the calculationId invalid (does not exists or not running), 
	 * or 404 if the user does not exists) 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/{userId}/calculations/{calculationId}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> stopCalculation(@PathVariable String userId,
			@PathVariable int calculationId) {
		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (!FileUtils.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);
		} else {
			if (FileUtils.getUserCalculations(userId).stream().anyMatch(c -> c.getId() == calculationId)) {
				if (masterAlgorithme.interruptCalculation(calculationId)) {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, calculationId, null, null, null), HttpStatus.OK);
				} else {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, calculationId, "The calcul is not running.", null, null),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<ApiJsonResponse>(
						new ApiJsonResponse(userId, calculationId, "The calculationId is wrong", null, null),
						HttpStatus.FORBIDDEN);
			}
		}
	}

	/**
	 *
	 * @param userId : owner of the calculation to launch
	 * @param jsonBody : json of the calculation to launch
	 * @return ResponseEntity<APiJsonResponse> containing the calculation id with Http code : 200 (or Http code 400 if the userId given is null or the json invalid, 
	 * 503 if the server is full, or 404 if the user does not exists) 
	 */
	@RequestMapping(value = "/{userId}/calculations", method = RequestMethod.PUT )
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> launchCalculation(@PathVariable String userId,
			@RequestBody String jsonBody) {
		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (jsonBody == null || "".equals(jsonBody)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.jsonBodyNull(), HttpStatus.BAD_REQUEST);
		}
		if (!FileUtils.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);
		}
		try {
			Integer calculationId = this.masterAlgorithme.compute(userId, jsonBody);
			if (calculationId == null) {
				return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.serverFull(),
						HttpStatus.SERVICE_UNAVAILABLE);
			}
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(userId, calculationId, null, null, null),
					HttpStatus.OK);
		} catch (JsonParseException e) {
			log.error("Problem during Json parsing", e);
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.jsonInvalid(userId, "JsonParseException"),
					HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			log.error("Problem during Json mapping", e);
			return new ResponseEntity<ApiJsonResponse>(
					ApiJsonResponseCreator.jsonInvalid(userId, "JsonMappingException"), HttpStatus.BAD_REQUEST);
		} catch (UnmarshalException e) {
			log.error("Missing fields in the JSON", e);
			return new ResponseEntity<ApiJsonResponse>(
					ApiJsonResponseCreator.jsonInvalid(userId, "Missing fields in the JSON"), HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			log.error("I/O problem", e);
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.InternalServerError(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 *
	 * @param userId : owner of the calculation to get
	 * @param calculationid : id of the calculation to get
	 * @return ResponseEntity<APiJsonResponse> containing the base 64 of the calculation result (image)  Http code : 200 (or Http code 400 if the userId given is null or 404 if the calculation or user does not exists) 
	 */
	@RequestMapping(value = "/{userId}/calculations/{calculationid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getCalculationPng(@PathVariable String userId, @PathVariable int calculationid) {

		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}

		if (!FileUtils.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId), HttpStatus.NOT_FOUND);
		} else {
			List<Calculation> calculations = FileUtils.getUserCalculations(userId);
			Optional<Calculation> calcul = calculations.stream().filter(x -> x.getId() == calculationid).findFirst();
			if (calcul.isPresent()) {

				try {
					String base64img = FileUtils.encodeFile(Paths.get(FileUtils.getCalculationDirectoryPath(userId, calculationid, calcul.get().getName()) + File.separator + "result.png"));
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, calculationid, null, null, base64img), HttpStatus.OK);
				} catch (IOException e) {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, 0, "The result does not exist", null, null),
							HttpStatus.NOT_FOUND);
				}

			}
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(userId, 0,
					"calculation " + calculationid + " does not exist for user " + userId, null, null),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 *
	 * @param userId : owner of the calculation to get
	 * @param calculationid : id of the calculation to get
	 * @return ResponseEntity<APiJsonResponse> containing the base 64 of the calculation result (html)  Http code : 200 (or Http code 400 if the userId given is null or 404 if the calculation or user does not exists) 
	 */
	@RequestMapping(value = "/{userId}/calculations/{calculationid}/html", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getCalculationHtml(@PathVariable String userId, @PathVariable int calculationid) {

		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}

		if (!FileUtils.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId), HttpStatus.NOT_FOUND);
		} else {
			List<Calculation> calculations = FileUtils.getUserCalculations(userId);
			Optional<Calculation> calcul = calculations.stream().filter(x -> x.getId() == calculationid).findFirst();
			if (calcul.isPresent()) {

				try {
					String b64html = FileUtils.encodeFile(Paths.get(FileUtils.getCalculationDirectoryPath(userId, calculationid, calcul.get().getName()) + File.separator + "result.html"));
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, calculationid, null, null, b64html), HttpStatus.OK);
				} catch (IOException e) {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, 0, "cannot open calculation path", null, null),
							HttpStatus.NOT_FOUND);
				}

			}
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(userId, 0,
					"calculation " + calculationid + " does not exist for user " + userId, null, null),
					HttpStatus.NOT_FOUND);
		}
	}
}