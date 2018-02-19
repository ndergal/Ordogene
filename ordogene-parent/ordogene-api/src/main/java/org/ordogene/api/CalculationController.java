package org.ordogene.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.master.Master;
import org.ordogene.api.utils.ApiJsonResponseCreator;
import org.ordogene.file.FileService;
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

@RestController
public class CalculationController {
	private static final Logger log = LoggerFactory.getLogger(CalculationController.class);

	@Autowired
	private FileService fs;

	@Autowired
	private Master masterAlgorithme;

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{userId}/calculations", produces = "application/json")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getUserCalculations(@PathVariable String userId) {

		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (!fs.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);
		} else {
			// Do list
			List<Calculation> calculations = fs.getUserCalculations(userId);
			calculations.forEach(c -> {
				try {
					masterAlgorithme.updateCalculation(c, userId);
				} catch (InternalError e) {
					System.err.println("Problem with calculation format informations");
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
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/{userId}/calculations/{calculationId}"/*, produces = "application/json"*/)
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> stopCalculation(@PathVariable String userId,
			@PathVariable int calculationId) {
		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (!fs.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);
		} else {
			if (fs.getUserCalculations(userId).stream().anyMatch(c -> c.getId() == calculationId)) {
				if (masterAlgorithme.interruptCalculation(calculationId)) {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, calculationId, null, null, null), HttpStatus.OK);
				} else {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(userId, calculationId, "The calcul is not running.", null, null),
							HttpStatus.NOT_FOUND);
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
	 * @param userId
	 * @param jsonBody
	 * @return
	 */
	@RequestMapping(value = "/{userId}/calculations", method = RequestMethod.PUT /*
																					 * , consumes =
																					 * MediaType.APPLICATION_JSON_VALUE
																					 */)
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> launchCalculation(@PathVariable String userId,
			@RequestBody String jsonBody) {
		if (userId == null || "".equals(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNull(), HttpStatus.BAD_REQUEST);
		}
		if (jsonBody == null|| "".equals(jsonBody)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.jsonBodyNull(), HttpStatus.BAD_REQUEST);
		}
		if (!fs.userExist(userId)) {
			return new ResponseEntity<ApiJsonResponse>(ApiJsonResponseCreator.userIdNotExist(userId),
					HttpStatus.NOT_FOUND);
		}
		try {
			int calculationId = this.masterAlgorithme.compute(userId, jsonBody);
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

	@RequestMapping(value = "/{id}/calculations/{calculationid}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getCalculation(@PathVariable String id, @PathVariable int calculationid) {

		if (id == null) {
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(null, 0, "id can't be null", null, null),
					HttpStatus.BAD_REQUEST);
		}

		if (!fs.userExist(id)) {
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(null, 0, id + " does not exist", null, null),
					HttpStatus.NOT_FOUND);
		} else {
			List<Calculation> calculations = fs.getUserCalculations(id);
			Optional<Calculation> calcul = calculations.stream().filter(x -> x.getId() == calculationid).findFirst();
			if (calcul.isPresent()) {

				try {
					Path imgPath = Paths.get(FileService.getCalculationPath(id, calcul.get()));
					String base64img = FileService.encodeImage(imgPath);
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(id, calculationid, null, null, base64img), HttpStatus.OK);
				} catch (FileNotFoundException e) {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(id, 0, "cannot find calculation path", null, null),
							HttpStatus.NOT_FOUND);
				} catch (IOException e) {
					return new ResponseEntity<ApiJsonResponse>(
							new ApiJsonResponse(id, 0, "cannot open calculation path", null, null),
							HttpStatus.NOT_FOUND);
				}

			}
			return new ResponseEntity<ApiJsonResponse>(new ApiJsonResponse(id, 0,
					"calculation " + calculationid + " does not exist for user " + id, null, null),
					HttpStatus.NOT_FOUND);
		}
	}
}