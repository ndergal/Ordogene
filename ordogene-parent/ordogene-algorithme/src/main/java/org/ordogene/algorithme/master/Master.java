package org.ordogene.algorithme.master;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.jenetics.CalculationHandler;
import org.ordogene.file.FileUtils;
import org.ordogene.file.models.JSONModel;
import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Handle the algorithm threads
 * 
 * @author darwinners team
 *
 */
public class Master {
	private static final Logger log = LoggerFactory.getLogger(Master.class);
	private static final int DEFAULT_THREAD = 10;
	private final int maxThread;
	private int currentThread;
	private final Map<Integer, ThreadHandler> threadMap = new HashMap<>();

	public Master() {
		int tmpMaxThread;
		String maxThreadFromMap = Const.getConst().get("MaxComputationThreads");
		if (maxThreadFromMap == null) {
			tmpMaxThread = DEFAULT_THREAD;
			log.info("MaxComputationThreads option is not present in the configuration file. Use default value : "+DEFAULT_THREAD);
		}
		try {
			tmpMaxThread = Integer.parseInt(maxThreadFromMap);
		} catch(NumberFormatException e) {
			tmpMaxThread = DEFAULT_THREAD;
			log.info("MaxComputationThreads option in the configuration file is not a valid number. Use default value : "+DEFAULT_THREAD);
		}
		maxThread = tmpMaxThread;
	}

	public Master(int maxThread) {
		if (maxThread <= 0) {
			throw new IllegalArgumentException("The max Thread can not be zero or negative");
		}
		this.maxThread = maxThread;
	}

	public Integer compute(String username, String jsonString)
			throws JsonParseException, JsonMappingException, UnmarshalException, IOException {

		Objects.requireNonNull(username);
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

		ThreadHandler th = new ThreadHandler();

		CalculationHandler ch = new CalculationHandler(th, model, username, calculationId);

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

	public void updateCalculation(Calculation cal, String username) {
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
				if (response == null) {
					log.error("The calculation does not respond at the request");
					return;
				}
				String[] state = response.split("_");
				updateCalculWithState(cal, state);
			} catch (InterruptedException e) {
				// Master will be closed
				Thread.currentThread().interrupt();
			}
		} else {
			try {
				String pathName = FileUtils.getCalculationStatePath(username, cal.getId(), cal.getName());
				Calculation tmpCal = (Calculation) Parser.parseJsonFile(Paths.get(pathName), Calculation.class);
				cal.setCalculation(tmpCal.getStartTimestamp(), tmpCal.getIterationNumber(),
						tmpCal.getLastIterationSaved(), tmpCal.getMaxIteration(), cal.getId(), cal.getName(),
						tmpCal.getFitnessSaved());
				cal.setRunning(false);
			} catch (IllegalAccessException | UnmarshalException | IOException e) {
				log.error("Problem to read the calculation ");
			}
		}
	}

	private void updateCalculWithState(Calculation cal, String[] state) throws InternalError {
		try {
			cal.setCalculation(Long.valueOf(state[0]), Integer.valueOf(state[1]), Integer.valueOf(state[2]),
					Integer.valueOf(state[3]), cal.getId(), cal.getName(), Integer.valueOf(state[4]));
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			throw new InternalError("Problem with calculation format informations");
		}
		cal.setRunning(true);
	}

	public boolean interruptCalculation(int cid) {
		ThreadHandler th = threadMap.get(cid);
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
