package org.ordogene.algorithme.master;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

import org.ordogene.algorithme.Model;
import org.ordogene.file.FileService;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import io.jenetics.util.RandomRegistry;

public class CalculationHandler {

	private final Date currentDate = new Date();
	private final Model model;
	private final String userId;
	private final int calculationId;
	private final ThreadHandler th;

	public CalculationHandler(ThreadHandler th, Model m, String userId, int calculationId) {
		this.th = th;
		this.model = Objects.requireNonNull(m);
		this.userId = userId;
		this.calculationId = calculationId;
	}

	public void launchCalculation() {
		int occur = 0;
		int maxIter = model.getExecTime();
		int fitness = RandomRegistry.getRandom().nextInt(Integer.MAX_VALUE);
		boolean interupted = false;
		while (occur < maxIter && !interupted) {
			System.out.println("hello world !(" + Thread.currentThread().getName() + ")");

			// TODO call real algorithm functions
			try {
				Dummy.fakeCalculation(model.getName(), userId, calculationId, occur);
			} catch (InterruptedException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			occur++;

			try {
				String str = th.threadFromMaster();
				if (str != null && str.equals("state")) {
					
					// TODO change 1 by real value
					String msg = constructStateString(occur, maxIter, 1,fitness);
					
					th.threadToMaster(msg.toString());
				} else if (str != null && str.equals("interrupt")) {
					interupted = true;

				}
			} catch (InterruptedException e) {
				interupted = true;
				Thread.currentThread().interrupt();
			}
		}
		// TODO change 1 by real value
		Calculation tmpCalc = new Calculation();
		tmpCalc.setCalculation(currentDate.getTime(), occur, 1, maxIter, calculationId,
				model.getName(), fitness);
		try {
			String calculationSaveDest = Const.getConst().get("ApplicationPath") + File.separator + userId
					+ File.separator + tmpCalc.getId() + "_" + model.getName() + File.separatorChar + "state.json";
			FileService.writeInFile(tmpCalc, Paths.get(calculationSaveDest));
			System.out.println(tmpCalc + " saved in " + calculationSaveDest);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(tmpCalc + " not saved.");
		}
	}

	private String constructStateString(int occur, int maxIter, int lastIterationSaved, int fitness) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentDate.getTime()).append("_");
		sb.append(occur).append("_");
		sb.append(lastIterationSaved).append("_");
		sb.append(maxIter).append("_");
		sb.append(fitness);
		return sb.toString();
	}

}
