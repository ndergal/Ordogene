package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.Crossover;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

public class ScheduleCrossover extends Crossover<ActionGene, Long> {
	
	private final Model model;

	public ScheduleCrossover(Model model, double probability) {
		super(probability);
		this.model = model;
	}

	@Override
	protected int crossover(MSeq<ActionGene> father, MSeq<ActionGene> mother) {
		synchronized (model) {
			model.resetModel();
			// Environment which evolve with the creation
			Environment envAfterStart = model.getStartEnvironment().copy();
			Environment envAfterEnd = model.getStartEnvironment().copy();
			
			// Map with action to stop at key value
			SortedMap<Integer, List<Action>> map = new TreeMap<>();
			
			// List which represent the children sequence
			ArrayList<ActionGene> seq = new ArrayList<>();
			
			// The current time
			int timeAtStart = 0;
			int timeAtEnd = 0;
			
			List<ActionGene> fatherList = father.asList();
			boolean[] fatherActionAvailable = new boolean[father.size()];
			List<ActionGene> motherList = mother.asList();
			boolean[] motherActionAvailable = new boolean[mother.size()];
	
			// Continue while a action is in progress or a Action is possible
			while (model.hasWorkableAction(envAfterEnd, timeAtEnd) || !map.isEmpty()) {
				int coin = flipCoin();
				
				// SELECT AN ACTION ON A PARENT AND CHANGE ITS AVAILIBILITY ON OTHER PARENT
				Action action = null;
				// check father
				if(coin == 0) {
					action = getAction(fatherList, envAfterEnd, timeAtEnd, fatherActionAvailable);
					if(action == null) {
						break;
					}
					removeAction(action , motherList, motherActionAvailable);
				} else {
					// check mother
					action = getAction(motherList, envAfterEnd, timeAtEnd, motherActionAvailable);
					if(action == null) {
						break;
					}
					removeAction(action , fatherList, fatherActionAvailable);
				}
	
				
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
				
			}
			
			if(flipCoin() == 0) {
				father = MSeq.of(seq);
			} else {
				mother = MSeq.of(seq);
			}
			
			return 1;
		}
	}

	private void removeAction(Action actionToRemove, List<ActionGene> list, boolean[] actionAvailable) {
		for(int i = 0; i < list.size(); i++) {
			if(actionAvailable[i]) {
				Action action = list.get(i).getAllele();
				if(actionToRemove.equals(action)) {
					actionAvailable[i] = false;
					return;
				}
			}
		}
	}

	private Action getAction(List<ActionGene> list, Environment envAfterEnd, int timeAtEnd, boolean[] actionAvailable) {
		for(int i = 0; i < list.size(); i++) {
			if(actionAvailable[i]) {
				Action action = list.get(i).getAllele();
				if (model.workable(action, envAfterEnd, timeAtEnd)) {
					return action;
				}
			}
		}
		return null;
	}
	
	private int flipCoin() {
		Random rand = RandomRegistry.getRandom();
		return rand.nextInt(2);
	}

}
