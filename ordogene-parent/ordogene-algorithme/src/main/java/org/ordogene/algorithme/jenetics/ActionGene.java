package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.models.Action;

import io.jenetics.Gene;

public class ActionGene implements Gene<Action, ActionGene> {

	private final Action action;
	
	public ActionGene(Action action) {
		super();
		this.action = action;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Action getAllele() {
		return action;
	}

	@Override
	public ActionGene newInstance() {
		return new ActionGene(Action.EMPTY(5));
	}

	@Override
	public ActionGene newInstance(Action action) {
		return new ActionGene(action);
	}

}
