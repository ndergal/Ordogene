package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.Chromosome;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.internal.math.probability;
import io.jenetics.util.ISeq;

public class ScheduleMutator extends Mutator<ActionGene, Long> {

	public ScheduleMutator(double p) {
		super(p);
	}
	
	@Override
	protected MutatorResult<Chromosome<ActionGene>> mutate(final Chromosome<ActionGene> chromosome, final double p,
			final Random random) {
		System.out.println("NOUVELLE MUTATION");
		
		Schedule sch = (Schedule) chromosome;
		Model model = sch.getModel();

		// System.out.println("\n\nNEW SCHEDULE");
		model.resetModel();
		// Environment which evolve with the creation
		Environment currentEnv = model.getStartEnvironment().copy();
		// Map with action to stop at key value
		HashMap<Integer, List<Action>> map = new HashMap<>();
		// List which represent the sequence
		List<ActionGene> seq = new ArrayList<>();
		// Boolean to end action at current time
		// The current time
		int currentTime = 0;
		for (ActionGene gene : sch.toSeq()) {
			Action action = gene.getAllele();

			if (action.getName().equals("EMPTY")) {
				currentTime++;
				List<Action> actions = map.get(currentTime);
				if (actions != null) {
					for (Action a : actions) {
						model.endAction(currentEnv, a);
					}
				}
				map.remove(currentTime);
				seq.add(gene.newInstance());
				continue;
			}

			if (random.nextInt() < probability.toInt(p)) {
//				System.out.println("CHANGEMENT D'ACTION");
				
				ActionGene actiongene = ActionGene.of(currentEnv, currentTime, model);
//				System.out.println("Ancienne action : " + action + ", \nNouvelle action : " + actiongene.getAllele() + ", \ncurEnv : " + currentEnv);
				action = actiongene.getAllele();
				seq.add(actiongene);
				
			}

			else {				
				seq.add(gene.newInstance());
			}

			if (!model.workable(action, currentEnv, currentTime)) {
				System.out.println("MUTATION INVALIDE à cause de : " + action + "\nAvec + " + currentEnv);
				return MutatorResult.of(chromosome);
			}
			model.startAction(action, currentEnv, currentTime);
			int endTime = currentTime + action.getTime();
			List<Action> actions = map.get(endTime);

			if (actions == null) {
				actions = new ArrayList<>();
				map.put(endTime, actions);
			}
			actions.add(action);
		}


		System.out.println("MUTATION VALIDE");
		return MutatorResult.of(sch.newInstance(ISeq.of(seq)), 1);
	}
}
