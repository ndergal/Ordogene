package org.ordogene.algorithme;

import java.util.HashMap;
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

public class Model {
	private final List<Integer> snaps;
	private final String name;
	private final int slots;
	private final int execTime;
	private final Environment startEnvironment;
	private final HashMap<Action, Integer> actionsInProgress = new HashMap<>();
	private final Fitness fitness;

	private Environment currentEnvironment;

	private ActionSelector actionSelector = new ActionSelector();

	public Model(List<Integer> snaps, String name, int slots, int execTime, Environment environment,
			Set<Action> actions, Fitness fitness) {
		if (slots <= 0) {
			throw new IllegalArgumentException("slots has to be a positive integer");
		}
		if (execTime <= 0) {
			throw new IllegalArgumentException("execTime has to be a positive integer");
		}
		if (Objects.requireNonNull(name).isEmpty()) {
			throw new IllegalArgumentException("The name can't be empty");
		}
		this.name = name;
		this.snaps = Objects.requireNonNull(snaps);
		this.slots = slots;
		this.execTime = execTime;
		this.startEnvironment = Objects.requireNonNull(environment);
		this.currentEnvironment = new Environment(environment.getEntities());
		actions.forEach(a -> {
			this.actionsInProgress.put(Objects.requireNonNull(a), 0);
		});
		this.fitness = Objects.requireNonNull(fitness);
	}

	public static Model createModel(JSONModel jm) {
		Objects.requireNonNull(jm);
		Environment env = new Environment(
				jm.getEnvironment().stream().map(Entity::createEntity).collect(Collectors.toSet()));
		Set<Action> actions = jm.getActions().stream().map(Action::createAction).collect(Collectors.toSet());
		List<Integer> snaps = jm.getSnaps().stream().collect(Collectors.toList());
		return new Model(snaps, jm.getName(), jm.getSlots(), jm.getExecTime(), env, actions,
				Fitness.createFitness(jm.getFitness()));
	}

	public String getName() {
		return name;
	}

	/**
	 * Check if the action can be done with the actual environment
	 * 
	 * @param a
	 *            The action to checked
	 * @return True if the action can be done, else False
	 */
	public boolean workable(Action a) {
		if (actionsInProgress.get(a) == null) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		Iterator<Input> it = a.getInputs().iterator();
		while (it.hasNext()) {
			Input input = it.next();
			Entity entityToChecked = currentEnvironment.getEntity(input.getName());
			if (entityToChecked.getQuantity() < input.getQuantity()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Give an {@link Action} workable in the environment
	 * 
	 * @return an {@link Action workable} else the empty Action
	 */
	public Action getWorkableAction() {
		if (!actionSelector.isReset()) {
			// Select one action here
			return actionSelector.select();
		}
		actionSelector.add(Action.EMPTY(1), 0);
		for (Action a : actionsInProgress.keySet()) {
			if (workable(a)) {
				actionSelector.add(a, fitness.eval(a));
			}
		}
		return actionSelector.select();
	}

	public void startAnAction(Action a) {
		Objects.requireNonNull(a);
		if (actionsInProgress.get(a) == null) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		if (!workable(a)) {
			throw new IllegalArgumentException("The Action given can be start");
		}
		for (Input input : a.getInputs()) {
			String inputEntityName = input.getName();
			Relation inputType = input.getRelation();
			if (inputType == Relation.c || inputType == Relation.p) {
				Entity environmentEntity = currentEnvironment.getEntity(inputEntityName);
				int quantityToRemoved = input.getQuantity();
				environmentEntity.addQuantity(-quantityToRemoved);
			}
		}
		actionsInProgress.compute(a, (k, i) -> {
			return i + 1;
		});
	}

	public void endAnAction(Action a) {
		Objects.requireNonNull(a);
		Integer action = actionsInProgress.get(a);
		if (action == null) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		if (action == 0) {
			throw new IllegalArgumentException("The Action given was not launched");
		}
		for (Input input : a.getInputs()) {
			String inputEntityName = input.getName();
			Relation inputType = input.getRelation();
			if (inputType == Relation.p) {
				Entity environmentEntity = currentEnvironment.getEntity(inputEntityName);
				int quantityToAdd = input.getQuantity();
				environmentEntity.addQuantity(quantityToAdd);
			}
		}

		for (Entity outputEntity : a.getOutputs()) {
			String outputEntityName = outputEntity.getName();
			Entity environmentEntity = currentEnvironment.getEntity(outputEntityName);
			int quantityToAdd = outputEntity.getQuantity();
			environmentEntity.addQuantity(quantityToAdd);
		}

		actionsInProgress.put(a, action - 1);
	}

	public int getExecTime() {
		return execTime;
	}

}
