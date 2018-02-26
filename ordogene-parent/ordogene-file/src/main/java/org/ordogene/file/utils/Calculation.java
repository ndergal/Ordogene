package org.ordogene.file.utils;

import java.util.Objects;

import org.ordogene.file.parser.Validable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Calculation implements Validable {
	/**
	 * 
	 */
	private int id;
	private String name;
	private boolean running;
	private int iterationNumber;
	private int maxIteration;
	private long fitnessSaved;
	private long lastIterationSaved;
	private long startTimestamp;

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTime) {
		if(startTime < 0) {
			throw new IllegalArgumentException("The number of iteration cannot be negative");
		}
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
		if(iterationNumber < 0) {
			throw new IllegalArgumentException("The number of iteration cannot be negative");
		}
		this.iterationNumber = iterationNumber;
	}

	public int getMaxIteration() {
		return maxIteration;
	}

	public void setMaxIteration(int maxIteration) {
		if(maxIteration < 0) {
			throw new IllegalArgumentException("The maximum of iteration cannot be negative");
		}
		this.maxIteration = maxIteration;
	}

	public long getLastIterationSaved() {
		return lastIterationSaved;
	}

	public void setLastIterationSaved(long lastIterationSaved) {
		if(lastIterationSaved < 0) {
			throw new IllegalArgumentException("The last iteration saved cannot be negative");
		}
		this.lastIterationSaved = lastIterationSaved;
	}

	public long getFitnessSaved() {
		return fitnessSaved;
	}

	public void setFitnessSaved(long fitnessSaved) {
		this.fitnessSaved = fitnessSaved;
	}

	@Override
	public String toString() {
		return "Calculation [id=" + id + ", name=" + name + ", running=" + running + ", iterationNumber="
				+ iterationNumber + ", maxIteration=" + maxIteration + ", fitnessSaved=" + fitnessSaved
				+ ", lastIterationSaved=" + lastIterationSaved + ", startTimestamp=" + startTimestamp + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + id;
		result = prime * result + iterationNumber;
		result = prime * result + (int) (lastIterationSaved ^ (lastIterationSaved >>> 32));
		result = prime * result + maxIteration;
		result = prime * result + name.hashCode();
		result = prime * result + (running ? 1231 : 1237);
		result = prime * result + (int) (fitnessSaved ^ (fitnessSaved >>> 32));
		result = prime * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Calculation))
			return false;
		Calculation c = (Calculation) obj;
		return startTimestamp == c.startTimestamp && running == c.running && fitnessSaved == c.fitnessSaved
				&& id == c.id && iterationNumber == c.iterationNumber && lastIterationSaved == c.lastIterationSaved
				&& maxIteration == c.maxIteration && name.equals(c.name);
	}

	@JsonIgnore
	@Override
	public boolean isValid() {
		return name != null;
	}

	@JsonIgnore
	public void setCalculation(long startTimestamp, int iterationNumber, long lstIterationSaved, int maxIteration,
			int id, String name, long fitness) {
		setStartTimestamp(startTimestamp);
		setIterationNumber(iterationNumber);
		setLastIterationSaved(lstIterationSaved);
		setMaxIteration(maxIteration);
		setId(id);
		setName(name);
		setFitnessSaved(fitness);
	}
}
