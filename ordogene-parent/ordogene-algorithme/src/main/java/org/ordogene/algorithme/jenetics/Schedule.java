package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.AbstractChromosome;
import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

public class Schedule extends AbstractChromosome<ActionGene> {

	private static final long serialVersionUID = 1L;

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
		//System.out.println("\n\nNEW SCHEDULE");
		model.resetModel();
		// Environment which evolve with the creation
		Environment envAfterStart = model.getStartEnvironment().copy();
		Environment envAfterEnd = model.getStartEnvironment().copy();
		// Map with action to stop at key value
		SortedMap<Integer, List<Action>> map = new TreeMap<>();
		// List which represent the sequence
		ArrayList<ActionGene> seq = new ArrayList<>();
		// Boolean to end action at current time
		// The current time
		int timeAtStart = 0;
		int timeAtEnd = 0;

		// Continue while a action is in progress or a Action is possible
		// TODO add possibility to stop while and stop Schedule building
		while (model.hasWorkableAction(envAfterEnd, timeAtEnd) || !map.isEmpty()) {

			// Select a action
			//System.out.println("Time :" + currentTime);
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
			
			if(timeAtEnd == timeAtStart) {
				// Remove action which ended in previous startTime
				map.remove(timeAtEnd);
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
		
		map.remove(timeAtEnd);
		final Environment finalEnv = envAfterEnd;
		map.forEach((endTime,actionList) -> {
			for(Action a : actionList) {
				model.endAction(finalEnv, a);
			}
		});
		
		System.out.println("Seq : " + seq);
		System.out.println("Size seq : " + seq.size());
		System.out.println("End Env :" + finalEnv);
		return new Schedule(ISeq.of(seq), model);
	}

}
