package org.ordogene.algorithme.master;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.Model;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Master {
	private static final int DEFAULT_THREAD = 10;
	private final int maxThread;
	private int currentThread;
	private final Map<Integer, ThreadHandler> threadMap = new HashMap<>();

	static class ThreadHandler {
		private Thread thread;
		private final BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
		private final BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(1);

		public void masterToThread(String str) throws InterruptedException {
			queue1.put(str);
		}

		public String masterFromThread() throws InterruptedException {
			return queue2.take();
		}

		public void threadToMaster(String str) throws InterruptedException {
			queue2.put(str);
		}

		public String threadFromMaster() throws InterruptedException {
			return queue1.poll();
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
			if (currentThread == maxThread) {
				// timeout -2 complet ~5sec -1 error random
				return -2;
			}
			currentThread++;
		}

		JSONModel jmodel = (JSONModel) Parser.parseJsonFile(jsonString, JSONModel.class);
		Model model = Model.createModel(jmodel);
		int numCalc = jsonString.hashCode();

		ThreadHandler th = new ThreadHandler();
		Thread t = new Thread(() -> {
			
			try {
				Dummy.fakeCalculation(th, idUser,numCalc);
			} catch (InterruptedException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*
			System.out.println("hello world !");
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
			}
			*/

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
}
