package org.ordogene.cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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
				addr + "/:id/calculations",
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
		//Parameter validation
		Path path = Paths.get(model);
		if(Files.notExists(path)) {
			System.out.println("The path does not exist. Try again.");
			return;
		}
		File file = new File(model);
		
		//Request
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<File> request = new HttpEntity<>(file);
		ResponseEntity<Integer> response = restTemplate.exchange(
				addr + "/:id/calculations/",
				HttpMethod.PUT,
				request,
				Integer.class);
		
		//Check status code
		int code = response.getStatusCodeValue();
		switch(code) {
			case 200:
				//if ??
				break;
			case 400:
				System.out.println("The model is corrupted");
				return;
			case 404:
				System.out.println("User does not exist");
				return;
			default:
				System.out.println("Unimplemented http status code");
				return;
		}
		
		int cid = response.getBody();
		System.out.println("Calculation #" + cid + "launched");
	}
	
	
	/**
	 * Stop the calculation
	 * @param id id of the calculation
	 */
	@ShellMethod(value = "Stop a calculation")
	public void stopCalculation(int cid) {
		//Request
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Void> response = restTemplate.exchange(
				addr + "/:id/calculations/" + cid,
				HttpMethod.POST,
				null,
				Void.class
			);
		
		//Check status code
		int code = response.getStatusCodeValue();
		switch(code) {
			case 200:
				//if ??
				break;
			case 400:
				System.out.println("The calculation is already stopped");
				return;
			case 401:
				System.out.println("You do not own this calculation");
				return;
			case 404:
				System.out.println("The calculation does not exist");
				return;
			default:
				System.out.println("Unimplemented http status code");
				return;
		}
		
		System.out.println("Calculation #" + cid + " stopped");
	}
	
	/**
	 * Remove the calculation
	 * @param id id of the calculation
	 */
	@ShellMethod(value = "Remove a calculation")
	public void removeCalculation(int cid) {
		//Request
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Void> response = restTemplate.exchange(
				addr + "/:id/calculations/" + cid,
				HttpMethod.DELETE,
				null,
				Void.class
			);
		
		//Check status code
		int code = response.getStatusCodeValue();
		switch(code) {
		case 200:
			//if ??
			break;
		case 400:
			System.out.println("The calculation does not exist");
			return;
		case 401:
			System.out.println("You do not own this calculation");
			return;
		default:
			System.out.println("Unimplemented http status code");
			return;
		}
		
		System.out.println("Calculation #" + cid + " deleted");
	}
	
	/**
	 * Create the image of the best solution of the calculation
	 * @param id id of the calculation
	 * @param dst path of the generated image
	 * @param force if set, overwrite if dst already exists
	 */
	@ShellMethod(value = "Get the result of a calculation")
	public void resultCalculation(int cid, String dst, @ShellOption(arity=0, defaultValue="false") boolean force) {
		//Parameter validation
		Path path = Paths.get(dst);
		if(Files.exists(path)/* && Files.isRegularFile(path) && Files.isWritable(path)*/) {
			if(!force) {
				System.out.println("A file already exists, use --force to overwrite.");
				return;
			}
		}
		
		//Request
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BufferedImage> response = restTemplate.exchange(
				addr + "/:id/calculations/" + cid,
				HttpMethod.GET,
				null,
				BufferedImage.class);
		
		//Check status code
		int code = response.getStatusCodeValue();
		switch(code) {
			case 200:
				//if ??
				break;
			case 401:
				System.out.println("You do not own this calculation");
				return;
			case 404:
				System.out.println("The calculation does not exist");
				return;
			default:
				System.out.println("Unimplemented http status code");
				return;
		}
		
		//Writing the image
		BufferedImage img = response.getBody();
		try {
			ImageIO.write(img, "PNG", new File(dst));
		} catch (IOException e) {
			System.out.println("A error has occured while writing the image");
		}
		
		System.out.println("The image of the result is downloaded at " + dst);
	}
	
	/**
	 * Launch the calculation of a snapshot
	 * @param id id of the calculation
	 * @param iter id of the snapshot
	 * @param loops number of loops to calculate
	 */
	@ShellMethod(value = "Launch a snapshot of a calculation")
	public void launchSnapshot(int cid, int sid, int loops) {
		//Request
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Integer> request = new HttpEntity<>(loops);
		ResponseEntity<Integer> response = restTemplate.exchange(
				addr + "/:id/calculations/" + cid + "/snapshots/" + sid ,
				HttpMethod.POST,
				request,
				Integer.class
			);
		
		//Check status code
		/*int code = response.getStatusCodeValue();
		switch(code) {
			case 200:
				//if ??
				break;
			case 400:
				System.out.println("The calculation is already stopped");
				return;
			case 401:
				System.out.println("You do not own this snapshot");
				return;
			case 404:
				System.out.println("The snapshot does not exist");
				return;
			default:
				System.out.println("Unimplemented http status code");
				return;
		}*/
		
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
