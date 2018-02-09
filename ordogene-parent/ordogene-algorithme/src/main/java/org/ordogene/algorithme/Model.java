package org.ordogene.algorithme;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;
import org.ordogene.algorithme.util.ActionSelector;
import org.ordogene.file.JSONModel;
import org.ordogene.file.models.Relation;
import org.ordogene.file.models.Type;

public class Model {
	private final List<Integer> snaps;
	private final int slots;
	private final int execTime;
	private final Environment startEnvironment;
	private final HashMap<Action, Integer> actions = new HashMap<>();
	private final Fitness fitness;	
	
	private Environment currentEnvironment;
	
	private ActionSelector actionSelector;
	
	//TODO check les null dans les list
	private Model(List<Integer> snaps, int slots, int execTime, Environment environment, List<Action> actions, Fitness fitness) {
		if(slots <= 0) {
			throw new IllegalArgumentException("slots has to be a positive integer");
		}
		if (execTime <= 0) {
			throw new IllegalArgumentException("execTime has to be a positive integer");
		}
		this.snaps = Objects.requireNonNull(snaps);
		this.slots = slots;
		this.execTime = execTime;
		this.startEnvironment = Objects.requireNonNull(environment);
		this.currentEnvironment = new Environment(environment.getEntities());
		actions.forEach(a -> this.actions.put(a, 0));;
		this.fitness = Objects.requireNonNull(fitness);
		this.actionSelector = new ActionSelector();
	}

	public static Model createModel(JSONModel jm){
		Environment env = new Environment(jm.getEnvironment().stream().map(Entity::createEntity).collect(Collectors.toSet()));
		List<Action> actions = jm.getActions().stream().map(Action::createAction).collect(Collectors.toList());
		List<Integer> snaps = jm.getSnaps().stream().collect(Collectors.toList());
		return new Model(snaps, jm.getSlots(), jm.getExecTime(), env, actions, Fitness.createFitness(jm.getFitness()));
	}

	
	/**
	 * Check if the action can be done with the actual environment
	 * @param a The action to checked
	 * @return True if the action can be done, else False
	 */
	public boolean workable(Action a) {
		if(actions.get(a) == null) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		Iterator<Input> it = a.getInputs().iterator();
		while(it.hasNext()) {
			Input input = it.next();
			Entity entityToChecked = currentEnvironment.getEntity(input.getName());
			if(entityToChecked.getQuantity() < input.getQuantity()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Give an {@link Action} workable in the environment
	 * @return an {@link Action workable} else the empty Action
	 */
	public Action getWorkableAction() {
		if(!actionSelector.isReset()) {
			//Select one action here
			return actionSelector.select();
		}
		actionSelector.add(Action.EMPTY(1), 0);
		for(Action a : actions.keySet()) {
			if(this.workable(a)) {
				actionSelector.add(a, fitness.eval(a));
			}
		}
		return actionSelector.select();
	}
	
	public void startAnAction(Action a) {
		if(actions.get(a) == null) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		if(!workable(a)) {
			throw new IllegalArgumentException("The Action given can be start");
		}
		Iterator<Input> it = a.getInputs().iterator();
		while(it.hasNext()) {
			Input input = it.next();
			String inputEntityName = input.getName();
			Relation inputType = input.getRelation();
			if (inputType == Relation.c || inputType == Relation.p) {
				Entity environmentEntity = currentEnvironment.getEntity(inputEntityName);
				int quantityToRemoved = input.getQuantity();
				environmentEntity.addQuantity(-quantityToRemoved);
			}
		}
		actions.compute(a, (k,i) -> { return i+1; });
	}
	
	public void endAnAction(Action a) {
		Integer actionsInProgress = actions.get(a);
		if(actionsInProgress == null) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		if(actionsInProgress == 0) {
			throw new IllegalArgumentException("The Action given was not launched");
		}
		for(Input input : a.getInputs()) {
			String inputEntityName = input.getName();
			Relation inputType = input.getRelation();
			if (inputType == Relation.p) {
				Entity environmentEntity = currentEnvironment.getEntity(inputEntityName);
				int quantityToAdd = input.getQuantity();
				environmentEntity.addQuantity(quantityToAdd);
			}
		}
		
		for(Entity outputEntity : a.getOutputs()) {
			String outputEntityName = outputEntity.getName();
			Entity environmentEntity = currentEnvironment.getEntity(outputEntityName);
			int quantityToAdd = outputEntity.getQuantity();
			environmentEntity.addQuantity(quantityToAdd);
		}
		
		actions.compute(a, (k,i) -> { return i-1; });
	}

	public static void main(String[] args) {
		Set<Entity> entities = new HashSet<>();
		entities.add(new Entity("A", 10));
		entities.add(new Entity("B", 1));
		entities.add(new Entity("C", 5));
		entities.add(new Entity("D", 0));
		entities.add(new Entity("E", 0));
		entities.add(new Entity("F", 0));
		
		Environment env = new Environment(entities);
		
		ArrayList<Input> inputs = new ArrayList<>();
		inputs.add(new Input("A", 2, Relation.c));
		inputs.add(new Input("B", 1, Relation.c));
		
		ArrayList<Entity> outputs = new ArrayList<>();
		outputs.add(new Entity("D", 3));
		
		Action a = new Action("1", 1, inputs, outputs);
		
		ArrayList<Action> actions = new ArrayList<>();
		actions.add(a);
		
		HashMap<String,Long> h = new HashMap<>();
		h.put("A", Long.valueOf(8));
		
		Fitness f = new Fitness(Type.max, h);
		
		Model m = new Model(Collections.emptyList(), 15, 1000, env, actions, f);
		
		System.out.println(m.workable(a));
		
		System.out.println(m.getWorkableAction());
		System.out.println(m.getWorkableAction());
		
	}
}
