package org.ordogene.file.utils;

public class Calculation {
	private int id;
	private String name;
	private boolean running;
	private int iterationNumber;
	private int maxIteration;
	private String date;
	private int lastIterationSaved;
	
	public Calculation() {} 
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public int getIterationNumber() {
		return iterationNumber;
	}
	public void setIterationNumber(int iterationNumber) {
		this.iterationNumber = iterationNumber;
	}
	public int getMaxIteration() {
		return maxIteration;
	}
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getLastIterationSaved() {
		return lastIterationSaved;
	}
	public void setLastIterationSaved(int lastIterationSaved) {
		this.lastIterationSaved = lastIterationSaved;
	}
	
	@Override
	public String toString() {
		return "Calculation {" +
				" id='" + id + "'" +
				" name='" + name + "'" +
				" running='" + running + "'" +
				" iterationNumber='" + iterationNumber + "'" +
				" maxIteration='" + maxIteration + "'" +
				" date='" + date + "'" +
				" lastIterationSaved='" + lastIterationSaved + "'" +
				"}";
	}
}
