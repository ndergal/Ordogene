package org.ordogene.api;

import java.io.IOException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

	@RequestMapping(method = RequestMethod.GET, value = "/{userId}/calculations/{calculationId}", produces = "image/png")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> getCalculationResult(@PathVariable String userId, @PathVariable String calculationId) {
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
			//TODO 
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{userId}/launchCalculation")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> launchCalculation(@PathVariable String userId) {

		if (userId == null) {
			//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, userId + " does not exist", null, null),
					HttpStatus.BAD_REQUEST
				);
		}

		if (!fs.userExist(userId)) {
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not exist");
			return new ResponseEntity<ApiJsonResponse>(
					new ApiJsonResponse(null, 0, userId + " does not exist", null, null),
					HttpStatus.NOT_FOUND
				);
		}
		
		//TODO
		return null;
//
//		int pid = 0;
//		try {
//			String command = "java -jar " + Const.getConst().get("JarAlgorithmPath");
//			Process proc = Runtime.getRuntime().exec(command);
//			Field f = proc.getClass().getDeclaredField("pid");
//			f.setAccessible(true);
//			pid = (int) f.get(proc);
//			synchronized (token) {
//				currentCalculation.put(pid, userId);
//			}
//			asynchronousDeletePid(proc, pid, userId);
//		} catch (Exception e) {
//			//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknow error... Sorry.");
//			return new ResponseEntity<ApiJsonResponse>(
//					new ApiJsonResponse(null, 0, "Unknow error... Sorry.", null, null),
//					HttpStatus.INTERNAL_SERVER_ERROR
//				);
//		}
//		//return ResponseEntity.ok().body("" + pid);
//		return new ResponseEntity<ApiJsonResponse>(
//				new ApiJsonResponse(null, 0, null, null, null),
//				HttpStatus.OK
//			);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{userId}/calculations/{calculationId}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> stopCalculation(@PathVariable String userId, @PathVariable String calculationId) {
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
			//TODO 
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/calculations/{calculationId}")
	@ResponseBody
	public ResponseEntity<ApiJsonResponse> deleteCalculation(@PathVariable String userId, @PathVariable String calculationId) {
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
			//TODO 
			return null;
		}
	}

//	private void asynchronousDeletePid(Process proc, int pid, String id) {
//
//		Runnable waitAndDeletePid = new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					// int exitVal = proc.waitFor();
//					int exitVal = proc.waitFor();
//					synchronized (token) {
//						if (currentCalculation.containsKey(pid)) {
//							currentCalculation.remove(pid);
//							System.out.println("remove " + pid + " from the map");
//						}
//					}
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		};
//
//		Thread t = new Thread(waitAndDeletePid);
//		t.setDaemon(true);
//		t.start();
//	}
}