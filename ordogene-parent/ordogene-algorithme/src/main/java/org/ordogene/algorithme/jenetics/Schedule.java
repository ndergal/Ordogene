package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * Represents an individual (as a sequance of Actions)
 * @author darwinners team
 *
 */
public class Schedule implements Chromosome<ActionGene> {

	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);

	private final ISeq<ActionGene> seq;
	private final Model model;
	private final Environment endEnv;
	private final long duration;

	public Schedule(ISeq<ActionGene> seq, Model model, Environment endEnv, long duration) {
		this.seq = seq;
		this.model = model;
		this.endEnv = endEnv;
		this.duration = duration;
	}

	@Override
	public Chromosome<ActionGene> newInstance() {
		// jenetics parallelise un maximum de tâches, comme nous utilisons un unique model pour tous les individus
		// il faut bloquer sont accès lors de la création d'un individu pour éviter les états incohérents du model
		synchronized (model) {
			return of(model, 0.001);
		}
	}

	@Override
	public Chromosome<ActionGene> newInstance(ISeq<ActionGene> genes) {
		long newDuration = genes.stream()
								.mapToLong(ag -> ag.getStartTime() + ag.getAllele().getTime())
								.max().getAsLong();
		return new Schedule(genes, model, model.calculEndEnvironment(genes), newDuration);
	}

	public Environment getEndEnv() {
		return endEnv;
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
			if (action != null) {

				// Parallele start
				if (model.workable(action, envAfterStart, timeAtStart)) {
					// Create ActionGene and add it on seq
					ActionGene actionGene = ActionGene.of(action, timeAtStart);
					seq.add(actionGene);

					// Change StartEnvironment
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
				List<Action> actions = map.computeIfAbsent(actionEndTime, (key) -> new ArrayList<>());
				actions.add(action);
			}

			if (timeAtEnd == timeAtStart || action == null) {
				// Remove action which ended in previous startTime
				map.remove(timeAtEnd);
				if (map.isEmpty()) {
					break;
				}
			}

			// Change timeAtEnd
			timeAtEnd = map.firstKey();

			// Change EndEnvironment
			envAfterEnd = envAfterStart.copy();
			for (Action a : map.get(timeAtEnd)) {
				model.endAction(envAfterEnd, a);
			}

			double randomValue = RandomRegistry.getRandom().nextDouble();
			if (randomValue < probaToStop) {
				break;
			}

		}
		return new Schedule(ISeq.of(seq), model, envAfterEnd, timeAtEnd);

	}

	public Model getModel() {
		return model;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public boolean isValid() {
		return seq.stream().allMatch(ActionGene::isValid);
	}

	@Override
	public Iterator<ActionGene> iterator() {
		return seq.iterator();
	}

	@Override
	public ActionGene getGene(int index) {
		return seq.get(index);
	}

	@Override
	public int length() {
		return seq.length();
	}

	@Override
	public ISeq<ActionGene> toSeq() {
		return seq;
	}

}
