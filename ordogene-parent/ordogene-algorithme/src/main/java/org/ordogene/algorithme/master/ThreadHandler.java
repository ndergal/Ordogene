package org.ordogene.algorithme.master;

import java.util.concurrent.BlockingQueue;

public class ThreadHandler {
	public Thread thread;
	public BlockingQueue<String> queue1;
	public BlockingQueue<String> queue2;

	public ThreadHandler(BlockingQueue<String> queue1, BlockingQueue<String> queue2) {
		this.queue1 = queue1;
		this.queue2 = queue2;
	}
	
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