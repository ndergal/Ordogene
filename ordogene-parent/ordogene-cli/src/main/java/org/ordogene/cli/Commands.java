package org.ordogene.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

@ShellComponent
public class Commands {
	public final String[] headers = {"id", "name", "status", "rate", "max fitness"};
	
	@ShellMethod(value = "List calculations")
	private Table listCalculations() {
		String[][] data = new String[5][5]; 
		TableModel model = new ArrayTableModel(data);
		TableBuilder builder = new TableBuilder(model);
		
		for(int i = 0; i < 5; i++) {
			data[0][i] = headers[i];
		}
		
		//System.out.println("calculations listed");
		return builder.addFullBorder(BorderStyle.fancy_light).build();
	}
	
	@ShellMethod(value = "Launch a calculation from a model")
	private void launchCalculation(String model) {
		System.out.println("calculation launched");
	}
	
	@ShellMethod(value = "Stop a calculation")
	private void stopCalculation(int id) {
		System.out.println("calculation stopped");
	}
	
	@ShellMethod(value = "Remove a calculation")
	private void removeCalculation() {
		System.out.println("calculation removed");
	}
	
	@ShellMethod(value = "Get the result of a calculation")
	private void resultCalculation() {
		System.out.println("calculation resulted");
	}
	
	@ShellMethod(value = "Launch a snapshot of a calculation")
	private void launchSnapshot() {
		System.out.println("snapshot launched");
	}
	
	@ShellMethod(value = "Remove a calculation")
	private void removeSnapshot() {
		System.out.println("snapshot removed");
	}
	
}
