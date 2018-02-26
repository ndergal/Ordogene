package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.models.Action;

import io.jenetics.Gene;

public class ActionGene implements Gene<Action, ActionGene> {

	private final Action action;
	private final int startTime;

	public ActionGene(Action action, int startTime) {
		this.action = action;
		this.startTime = startTime;
	}

	// TODO find a solution
	@Override
	public boolean isValid() {
		return true;
		// return model.workable(action, currentEnvironment);
	}

	@Override
	public Action getAllele() {
		return action;
	}

	@Override
	public ActionGene newInstance() {
		return new ActionGene(action, startTime);
	}

	@Override
	public ActionGene newInstance(Action action) {
		return new ActionGene(action, startTime);
	}

	public static ActionGene of(Action action, int startTime) {
		return new ActionGene(action, startTime);
	}

	@Override
	public String toString() {
		return "ActionGene [action=" + action.getName() + "]";
	}

	public int getStartTime() {
		return startTime;
	}

}
