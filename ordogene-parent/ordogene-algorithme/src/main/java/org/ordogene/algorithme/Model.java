package org.ordogene.algorithme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;

public class Model {
	private final int slots;
	private final int execTime;
	private final Environment environment;
	private final ArrayList<Action> actions;
	private final Fitness fitness;
	
	
	//TODO change to public temporally
	public Model(int slots, int execTime,Environment environment,ArrayList<Action> actions,Fitness fitness) {
		if(slots <= 0) {
			throw new IllegalArgumentException("slots has to be a positive integer");
		}
		if(execTime <= 0) {
			throw new IllegalArgumentException("execTime has to be a positive integer");
		}
		this.slots = slots;
		this.execTime = execTime;
		this.environment = Objects.requireNonNull(environment);
		this.actions = Objects.requireNonNull(actions);
		this.fitness = Objects.requireNonNull(fitness);
	}
	
	public static Model createModel(String jsonModel) {
		return null;
	}
	
	public boolean workable(Action a) {
		Iterator<Input> it = a.getInputs().iterator();
		while(it.hasNext()) {
			Input requirementToChecked = it.next();
			String entityName = requirementToChecked.getEntity().getName();
			Entity entityToChecked = environment.getEntity(entityName);
			if(entityToChecked.getQuantity() < requirementToChecked.getEntity().getQuantity()) {
				return false;
			}
		}
		return true;
	}
	
	public Action getWorkableAction() {
		for(Action a : actions) {
			if(this.workable(a)) {
				return a;
			}
		}
		return Action.EMPTY(1);
	}
	
	
	
}
