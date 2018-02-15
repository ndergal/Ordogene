package org.ordogene.algorithme.master;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
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
	private final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy-hh:mm");

	static class ThreadHandler {
		private Thread thread;
		private final BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
		private final BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(1);

		public void masterToThread(String str) throws InterruptedException {
			queue1.put(str);
		}

		public String threadFromMaster() throws InterruptedException {
			return queue1.poll();
		}

		public String masterFromThread() throws InterruptedException {
			return queue2.take();
		}

		public void threadToMaster(String str) throws InterruptedException {
			queue2.put(str);
		}

		public void setThread(Thread thread) {
			this.thread = thread;
		}

	}

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

			Date currentDate = new Date();
			int occur = 0;
			int maxIter = model.getExecTime();
			boolean interupted = false;
			while (occur < maxIter && !interupted) {
				System.out.println("hello world !(" + Thread.currentThread().getName() + ")");

				// TODO call real algorithm functions
				try {
					Dummy.fakeCalculation(th, model.getName(), idUser, numCalc, occur);
				} catch (InterruptedException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				occur++;

				try {
					String str = th.threadFromMaster();
					if (str != null && str.equals("state")) {
						Random rand = RandomRegistry.getRandom();
						int lastIterationSaved = rand.nextInt(occur); //TODO change RANDOM
						StringBuilder sb = new StringBuilder();
						sb.append(currentDate.getTime()).append("_");
						sb.append(occur).append("_");
						sb.append(lastIterationSaved).append("_");
						sb.append(maxIter).append("_");
						sb.append(rand.nextInt(Integer.MAX_VALUE));
						th.threadToMaster(sb.toString());
					} else if (str != null && str.equals("interrupt")) {
						interupted = true;

					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
 			Random rand = RandomRegistry.getRandom();  //TODO change RANDOM
			int lastIterationSaved = rand.nextInt(occur);
			Calculation tmpCalc = new Calculation();
			tmpCalc.setDate(currentDate);
			tmpCalc.setIterationNumber(occur);
			tmpCalc.setLastIterationSaved(lastIterationSaved);
			tmpCalc.setMaxIteration(maxIter);
			tmpCalc.setId(numCalc);
			tmpCalc.setName(model.getName());
			try {
				String calculationSaveDest = Const.getConst().get("ApplicationPath") + File.separator + idUser
						+ File.separator + tmpCalc.getId() + "_"+model.getName() + File.separatorChar + "state.json"; 
				FileService.writeInFile(tmpCalc, Paths.get(calculationSaveDest));
				System.out.println(tmpCalc + " saved in " + calculationSaveDest);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(tmpCalc + " not saved.");
			}
			// TODO donner blockingqueue a la méthode sunchronized pour get th
			synchronized (threadMap) {
				currentThread--;
				threadMap.remove(numCalc);
				return;
			}
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
	public void updateCalculation(Calculation cal) {
		ThreadHandler th = threadMap.get(cal.getId());
		if (th != null) {
			// IsRunning
			try {
				th.masterToThread("state");

				//Format: epoch_iterationNumber_lastIterationSaved_maxIteration_fitness
				String[] state = th.masterFromThread().split("_");
				try {
					Date date = new Date(Long.valueOf(state[0]));
					cal.setDate(formater.format(date));
					cal.setIterationNumber(Integer.valueOf(state[1]));
					cal.setLastIterationSaved(Integer.valueOf(state[2]));
					cal.setMaxIteration(Integer.valueOf(state[3]));
					cal.setFitnessSaved(Integer.valueOf(state[4]));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException | ParseException e) {
					throw new InternalError("Problem with calculation format informations");
				}
				cal.setRunning(true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cal.setRunning(true);
		} else {
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
