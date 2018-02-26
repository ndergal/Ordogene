package org.ordogene.algorithme;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.HashSet;
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

public class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String CURRENT_TIME_CANNOT_BE_NEGATIVE = "The current time cannot be negative";
	private final List<Integer> snaps;
	private final String name;
	private final int slots;
	private final int execTime;
	private final Environment startEnvironment;
	private final Set<Action> actions = new HashSet<>();
	private final Fitness fitness;
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
		actions.forEach(a -> this.actions.add(Objects.requireNonNull(a)));
		this.actions.add(Action.EMPTY());
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
	
	public boolean hasWorkableAction(Environment currentEnvironment, int currentTime) {
		if(currentTime < 0) {
			throw new IllegalArgumentException(CURRENT_TIME_CANNOT_BE_NEGATIVE);
		}
		return actions.stream()
				.filter(a -> !a.equals(Action.EMPTY()))
				.anyMatch(a -> this.workable(a, currentEnvironment, currentTime));
	}

	/**
	 * Check if the action can be done with the actual environment
	 * 
	 * @param a
	 *            The action to checked
	 * @param currentEnvironment 
	 * @return True if the action can be done, else False
	 */
	public boolean workable(Action a, Environment currentEnvironment, int currentTime) {
		if(currentTime < 0) {
			throw new IllegalArgumentException(CURRENT_TIME_CANNOT_BE_NEGATIVE);
		}
		if (!isInModel(a)) {
			throw new IllegalArgumentException("The Action given don't exist in this model");
		}
		return a.getInputs().stream().allMatch(input -> input.getQuantity() <= currentEnvironment.getEntity(input.getName()).getQuantity()) 
				&& ((currentTime + a.getTime()) <= slots);
	}

	/**
	 * Give an {@link Action} workable in the environment
	 * @param currentEnvironment2 
	 * 
	 * @return an {@link Action workable} else the empty Action
	 */
	public Action getWorkableAction(Environment currentEnvironment, int currentTime) {
		if(currentTime < 0) {
			throw new IllegalArgumentException(CURRENT_TIME_CANNOT_BE_NEGATIVE);
		}
		if (!actionSelector.isReset()) {
			// Select one action here
			return actionSelector.select();
		}
		for (Action a : actions) {
			if (workable(a, currentEnvironment, currentTime)) {
				actionSelector.add(a, fitness.eval(a));
			}
		}
		return actionSelector.select();
	}

	public void startAction(Action a, Environment currentEnvironment, int currentTime) {
		if(currentTime < 0) {
			throw new IllegalArgumentException(CURRENT_TIME_CANNOT_BE_NEGATIVE);
		}
		requireNonNull(a);
		requireNonNull(currentEnvironment);
		if (!isInModel(a)) {
			throw new IllegalArgumentException("The Action given doesn't exist in this model");
		}
		if (!workable(a, currentEnvironment, currentTime)) {
			throw new IllegalArgumentException("The Action given cannot be started");
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
		actionSelector.reset();
	}

	public void endAction(Environment currentEnvironment, Action a) {
		requireNonNull(a);
		requireNonNull(currentEnvironment);
		if (!isInModel(a)) {
			throw new IllegalArgumentException("The given Action doesn't exist in this model");
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
		actionSelector.reset();
	}
	
	public Fitness getFitness() {
		return fitness;
	}
	
	public int getSlots() {
		return slots;
	}

	public Environment getStartEnvironment() {
		return startEnvironment;
	}
	
	public void resetModel() {
		actionSelector.reset();
	}

	public boolean isInModel(Action action) {
		return actions.contains(action);
	}

	public int getExecTime() {
		return execTime;
	}

}
