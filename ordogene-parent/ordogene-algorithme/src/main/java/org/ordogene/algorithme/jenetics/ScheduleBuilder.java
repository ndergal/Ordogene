package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.file.models.Type;

import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
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

	public void run() {
		
		Engine<ActionGene, Long> engine = Engine
			.builder(this::fitness, Genotype.of(Schedule.of(model), 1))
			.optimize(Type.min.equals(model.getFitness().getType()) ? Optimize.MINIMUM : Optimize.MAXIMUM)
			//.fitnessScaler(this::fitnessScaler)
			.populationSize(POPULATION_SIZE)
			.selector(new TournamentSelector<>())
			.alterers(new ScheduleCrossover(0.2), new ScheduleMutator(0.005))
			.build();
		
		EvolutionStatistics<Long, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();
		
		Phenotype<ActionGene, Long> best = engine.stream()
			.limit(1000)
			.peek(result -> {
				//System.out.println(result.getGeneration() + " : " + result.getBestFitness());
				result.getPopulation().forEach(pheno -> {
					int i = 0;
					for (ActionGene g : pheno.getGenotype().getChromosome()) {
						//System.out.println((i++) + " " + g.getAllele());
					}
				});
			})
			.peek(statistics)
			.collect(EvolutionResult.toBestPhenotype());
		
		try {
			th.threadFromMaster();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		System.out.println(statistics);
//		best.getGenotype().getChromosome().forEach(g -> System.out.println(g.getAllele()));
	}
	
	/**
	 * Fitness Scaler de l'engine
	 * @param value
	 * @return
	 */
	public Double fitnessScaler(Double value) {
		if (Type.value.equals(model.getFitness().getType())) {
			if (value == new Double(model.getFitness().getValue())) {
				return Double.MAX_VALUE;
			}
			return 1 / Math.pow((value - model.getFitness().getValue()), 2);
		}
		return value;
	}
	
	/**
	 * Fonction de fitness de l'engine
	 * @param ind
	 * @return
	 */
	public Long fitness(Genotype<ActionGene> ind) {
		long startFitness = model.getFitness().evalEnv(model.getStartEnvironment());
		long transformationFitness = ind.stream()
				.flatMap(c -> c.stream())
				.mapToLong(ag -> model.getFitness().eval(ag.getAllele()))
				.sum();
		return startFitness + transformationFitness;
	}

}
