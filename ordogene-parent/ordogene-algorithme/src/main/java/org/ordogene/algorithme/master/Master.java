package org.ordogene.algorithme.master;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.Model;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.Calculation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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

	public int compute(String idUser, String jsonString) throws JsonParseException, JsonMappingException,
			InstantiationException, IllegalAccessException, UnmarshalException, IOException, InterruptedException {

		synchronized (threadMap) {
			while (currentThread == maxThread) {
				// timeout -2 complet ~5sec -1 error random
				threadMap.wait();
			}
			currentThread++;
		}

		JSONModel jmodel = (JSONModel) Parser.parseJsonFile(jsonString, JSONModel.class);
		Model model = Model.createModel(jmodel);
		int numCalc = jsonString.hashCode();

		ThreadHandler th = new ThreadHandler();
		Thread t = new Thread(() -> {
			int i = 5;
			while (i > 0) {
				System.out.println("hello world !(" + Thread.currentThread().getName() +")");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					String str = th.threadFromMaster();
					if (str != null) {
						Random rand = RandomRegistry.getRandom();
						int maxIteration = rand.nextInt(1000);
						int iterationNumber = rand.nextInt(maxIteration);
						int lastIterationSaved = rand.nextInt(iterationNumber);
						StringBuilder sb = new StringBuilder();
						sb.append(new Date().getTime()).append("_");
						sb.append(iterationNumber).append("_");
						sb.append(lastIterationSaved).append("_");
						sb.append(maxIteration).append("_");
						sb.append(rand.nextInt(Integer.MAX_VALUE));
						th.threadToMaster(sb.toString());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i--;
			}

			// TODO donner blockingqueue a la méthode sunchronized pour get th
			synchronized (threadMap) {
				currentThread--;
				threadMap.notifyAll();
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
	
	//TODO connection with Thread
	public void updateCalculation(Calculation cal) {
		ThreadHandler th = threadMap.get(cal.getId());
		if(th != null) {
			//IsRunning
			try {
				th.masterToThread("state");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				//Format: epoch_iterationNumber_lastIterationSaved_maxIteration_fitness
				String[] state = th.masterFromThread().split("_");
				try {
					Date date = new Date(Long.valueOf(state[0]));
					cal.setDate(formater.format(date));
					cal.setIterationNumber(Integer.valueOf(state[1]));
					cal.setLastIterationSaved(Integer.valueOf(state[2]));
					cal.setMaxIteration(Integer.valueOf(state[3]));
					cal.setFitnessSaved(Integer.valueOf(state[4]));
				} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
					throw new InternalError("Problem with calculation format informations");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			cal.setRunning(false);
		}
	}
}
