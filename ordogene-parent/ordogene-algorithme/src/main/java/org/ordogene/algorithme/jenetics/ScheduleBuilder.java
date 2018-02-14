package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.file.models.Type;

import io.jenetics.Genotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;

public class ScheduleBuilder {
	
	private final ThreadHandler th;
	private final Model model;
	private final int POPULATION_SIZE = 100;
	
	public ScheduleBuilder(ThreadHandler th, Model model) {
		this.th = th;
		this.model = model;
	}
	
	public Action createAction(Environment currentEnvironment) {
		return model.getWorkableAction();
	}
	
	public boolean validateAction(Action action) {
		return model.workable(action);
	}

	public void run() {
		Engine<ActionGene, Double> engine = Engine
			.builder(this::fitness, Genotype.of(Schedule.of(this::createAction, model.getSlots(), () -> model.copy()), 1))
			.fitnessScaler(this::fitnessScaler)
			.populationSize(POPULATION_SIZE)
			.selector(new TournamentSelector<>())
			.alterers(new ScheduleCrossover(2))
			.build();
	}
	
	public Double fitnessScaler(Double value) {
		if (Type.value.equals(model.getFitness().getType())) {
			if (value == new Double(model.getFitness().getValue())) {
				return Double.MAX_VALUE;
			}
			return 1 / Math.pow((value - model.getFitness().getValue()), 2);
		}
		return value;
	}
	
	public Double fitness(Genotype<ActionGene> genotype) {
		return genotype.toSeq().stream()
				.flatMapToDouble(schedule -> schedule.toSeq().stream()
						.mapToDouble(action -> model.getFitness().eval(action.getAllele())))
				.sum();
	}

}
