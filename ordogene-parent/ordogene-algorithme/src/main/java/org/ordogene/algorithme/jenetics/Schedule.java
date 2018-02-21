package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.AbstractChromosome;
import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;

public class Schedule extends AbstractChromosome<ActionGene> {

	private static final long serialVersionUID = 1L;

	private Model model;

	public Schedule(ISeq<ActionGene> seq, Model model) {
		super(seq);
		this.model = model;
	}


	@Override
	public Chromosome<ActionGene> newInstance() {
		return Schedule.of(model);
	}

	@Override
	public Chromosome<ActionGene> newInstance(ISeq<ActionGene> genes) {
		return new Schedule(genes, model);
	}

	public static Schedule of(Model model) {
		System.out.println("\n\nNEW SCHEDULE");
		model.resetModel();
		// Environment which evolve with the creation
		Environment currentEnv = model.getStartEnvironment().copy();
		// Map with action to stop at key value
		HashMap<Integer, List<Action>> map = new HashMap<>();
		// List which represent the sequence
		ArrayList<ActionGene> seq = new ArrayList<>();
		// Boolean to end action at current time
		boolean needToEndAction = true;
		// The current time
		int currentTime = 0;

		// Continue while a action is in progress or a Action is possible
		// TODO add possibility to stop while and stop Schedule building
		while (model.hasWorkableAction(currentEnv, currentTime) || !map.isEmpty()) {
			// if it's the first time at currentTime when end actions
			if (needToEndAction) {
				endAll(map, currentTime, model, currentEnv);
				needToEndAction = false;
			}

			// Select a action
			System.out.println("Time :" + currentTime);
			ActionGene actionGene = ActionGene.of(currentEnv, currentTime, model);
			Action action = actionGene.getAllele();

			// Add action in the seq
			seq.add(actionGene);

			System.out.println("Action : " + action);
			if(!action.equals(Action.EMPTY())) {
				// Start the action
				model.startAction(action, currentEnv, currentTime);
	
				// Add action in map to end it
				int endTime = currentTime + action.getTime();
				List<Action> actions = map.get(endTime);
				if (actions == null) {
					actions = new ArrayList<>();
					map.put(endTime, actions);
				}
				actions.add(action);
			} else {
				// If the action is the Empty action so change the currentTime
				currentTime++;
				needToEndAction = true;
			}
		}
		System.out.println("End Env : " + currentEnv);
		return new Schedule(ISeq.of(seq), model/*, currentEnv*/);
	}

	private static void endAll(HashMap<Integer, List<Action>> map, int currentTime, Model model,
			Environment currentEnvironment) {
		List<Action> actions = map.get(currentTime);
		if (actions != null) {
			for (Action a : actions) {
				model.endAction(currentEnvironment, a);
			}
		}
		map.remove(currentTime);
	}

}
