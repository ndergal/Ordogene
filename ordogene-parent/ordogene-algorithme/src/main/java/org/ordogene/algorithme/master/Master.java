package org.ordogene.algorithme.master;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.Model;
import org.ordogene.file.FileService;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jenetics.util.RandomRegistry;

public class Master {
	private static final int DEFAULT_THREAD = 10;
	private final int maxThread;
	private int currentThread;
	private final Map<Integer, ThreadHandler> threadMap = new HashMap<>();

	public Master() {
		maxThread = DEFAULT_THREAD;
	}

	public Master(int maxThread) {
		this.maxThread = maxThread;
	}

	public int compute(String idUser, String jsonString) throws JsonParseException, JsonMappingException, UnmarshalException, IOException {

		synchronized (threadMap) {
			if (currentThread == maxThread) {
				// TODO to fix we can send -2
				// timeout -2 complet ~5sec -1 error random
				return -2;
			}
			currentThread++;
		}

		JSONModel jmodel = (JSONModel) Parser.parseJsonFile(jsonString, JSONModel.class);
		Model model = Model.createModel(jmodel);


		String toHash = jsonString + (new Date()).toString();
		int numCalc = toHash.hashCode();
		
		ThreadHandler th = new ThreadHandler();

		Thread t = new Thread(() -> {

		});
		

		synchronized (threadMap) {
			th.setThread(t);
			t.start();
			threadMap.put(numCalc, th);
			return numCalc;
		}

		
	}

	public String getInfoByNumCalc(int numCalc) throws InterruptedException {
		ThreadHandler th = threadMap.get(numCalc);
		th.masterToThread("something");
		return th.masterFromThread();
	}
	
	// TODO connection with Thread
	public void updateCalculation(Calculation cal, String userId) {
		ThreadHandler th = threadMap.get(cal.getId());
		if (th != null) {
			// IsRunning
			try {
				th.masterToThread("state");

				//Format: epoch_iterationNumber_lastIterationSaved_maxIteration_fitness
				String[] state = th.masterFromThread().split("_");
				try {
					cal.setStartTimestamp(Long.valueOf(state[0]));
					cal.setIterationNumber(Integer.valueOf(state[1]));
					cal.setLastIterationSaved(Integer.valueOf(state[2]));
					cal.setMaxIteration(Integer.valueOf(state[3]));
					cal.setFitnessSaved(Integer.valueOf(state[4]));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					throw new InternalError("Problem with calculation format informations");
				}
				cal.setRunning(true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cal.setRunning(true);
		} else {
			try {
				String pathName = Const.getConst().get("ApplicationPath") + File.separator + userId
						+ File.separator + cal.getId() + "_" + cal.getName() + File.separatorChar + "state.json";
				Calculation tmpCal = (Calculation) Parser.parseJsonFile(Paths.get(pathName), Calculation.class);
				cal.setStartTimestamp(tmpCal.getStartTimestamp());
				cal.setIterationNumber(tmpCal.getIterationNumber());
				cal.setLastIterationSaved(tmpCal.getLastIterationSaved());
				cal.setMaxIteration(tmpCal.getMaxIteration());
				cal.setFitnessSaved(tmpCal.getFitnessSaved());
			} catch (IllegalAccessException | UnmarshalException | IOException e) {
				System.err.println("Problem to read the calculation ");
			}
			cal.setRunning(false);
		}
	}

	// TODO connection with Thread
	public boolean interruptCalculation(Calculation cal) {
		ThreadHandler th = threadMap.get(cal.getId());
		if (th != null) {
			// IsRunning
			try {
				th.masterToThread("interrupt");
				return (true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (false);
			}
		} else {
			return (false);
		}
	}
}
