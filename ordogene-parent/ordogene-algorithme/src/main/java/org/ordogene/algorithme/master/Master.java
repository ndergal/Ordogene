package org.ordogene.algorithme.master;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.jenetics.ScheduleBuilder;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Master {
	private static final int DEFAULT_THREAD = 10;
	private final int maxThread;
	private int currentThread;
	private final Map<Integer, ThreadHandler> threadMap = new HashMap<>();

	/*static class ThreadHandler {
		private ThreadHandler data = new ThreadHandler(new ArrayBlockingQueue<>(1), new ArrayBlockingQueue<>(1));

		public void masterToThread(String str) throws InterruptedException {
			data.queue1.put(str);
		}

		public String masterFromThread() throws InterruptedException {
			return data.queue2.take();
		}

		public void threadToMaster(String str) throws InterruptedException {
			data.queue2.put(str);
		}

		public String threadFromMaster() throws InterruptedException {
			return data.queue1.poll();
		}

		public void setThread(Thread thread) {
			this.data.thread = thread;
		}

	}*/

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

		ThreadHandler th = new ThreadHandler(new ArrayBlockingQueue<>(1), new ArrayBlockingQueue<>(1));
		Thread t = new Thread(() -> {
			/*System.out.println("hello world !");
			int i = 5;
			while (i > 0) {
				try {
					threadMap.wait(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					String str = th.threadFromMaster();
					if (str != null) {
						th.threadToMaster("hello thread");
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i--;
			}*/
			
			ScheduleBuilder runnable = new ScheduleBuilder(th, model);
			runnable.run();
			// TODO donner blockingqueue a la méthode synchronized pour get th
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
}
