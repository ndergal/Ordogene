package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.algorithme.models.Action;
import org.ordogene.file.models.Type;

import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;

public class ScheduleBuilder {
	
	private final ThreadHandler th;
	private final Model model;
	private final int POPULATION_SIZE = 100;
	
	public ScheduleBuilder(ThreadHandler th, Model model) {
		this.th = th;
		this.model = model;
	}
	
	public Action createAction(ActionFactoryObjectValue bundle) {
		bundle.getCurSeq().forEach(g -> {
			Action a = g.getAllele();
			if (a.getStartTime() + a.getTime() == bundle.getCurrentAction().getStartTime()) {
				bundle.getModel().endAnAction(bundle.getCurrentEnvironment(), a);
			}
		});
		Action action = model.getWorkableAction(bundle.getCurrentEnvironment());
		model.startAnAction(bundle.getCurrentEnvironment(), action);
		return action;
	}

	public void run() {
		Codec<ISeq<ActionGene>, ActionGene> codec = Codec.of(
				Genotype.of(Schedule.of(this::createAction, model.getSlots(), () -> model.copy())), 
				gt -> gt.getChromosome().toSeq());
		
		Engine<ActionGene, Double> engine = Engine
			.builder(this::fitness, codec)
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
	
	public Double fitness(ISeq<ActionGene> ind) {
		return ind.stream()
				.mapToDouble(action -> model.getFitness().eval(action.getAllele()))
				.sum();
	}

}
