package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jenetics.AbstractChromosome;
import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

public class Schedule extends AbstractChromosome<ActionGene> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);

	private Model model;

	public Schedule(ISeq<ActionGene> seq, Model model) {
		super(seq);
		this.model = model;
	}

	@Override
	public Chromosome<ActionGene> newInstance() {
		return new Schedule(_genes, model);
	}

	@Override
	public Chromosome<ActionGene> newInstance(ISeq<ActionGene> genes) {
		return new Schedule(genes, model);
	}


	public static Schedule of(Model model, double probaToStop) {
		logger.debug("\n\nNEW SCHEDULE");
		model.resetModel();
		// Environment which evolve with the creation
		Environment currentEnv = model.getStartEnvironment().copy();
		// Map with action to stop at key value
		HashMap<Integer, List<Action>> map = new HashMap<>();
		// List which represent the sequence
		ArrayList<ActionGene> seq = new ArrayList<>();
		// Boolean to end action at current time
		// The current time
		int currentTime = 0;

		// Continue while a action is in progress or a Action is possible
		// TODO add possibility to stop while and stop Schedule building
		while (model.hasWorkableAction(currentEnv, currentTime) || !map.isEmpty()) {

			// Select a action
			logger.debug("Time :" + currentTime);

			ActionGene actionGene = ActionGene.of(currentEnv, currentTime, model);
			Action action = actionGene.getAllele();

			// Add action in the seq
			seq.add(actionGene);


			logger.debug("Action : " + action);
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
				// If the action is the Empty action so change the currentTime and finish action
				currentTime++;
				endAll(map, currentTime, model, currentEnv);
			}
			
			double randomValue = RandomRegistry.getRandom().nextDouble();
			if(randomValue < probaToStop) {
				completeSchedule(seq, map, currentTime, model, currentEnv);
				break;
			}
			
		}

		System.out.println("Seq : " + seq);
		System.out.println("End Env :" + currentEnv);

		return new Schedule(ISeq.of(seq), model);
	}

	private static void completeSchedule(ArrayList<ActionGene> seq, HashMap<Integer, List<Action>> map, int currentTime, Model model,
			Environment currentEnvironment) {
		while(!map.isEmpty()) {
			List<Action> actions = map.get(currentTime);
			if (actions != null) {
				for (Action a : actions) {
					model.endAction(currentEnvironment, a);
				}
				seq.add(ActionGene.emptyActionGene());
			}
			map.remove(currentTime);
			currentTime++;
		}
		
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


	public Model getModel() {
		return model;
	}

}
