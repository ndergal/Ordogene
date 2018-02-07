package org.ordogene.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestTemplate;

@ShellComponent
public class Commands {
	private final String[] headers = {"id", "name", "status", "progress", "max fitness"};
	private final String addr = "https://163.172.90.226/ordogene"; 
	
	/**
	 * Display a table containing calculations of the user
	 */
	@ShellMethod(value = "List calculations")
	public Table listCalculations() {
		//Request
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<Calculation>> response = restTemplate.exchange(
				addr+"/:id/calculations",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Calculation>>() {}
			);
		
		//Check status code
		int code = response.getStatusCodeValue();
		switch(code) {
			case 200:
				//if ??
				break;
			case 204:
				System.out.println("No calculations. Send one already !");
				return null;
			case 400:
				System.out.println("User does not exist");
				return null;
			default:
				System.out.println("Unimplemented http status code");
				return null;
		}
		
		//Build ascii table
		List<Calculation> list = response.getBody();
		String[][] data = new String[list.size() + 1][5];
		TableModel model = new ArrayTableModel(data);
		TableBuilder builder = new TableBuilder(model);
		for(int i = 0; i < 5; i++) {
			data[0][i] = headers[i];
			//builder.on(at(0, i)).
		}
		for(int i = 0; i < list.size(); i++) {
			Calculation c = list.get(i);
			data[i+1][0] = String.valueOf(c.getId());
			data[i+1][1] = c.getName();
			data[i+1][2] = String.valueOf(c.getIterationNumber());
			data[i+1][3] = String.valueOf(c.getMaxIteration());
			data[i+1][4] = String.valueOf(c.getLastIterationSaved());
		}
		return builder.addFullBorder(BorderStyle.oldschool).build();
	}
	
	/**
	 * Launch a calculation based on the model
	 * @param model path to model to send
	 */
	@ShellMethod(value = "Launch a calculation from a model")
	public void launchCalculation(String model) {
		Path path = Paths.get(model);
		if(Files.notExists(path)) {
			System.out.println("The path does not exist. Try again.");
			return;
		}

		System.out.println("calculation launched");
	}
	
	
	/**
	 * Stop the calculation
	 * @param id id of the calculation
	 */
	@ShellMethod(value = "Stop a calculation")
	public void stopCalculation(int id) {
		//HTTPRequest
		System.out.println("calculation stopped");
	}
	
	/**
	 * Remove the calculation
	 * @param id id of the calculation
	 */
	@ShellMethod(value = "Remove a calculation")
	public void removeCalculation(int id) {
		//HTTPRequest
		System.out.println("calculation removed");
	}
	
	/**
	 * Create the image of the best solution of the calculation
	 * @param id id of the calculation
	 * @param dst path of the generated image
	 * @param force if set, overwrite if dst already exists
	 */
	@ShellMethod(value = "Get the result of a calculation")
	public void resultCalculation(int id, String dst, @ShellOption(arity=0, defaultValue="false") boolean force) {
		//HTTPRequest
		Path path = Paths.get(dst);
		if(Files.exists(path)/* && Files.isRegularFile(path) && Files.isWritable(path)*/) {
			if(force) {
				System.out.println("calculation resulted");
			} else {
				System.out.println("A file already exists, use --force to overwrite.");
			}
		} else {
			
		}
	}
	
	/**
	 * Launch the calculation of a snapshot
	 * @param id id of the calculation
	 * @param iter id of the snapshot
	 * @param loops number of loops to calculate
	 */
	@ShellMethod(value = "Launch a snapshot of a calculation")
	public void launchSnapshot(int id, int iter, int loops) {
		//HTTPRequest
		System.out.println("snapshot launched");
	}
	
	/**
	 * Remove a snapshot
	 * @param id
	 * @param iter
	 */
	@ShellMethod(value = "Remove a calculation")
	public void removeSnapshot(int id, int iter) {
		//HTTPRequest
		System.out.println("snapshot removed");
	}
	
	/* UTILS */
	
	public static CellMatcher at(final int theRow, final int col) {
		return new CellMatcher() {
			@Override
			public boolean matches(int row, int column, TableModel model) {
				return row == theRow && column == col;
			}
		};
	}
}
