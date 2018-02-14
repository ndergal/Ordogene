package org.ordogene.file.utils;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Calculation implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2865737871685409060L;
	private static final DateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy-hh:mm");
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
		Date date = new Date(this.startTimestamp);
		return dateFormater.format(date);
	}

	public void setDate(String dateStr) throws ParseException {
		Date date = dateFormater.parse(dateStr);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		startTimestamp = cal.getTimeInMillis();
	}

	public void setDate(Date date) {
 		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		startTimestamp = cal.getTimeInMillis();
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
				+ " iterationNumber='" + iterationNumber + "'" + " maxIteration='" + maxIteration + "'" + " startDate='"
				+ dateFormater.format(new Date(startTimestamp)) + "'" + " lastIterationSaved='" + lastIterationSaved
				+ "'" + "}";
	}
}
