package org.ordogene.algorithme.master;


import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.ordogene.file.utils.Calculation;

public class ThreadHandler {
		private final BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
		private final Calculation c = new Calculation();  

		public void masterToThread(String str) throws InterruptedException {
			queue1.put(Objects.requireNonNull(str));
		}

		public String threadFromMaster() throws InterruptedException {
			return queue1.poll();
		}
		
		public Calculation getCalculation() {
			return c;
		}

	}
