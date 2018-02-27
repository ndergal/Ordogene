package org.ordogene.algorithme.master;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.UnmarshalException;

import org.apache.maven.doxia.logging.Log;
import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.jenetics.CalculationHandler;
import org.ordogene.file.FileService;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Master {
	private static final int DEFAULT_THREAD = 10;
	private final int maxThread;
	private int currentThread;
	private final Map<Integer, ThreadHandler> threadMap = new HashMap<>();

	public Master() {
		maxThread = DEFAULT_THREAD;
	}

	public Master(int maxThread) {
		if (maxThread <= 0) {
			throw new IllegalArgumentException("The max Thread can not be zero or negative");
		}
		this.maxThread = maxThread;
	}

	public Integer compute(String idUser, String jsonString)
			throws JsonParseException, JsonMappingException, UnmarshalException, IOException {

		Objects.requireNonNull(idUser);
		Objects.requireNonNull(jsonString);

		synchronized (threadMap) {
			if (currentThread == maxThread) {
				return null;
			}
			currentThread++;
		}

		JSONModel jmodel = (JSONModel) Parser.parseJsonFile(jsonString, JSONModel.class);
		Model model = Model.createModel(jmodel);

		String toHash = jsonString + (new Date()).toString();
		int calculationId = toHash.hashCode();
		
		if(!FileService.createCalculationDirectory(idUser, calculationId, model.getName())) {
			System.err.println("Error while creating the calculation folder...");
		}

		ThreadHandler th = new ThreadHandler();

		CalculationHandler ch = new CalculationHandler(th, model, idUser, calculationId);

		Thread t = new Thread(() -> {

			ch.launchCalculation();

			synchronized (threadMap) {
				currentThread--;
				threadMap.remove(calculationId);
				return;
			}
		});

		synchronized (threadMap) {
			threadMap.put(calculationId, th);
			t.start();
			return calculationId;
		}

	}

	public void updateCalculation(Calculation cal, String userId) {
		ThreadHandler th = null;
		synchronized (threadMap) {
			th = threadMap.get(cal.getId());
		}
		if (th != null) {
			// IsRunning
			try {
				th.clearMasterFromThread();
				th.masterToThread("state");

				// Format: epoch_iterationNumber_lastIterationSaved_maxIteration_fitness
				String response = th.masterFromThread();
				if(response == null) {
					System.err.println("The calculation does not respond at the request");
					return;
				}
				String[] state = response.split("_");
				try {
					cal.setCalculation(Long.valueOf(state[0]), Integer.valueOf(state[1]), Integer.valueOf(state[2]),
							Integer.valueOf(state[3]), cal.getId(), cal.getName(), Integer.valueOf(state[4]));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					throw new InternalError("Problem with calculation format informations");
				}
				cal.setRunning(true);
			} catch (InterruptedException e) {
				// Master will be closed
				Thread.currentThread().interrupt();
			}
		} else {
			try {
				String pathName = Const.getConst().get("ApplicationPath") + File.separator + userId + File.separator
						+ cal.getId() + "_" + cal.getName() + File.separatorChar + "state.json";
				Calculation tmpCal = (Calculation) Parser.parseJsonFile(Paths.get(pathName), Calculation.class);
				cal.setStartTimestamp(tmpCal.getStartTimestamp());
				cal.setIterationNumber(tmpCal.getIterationNumber());
				cal.setLastIterationSaved(tmpCal.getLastIterationSaved());
				cal.setMaxIteration(tmpCal.getMaxIteration());
				cal.setFitnessSaved(tmpCal.getFitnessSaved());
				cal.setRunning(false);
			} catch (IllegalAccessException | UnmarshalException | IOException e) {
				System.err.println("Problem to read the calculation ");
			}
		}
	}

	// TODO connection with Thread
	public boolean interruptCalculation(int calculationId) {
		ThreadHandler th = threadMap.get(calculationId);
		if (th != null) {
			// IsRunning
			try {
				th.masterToThread("interrupt");
				return true;
			} catch (InterruptedException e) {
				// Master will be closed
				Thread.currentThread().interrupt();
				return false;
			}
		} else {
			return false;
		}
	}
}
