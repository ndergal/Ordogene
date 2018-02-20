package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	//private Environment endEnvironment;

	public Schedule(ISeq<ActionGene> seq, Model model/*, Environment endEnvironement*/) {
		super(seq);
		this.model = model;
		//this.endEnvironment = endEnvironement;
	}

	// /**
	// * Factory d'action utilisé par l'engine de jenetics
	// * @param bundle
	// * @return
	// */
	// public static Action createAction(ActionFactoryObjectValue bundle) {
	// Model model = bundle.getModel();
	// bundle.getCurSeq().stream().filter(g -> g != null).forEach(g -> {
	// Action a = g.getAllele();
	// if (bundle.getCurrentAction() != null &&
	// a.getStartTime() + a.getTime() == bundle.getCurrentAction().getStartTime()) {
	// model.endAnAction(bundle.getCurrentEnvironment(), a);
	// }
	// });
	//
	// Action action = model.getWorkableAction(bundle.getCurrentEnvironment());
	//
	// if (bundle.getCurrentAction() != null) {
	// if ("EMPTY".equals(bundle.getCurrentAction().getName()) &&
	// bundle.getCurrentAction().getName().equals(action.getName())) {
	// action.setStartTime(bundle.getCurrentAction().getStartTime() + 1);
	// } else {
	// action.setStartTime(bundle.getCurrentAction().getStartTime());
	// }
	// } else {
	// action.setStartTime(0);
	// }
	// model.startAnAction(bundle.getCurrentEnvironment(), action);
	// return action;
	// }

//	public Environment getEndEnvironment() {
//		return endEnvironment;
//	}

	@Override
	public Chromosome<ActionGene> newInstance() {
		return Schedule.of(model);
	}

	@Override
	public Chromosome<ActionGene> newInstance(ISeq<ActionGene> genes) {
		return new Schedule(genes, model/*, currentEnv*/);
	}

	public static Schedule of(Model model) {
		System.out.println("\n\nNEW SCHEDULE");
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

		// TODO change endpoint condition ==> when empty is the only one action possible
		// with currentEnv
		while (currentTime < model.getSlots()) {
			// if it's the first time at currentTime when end actions
			if (needToEndAction) {
				endAll(map, currentTime, model, currentEnv);
				needToEndAction = false;
			}

			// Select a action
			ActionGene actionGene = ActionGene.of(currentEnv, model);
			Action action = actionGene.getAllele();

			// Start the action
			model.startAnAction(currentEnv, action);

			// Add action in map to end it
			int endTime = currentTime + action.getTime();
			List<Action> actions = map.get(endTime);
			if (actions == null) {
				actions = new ArrayList<>();
				map.put(endTime, actions);
			}
			actions.add(action);

			// Add action in the seq
			seq.add(actionGene);

			// If the action is the Empty action so change the currentTime
			if (action.equals(Action.EMPTY())) {
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
				model.endAnAction(currentEnvironment, a);
			}
		}
		map.remove(currentTime);
	}

}
