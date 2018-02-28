package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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

	private final Model model;

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
		Environment envAfterStart = model.getStartEnvironment().copy();
		Environment envAfterEnd = model.getStartEnvironment().copy();
		// Map with action to stop at key value
		SortedMap<Integer, List<Action>> map = new TreeMap<>();
		// List which represent the sequence
		ArrayList<ActionGene> seq = new ArrayList<>();
		// The current time
		int timeAtStart = 0;
		int timeAtEnd = 0;

		// Continue while a action is in progress or a Action is possible
		while (model.hasWorkableAction(envAfterEnd, timeAtEnd)) {

			// Select a action
			Action action = model.getWorkableAction(envAfterEnd, timeAtEnd);
			if(action != null) {
				
				// Parallele start
				if(model.workable(action, envAfterStart, timeAtStart)) {
					// Create ActionGene and add it on seq
					ActionGene actionGene = ActionGene.of(action, timeAtStart);
					seq.add(actionGene);

					//Change StartEnvironment
					model.startAction(action, envAfterStart, timeAtStart);
					
				} else {
				// start After
					ActionGene actionGene = ActionGene.of(action, timeAtEnd);
					seq.add(actionGene);
					
					// Change startTime
					timeAtStart = timeAtEnd;
					
					// Change StartEnvironment
					envAfterStart = envAfterEnd;
					model.startAction(action, envAfterStart, timeAtStart);
				}
				
				// Calcul actionEndTime
				int actionEndTime = timeAtStart + action.getTime();
				// Add action in map to end it later
				List<Action> actions = map.get(actionEndTime);
				if (actions == null) {
					actions = new ArrayList<>();
					map.put(actionEndTime, actions);
				}
				actions.add(action);
			}
			
			if(timeAtEnd == timeAtStart || action == null) {
				// Remove action which ended in previous startTime
				map.remove(timeAtEnd);
				if(map.isEmpty()) {
					break;
				}
			}
			
			// Change timeAtEnd
			timeAtEnd = map.firstKey();
			
			// Change EndEnvironment
			envAfterEnd = envAfterStart.copy();
			for(Action a : map.get(timeAtEnd)) {
				model.endAction(envAfterEnd, a);
			}
			
			double randomValue = RandomRegistry.getRandom().nextDouble();
			if(randomValue < probaToStop) {
				break;
			}
			
		}
		return new Schedule(ISeq.of(seq), model);
	}


	public Model getModel() {
		return model;
	}

}
