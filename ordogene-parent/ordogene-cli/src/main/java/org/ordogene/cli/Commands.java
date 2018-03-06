package org.ordogene.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.ordogene.file.FileUtils;
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
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ShellComponent
public class Commands {
	private static final String CALCULATIONS = "/calculations/";
	private static final String PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER = "Problem with the communication between client and server";
	private final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
	private String id;
	private static final Logger log = LoggerFactory.getLogger(Commands.class);

	private final String[] tableFields = { "Calculation Id", "Name", "Date", "Running", "Fitness", "Iteration done",
			"Last iteration saved", "Max iteration" };

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void login() {
		loop: for (;;) {
			log.info("\nDo you want to create a new group id ? [y/N] : ");
			@SuppressWarnings("resource") // problem if the scanner is closed
			Scanner scanner = new Scanner(System.in);
			String choice = scanner.nextLine();
			switch (choice) {
			case "":
			case "n":
			case "N":
			case "no":
				do {
					log.info("Enter your group id : ");
					id = scanner.nextLine();
				} while (id.isEmpty() ? true : !getUser(id));
				break loop;
			case "y":
			case "Y":
			case "yes":
				if (!createUser()) {
					log.error("Problem with the server: user creation failed");
					System.exit(1);
				}
				break loop;
			default:
			}
		}
		log.info("\n");
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
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.getStatusCode() + " -- " + e.getStatusText());
			return null;
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			log.error(PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER);
			return null;
		}

		// Build ascii table
		List<Calculation> list = response.getBody().getList();

		if (!(list != null && !list.isEmpty())) {

			log.info("No calculations yet");
			return null;
		}
		TableBuilder builder = fillTable(list);
		return builder.addFullBorder(BorderStyle.oldschool).build();
	}

	/**
	 * Launch a calculation based on the model
	 * 
	 * @param model
	 *            path to model to send
	 */
	@ShellMethod(value = "Launch a calculation from a model")
	public String launchCalculation(@ShellOption({ "-m", "--model" }) File model) {
		// Parameter validation
		String jsonContentRead;
		try {
			jsonContentRead = FileUtils.readFile(model);
		} catch (IOException | IllegalArgumentException e) {
			return e.getMessage();
		}

		// Request
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(jsonContentRead, headers);

		try {
			ResponseEntity<ApiJsonResponse> response = restTemplate.exchange("/" + id + CALCULATIONS, HttpMethod.PUT,
					request, ApiJsonResponse.class);
			int cid = response.getBody().getCid();
			return "Calculation '" + cid + "' launched";
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return e.getStatusCode() + " -- " + e.getStatusText();
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			return PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER;
		}

	}

	/**
	 * Stop the calculation
	 * 
	 * @param cid
	 *            id of the calculation
	 */
	@ShellMethod(value = "Stop a calculation")
	public String stopCalculation(@ShellOption({ "-cid", "--calculationid" }) int cid) {
		// Request
		try {
			restTemplate.exchange("/" + id + CALCULATIONS + cid, HttpMethod.POST, null, ApiJsonResponse.class);
			return "Calculation '" + cid + "' stopped";
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return e.getStatusCode() + " -- " + e.getStatusText();
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			return PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER;
		}
	}

	/**
	 * Remove the calculation
	 * 
	 * @param cid
	 *            id of the calculation
	 */
	@ShellMethod(value = "Remove a calculation")
	public String removeCalculation(@ShellOption({ "-cid", "--calculationid" }) int cid) {
		// Request
		try {
			restTemplate.exchange("/" + id + CALCULATIONS + cid, HttpMethod.DELETE, null, ApiJsonResponse.class);
			return "Calculation '" + cid + "' has been deleted.";
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return e.getStatusCode() + " -- " + e.getStatusText();
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			return PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER;
		}
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
	 * @param html
	 *            if set, save the result in html (else, save in png)
	 */
	@ShellMethod(value = "Get the result of a calculation")
	public String resultCalculation(@ShellOption({ "-cid", "--calculationid" }) int cid,
			@ShellOption({ "-d", "--destination" }) File dst,
			@ShellOption(arity = 0, defaultValue = "false") boolean force,
			@ShellOption(arity = 0, defaultValue = "false") boolean html) {

		// Parameter validation
		Path path = dst.toPath();
		if (dst.isDirectory() && html) {
			path = Paths.get(dst.toPath().toString() + File.separator + this.id + "_" + cid + ".html");
		} else if (dst.isDirectory() && !html) {
			path = Paths.get(dst.toPath().toString() + File.separator + this.id + "_" + cid + ".png");
		}
		if (path.toFile().exists() && !force) {
			return "A file already exists, use --force to overwrite.";
		}

		// Request
		ResponseEntity<ApiJsonResponse> response = null;
		try {

			if (html) {
				response = restTemplate.exchange("/" + id + CALCULATIONS + cid + "/html", HttpMethod.GET, null,
						ApiJsonResponse.class);
				// Writing the html
				String base64 = response.getBody().getBase64img();
				FileUtils.saveHtmlFromBase64(base64, path.toAbsolutePath().toString());
				return "The html of the result is downloaded at " + dst;

			} else {
				response = restTemplate.exchange("/" + id + CALCULATIONS + cid, HttpMethod.GET, null,
						ApiJsonResponse.class);
				// Writing the image
				String base64 = response.getBody().getBase64img();
				FileUtils.saveImageFromBase64(base64, path.toAbsolutePath().toString());
				return "The image of the result is downloaded at " + dst;
			}

		} catch (IOException e) {
			// IOException or NoSuchFileException
			return e.getMessage();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			return e.getStatusCode() + " -- " + e.getStatusText();
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			return PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER;
		}
	}

	/**
	 * Open a file with the default launcher for it"
	 * 
	 * @param file
	 *            file to open
	 */
	@ShellMethod(value = "Open a file", key = { "xdg-open", "start", "open" })
	public boolean xdgOpen(@ShellOption({ "-i", "--file" }) File file) {
		Runtime currentRuntime = Runtime.getRuntime();
		String absolutePath = file.getAbsolutePath();
		String cmd = absolutePath;
		if (isLinux()) {
			cmd = (String.format("xdg-open %s", absolutePath));
		} else if (isMac()) {
			cmd = (String.format("open %s", absolutePath));
		} else if (isWindows() && isWindows9X()) {
			cmd = (String.format("command.com /C start %s", absolutePath));
		} else if (isWindows()) {
			cmd = (String.format("cmd /c start %s", absolutePath));
		}
		try {
			currentRuntime.exec(cmd);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/* UTILS */

	private boolean getUser(String id) {
		// Request
		try {
			restTemplate.exchange("/" + id, HttpMethod.GET, null, ApiJsonResponse.class);
			this.id = id;
			log.info("Welcome back {}", id);
			return true;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.getStatusCode() + " -- " + e.getStatusText());
			return false;
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			log.info(PROBLEM_WITH_THE_COMMUNICATION_BETWEEN_CLIENT_AND_SERVER);
			return false;
		}
	}

	private boolean createUser() {
		// Request
		try {
			ResponseEntity<ApiJsonResponse> response = restTemplate.exchange("/", HttpMethod.PUT, null,
					ApiJsonResponse.class);
			id = response.getBody().getUserId();
			log.info("Your new group id is {}", id);
			return true;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.getStatusCode() + " -- " + e.getStatusText());
			return false;
		} catch (RestClientException e) {
			log.debug(e.getMessage());
			return false;
		}
	}

	private TableBuilder fillTable(List<Calculation> list) {
		String[][] data = new String[list.size() + 1][tableFields.length];
		TableModel model = new ArrayTableModel(data);
		TableBuilder builder = new TableBuilder(model);
		for (int i = 0; i < tableFields.length; i++) {
			data[0][i] = tableFields[i];
		}
		for (int i = 0; i < list.size(); i++) {
			Calculation c = list.get(i);
			log.debug("{}", c);
			data[i + 1][0] = String.valueOf(c.getId());
			data[i + 1][1] = c.getName();
			data[i + 1][2] = formater.format(new Date(c.getStartTimestamp()));
			data[i + 1][3] = String.valueOf(c.isRunning());
			data[i + 1][4] = String.valueOf(c.getFitnessSaved());
			data[i + 1][5] = String.valueOf(c.getIterationNumber());
			data[i + 1][6] = String.valueOf(c.getLastIterationSaved());
			data[i + 1][7] = String.valueOf(c.getMaxIteration());
		}
		return builder;
	}

	private static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("windows") != -1 || os.indexOf("nt") != -1;
	}

	private static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("mac") != -1;
	}

	private static boolean isLinux() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("linux") != -1;
	}

	private static boolean isWindows9X() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.equals("windows 95") || os.equals("windows 98");
	}
}
