package org.ordogene.file.utils;

import java.util.Objects;

import org.ordogene.file.parser.Validable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Calculation implements Validable{
	/**
	 * 
	 */
	private int id;
	private String name;
	private boolean running;
	private int iterationNumber;
	private int maxIteration;
	private int fitnessSaved;
	private int lastIterationSaved;
	private long startTimestamp;

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTime) {
		this.startTimestamp = startTime;
	}

	public Calculation() {
	}

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
		this.name = Objects.requireNonNull(name);
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

	public int getLastIterationSaved() {
		return lastIterationSaved;
	}

	public void setLastIterationSaved(int lastIterationSaved) {
		this.lastIterationSaved = lastIterationSaved;
	}

	public int getFitnessSaved() {
		return fitnessSaved;
	}

	public void setFitnessSaved(int fitnessSaved) {
		this.fitnessSaved = fitnessSaved;
	}

	@Override
	public String toString() {
		return "Calculation {" + " id='" + id + "'" + " name='" + name + "'" + " running='" + running + "'"
				+ " iterationNumber='" + iterationNumber + "'" + " maxIteration='" + maxIteration + "'" + " startTimestramp='"
				+ startTimestamp + "'" + " lastIterationSaved='" + lastIterationSaved
				+ "'" + "}";
	}

	@JsonIgnore
	@Override
	public boolean isValid() {
		return name != null;
	}
	
	@JsonIgnore
	public void setCalculation(long startTimestamp, int iterationNumber, int lstIterationSaved, int maxIteration, int id, String name, int fitness) {
		setStartTimestamp(startTimestamp);
		setIterationNumber(iterationNumber);
		setLastIterationSaved(lstIterationSaved);
		setMaxIteration(maxIteration);
		setId(id);
		setName(name);
		setFitnessSaved(fitness);
	}
}
