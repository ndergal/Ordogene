package org.ordogene.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.ordogene.file.FileService;
import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Calculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.CellMatcher;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ShellComponent
public class Commands {

	private String id;
	private static final Logger log = LoggerFactory.getLogger(Commands.class);

	private final String[] headers = { "Id", "Name", "Date", "Running", "Fitness", "Iteration done",
			"Last iteration saved", "Max iteration" };

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void login() {
		System.out.println();
		System.out.print("Do you want to create a new id ? [y/N] : ");
		@SuppressWarnings("resource") //problem if the scanner is closed
		Scanner scanner = new Scanner(System.in);
		String choice = scanner.nextLine();

		switch(choice) {
			case "":
			case "n":
			case "N":
			case "no":
				do {
					System.out.print("Enter your id : ");
					id = scanner.nextLine();
				} while (id.isEmpty() || !getUser(id));
				break;
			case "y":
			case "Y":
			case "yes":
				createUser();
				break;

		}

		System.out.println();
	}

	
	public boolean getUser(String id) {
		//Request

		try {

			restTemplate.exchange("/" + id, HttpMethod.GET, null, ApiJsonResponse.class);
		} catch (HttpClientErrorException e) {
			log.error(e.getStatusCode() + " -- " + e.getStatusText());
			return false;

		}


		this.id = id;
		log.info("Welcome back " + id);
		return true;
	}

	public void createUser() {
		// Request
		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/", HttpMethod.PUT, null, ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			log.error("here");
			return;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return;
		}

		id = response.getBody().getUserId();
		log.info("Your new id is " + id);

	}

	/**
	 * Display a table containing calculations of the user
	 */
	@ShellMethod(value = "List calculations")
	public Table listCalculations() {
		// Request
		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/" + id + "/calculations", HttpMethod.GET, null, ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			return null;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return null;
		}

		// Build ascii table
		List<Calculation> list = response.getBody().getList();
		if (!(list != null && !list.isEmpty())) {

			log.info("No calculations yet");
			return null;
		}
		String[][] data = new String[list.size() + 1][headers.length];
		TableModel model = new ArrayTableModel(data);
		TableBuilder builder = new TableBuilder(model);
		for (int i = 0; i < headers.length; i++) {
			data[0][i] = headers[i];
			// builder.on(at(0, i)).
		}
		for (int i = 0; i < list.size(); i++) {
			Calculation c = list.get(i);
			data[i + 1][0] = String.valueOf(c.getId());
			data[i + 1][1] = c.getName();
			data[i + 1][2] = c.getDate();
			data[i + 1][3] = String.valueOf(c.isRunning());
			data[i + 1][4] = String.valueOf(c.getFitnessSaved());
			data[i + 1][5] = String.valueOf(c.getIterationNumber());
			data[i + 1][6] = String.valueOf(c.getLastIterationSaved());
			data[i + 1][7] = String.valueOf(c.getMaxIteration());

		}
		return builder.addFullBorder(BorderStyle.oldschool).build();
	}

	/**
	 * Launch a calculation based on the model
	 * 
	 * @param model
	 *            path to model to send
	 */
	@ShellMethod(value = "Launch a calculation from a model")
	public void launchCalculation(File model) {
		// Parameter validation
		Path jsonPath = model.toPath();
		if (Files.notExists(jsonPath)) {

			log.error("The path does not exist. Try again.");
			return;
		}

		if (Files.isDirectory(jsonPath)) {
			log.error(jsonPath + " is a directory. Try again.");
		}
		String jsonContentRead = null;
		try {
			jsonContentRead = new String(Files.readAllBytes(jsonPath));
		} catch (IOException e1) {
			log.error("Error while reading " + model);
			return;
		}

		// Request
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(jsonContentRead, headers);

		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/" + id + "/calculations/", HttpMethod.PUT, request,
					ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			return;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return;
		}

		int cid = response.getBody().getCid();
		log.info("Calculation '" + cid + "' launched");
	}

	/**
	 * Stop the calculation
	 * 
	 * @param cid
	 *            id of the calculation
	 */
	@ShellMethod(value = "Stop a calculation")
	public void stopCalculation(int cid) {
		// Request
		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/" + id + "/calculations/" + cid, HttpMethod.POST, null,
					ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			return;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return;
		}

		log.info("Calculation '" + cid + "' stopped");

	}

	/**
	 * Remove the calculation
	 * 
	 * @param cid
	 *            id of the calculation
	 */
	@ShellMethod(value = "Remove a calculation")
	public void removeCalculation(int cid) {
		// Request
		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/" + id + "/calculations/" + cid, HttpMethod.DELETE, null,
					ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			return;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return;
		}

		log.info("Calculation '" + cid + "' deleted");

	}

	/**
	 * Create the image of the best solution of the calculation
	 * 
	 * @param cid
	 *            id of the calculation
	 * @param dst
	 *            path of the generated image
	 * @param force
	 *            if set, overwrite if dst already exists
	 */
	@ShellMethod(value = "Get the result of a calculation")
	public void resultCalculation(int cid, String dst, @ShellOption(arity = 0, defaultValue = "false") boolean force) {
		// Parameter validation
		Path path = Paths.get(dst);
		if (Files.exists(path)/* && Files.isRegularFile(path) && Files.isWritable(path) */) {
			if (!force) {
				log.error("A file already exists, use --force to overwrite.");
				return;
			}
		}

		// Request
		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/" + id + "/calculations/" + cid, HttpMethod.GET, null,
					ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			return;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return;
		}

		// Writing the image

		String base64img = response.getBody().getBase64img();
		FileService.decodeAndSaveImage(base64img, dst);

		log.info("The image of the result is downloaded at " + dst);
	}

	/**
	 * Launch the calculation of a snapshot
	 * 
	 * @param id
	 *            id of the calculation
	 * @param iter
	 *            id of the snapshot
	 * @param loops
	 *            number of loops to calculate
	 */
	@ShellMethod(value = "Launch a snapshot of a calculation")
	public void launchSnapshot(int cid, int sid, int loops) {
		// Request
		HttpEntity<Integer> request = new HttpEntity<>(loops);
		ResponseEntity<ApiJsonResponse> response = null;
		try {
			response = restTemplate.exchange("/" + id + "/calculations/" + cid + "/snapshots/" + sid, HttpMethod.POST,
					request, ApiJsonResponse.class);
		} catch (RestClientException e) {
			log.error(e.getMessage());
			return;
		}

		// Check status code
		int code = response.getStatusCodeValue();
		if (!isHttpCodeValid(code, response)) {
			return;
		}

		log.info("Snapshot '" + sid + "' launched");

	}

	/**
	 * Remove a snapshot
	 * 
	 * @param id
	 * @param iter
	 */
	@ShellMethod(value = "Remove a calculation")
	public void removeSnapshot(int id, int iter) {
		// HTTPRequest
		log.info("snapshot removed");
	}

	/* UTILS */

	/**
	 * @param response
	 * @param code
	 */
	public boolean isHttpCodeValid(int code, ResponseEntity<ApiJsonResponse> response) {
		switch(code) {
			case 200:
				return true;
			default:
				System.out.println("|" + response.getBody().toString() + "|");
				log.error("%d : %s", code, response.getBody().getError());
				return false;

		}
	}

	public static CellMatcher at(final int theRow, final int col) {
		return new CellMatcher() {
			@Override
			public boolean matches(int row, int column, TableModel model) {
				return row == theRow && column == col;
			}
		};
	}
}
