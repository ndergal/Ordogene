package org.ordogene.algorithme.jenetics;

import java.util.List;
import java.util.stream.Collectors;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.algorithme.models.Action;
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
	private final int POPULATION_SIZE = 2;
	
	public ScheduleBuilder(ThreadHandler th, Model model) {
		this.th = th;
		this.model = model;
	}
	
	/**
	 * Factory d'action utilisé par l'engine de jenetics
	 * @param bundle
	 * @return
	 */
	public Action createAction(ActionFactoryObjectValue bundle) {
		List<ActionGene> toEnd = bundle.getRunningActions().stream().filter(g -> g.getStartTime() == bundle.getTimeline().getTime()).collect(Collectors.toList());
		toEnd.forEach(ag -> bundle.getModel().endAnAction(bundle.getCurrentEnvironment(), ag.getAllele()));
		bundle.getRunningActions().removeAll(toEnd);
		
		Action action = bundle.getModel().getWorkableAction(bundle.getCurrentEnvironment());
		
		if ("EMPTY".equals(action.getName()) && bundle.getCurrentAction() != null) {
			if (action.getName().equals(bundle.getCurrentAction().getAllele().getName())) {
				bundle.getTimeline().step();
			} 
		}
		
		bundle.getModel().startAnAction(bundle.getCurrentEnvironment(), action);
		return action;
	}

	public void run() {
//		Codec<ISeq<ActionGene>, ActionGene> codec = Codec.of(
//				Genotype.of(Schedule.of(this::createAction, model.getSlots(), () -> model.copy())), 
//				gt -> gt.getChromosome().toSeq());
		
		Engine<ActionGene, Double> engine = Engine
			.builder(this::fitness, Genotype.of(Schedule.of(this::createAction, model.getSlots(), () -> model.copy()), 1))
			.optimize(Type.min.equals(model.getFitness().getType())?Optimize.MINIMUM:Optimize.MAXIMUM)
			.fitnessScaler(this::fitnessScaler)
			.populationSize(POPULATION_SIZE)
			.selector(new TournamentSelector<>())
			.alterers(new ScheduleCrossover(0.2))
			.build();
		
		EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();
		
		Phenotype<ActionGene, Double> best = engine.stream()
			.limit(1)
			.peek(result -> {
				System.out.println(result.getGeneration() + " : " + result.getBestFitness());
				result.getPopulation().forEach(pheno -> {
					int i = 0;
					for (ActionGene g : pheno.getGenotype().getChromosome()) {
						System.out.println((i++) + " " + g.getAllele() + " start time : " + g.getStartTime());
					}
				});
			})
			.peek(statistics)
			.collect(EvolutionResult.toBestPhenotype());
		
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
	public Double fitness(Genotype<ActionGene> ind) {
		return ind.stream()
				.flatMapToDouble(c -> c.stream()
						.mapToDouble(action -> model.getFitness().eval(action.getAllele())))
				.sum();
	}

}
