package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.Gene;

public class ActionGene implements Gene<Action, ActionGene> {

	private final Action action;
	private Model model;
	
	public ActionGene(Action action, Model model) {
		this.action = action;
		//this.currentEnvironment = currentEnvironment.copy();
		this.model = model;
	}

	// TODO find a solution
	@Override
	public boolean isValid() {
		return true;
		//return model.workable(action, currentEnvironment);
	}

	@Override
	public Action getAllele() {
		return action;
	}

	@Override
	public ActionGene newInstance() {
		return new ActionGene(action, model);
	}

	@Override
	public ActionGene newInstance(Action action) {
		return new ActionGene(action, model);
	}
	
	public static ActionGene of(Environment currentEnvironment, int currentTime, Model model) {
		Action newAction = model.getWorkableAction(currentEnvironment, currentTime);
		return new ActionGene(newAction, model);
	}

}
