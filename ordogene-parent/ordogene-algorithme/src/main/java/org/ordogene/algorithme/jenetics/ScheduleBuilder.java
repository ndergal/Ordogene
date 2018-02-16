package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.algorithme.models.Action;
import org.ordogene.file.models.Type;

import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;

public class ScheduleBuilder {
	
	private final ThreadHandler th;
	private final Model model;
	private final int POPULATION_SIZE = 100;
	
	public ScheduleBuilder(ThreadHandler th, Model model) {
		this.th = th;
		this.model = model;
	}
	
	public Action createAction(ActionFactoryObjectValue bundle) {
		Action action = model.getWorkableAction(bundle.getCurrentEnvironment());
		model.startAnAction(bundle.getCurrentEnvironment(), action);
		return action;
	}

	public void run() {
		Engine<ActionGene, Double> engine = Engine
			.builder(this::fitness, Genotype.of(Schedule.of(this::createAction, model.getSlots(), () -> model.copy()), 1))
			.optimize(Type.min.equals(model.getFitness().getType())?Optimize.MINIMUM:Optimize.MAXIMUM)
			.fitnessScaler(this::fitnessScaler)
			.populationSize(POPULATION_SIZE)
//			.selector(new TournamentSelector<>())
			.alterers(new ScheduleCrossover(0.2))
			.build();
		
		EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();
		
		Phenotype<ActionGene, Double> best = engine.stream()
			.limit(1)
			.peek(result -> System.out.println(result.getGeneration() + " : " + result.getBestFitness()))
			.peek(statistics)
			.collect(EvolutionResult.toBestPhenotype());
		try {
			th.threadFromMaster();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		System.out.println(statistics);
		best.getGenotype().forEach(c -> c.forEach(a -> System.out.println(a.getAllele())));
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
