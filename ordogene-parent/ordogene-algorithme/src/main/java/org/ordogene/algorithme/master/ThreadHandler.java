package org.ordogene.algorithme.master;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadHandler {
		private Thread thread;
		private final BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
		private final BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(1);

		public void masterToThread(String str) throws InterruptedException {
			queue1.put(Objects.requireNonNull(str));
		}

		public String threadFromMaster() throws InterruptedException {
			return queue1.poll();
		}

		public String masterFromThread() throws InterruptedException {
			return queue2.take();
		}

		public void threadToMaster(String str) throws InterruptedException {
			queue2.put(Objects.requireNonNull(str));
		}

		public void setThread(Thread thread) {
			this.thread = Objects.requireNonNull(thread);
		}

	}