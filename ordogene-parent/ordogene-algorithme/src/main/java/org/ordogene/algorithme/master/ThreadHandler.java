package org.ordogene.algorithme.master;


import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.ordogene.file.utils.Calculation;

public class ThreadHandler {
		private final BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
		private final BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(1);
		private final Calculation c = new Calculation();  

		public void masterToThread(String str) throws InterruptedException {
			queue1.put(Objects.requireNonNull(str));
		}

		public String threadFromMaster() throws InterruptedException {
			return queue1.poll();
		}

		public String masterFromThread() throws InterruptedException {
			return queue2.poll(10, TimeUnit.SECONDS);
		}

		public void threadToMaster(String str) throws InterruptedException {
			queue2.put(Objects.requireNonNull(str));
		}
		
		public void clearMasterFromThread() {
			queue2.clear();
		}
		
		public Calculation getCalculation() {
			return c;
		}

	}
