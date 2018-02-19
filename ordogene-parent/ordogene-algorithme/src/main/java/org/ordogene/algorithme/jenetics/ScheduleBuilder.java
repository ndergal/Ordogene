package org.ordogene.algorithme.jenetics;

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
		Model model = bundle.getModel();
		bundle.getCurSeq().stream().filter(g -> g != null).forEach(g -> {
			Action a = g.getAllele();
			if (bundle.getCurrentAction() != null && 
					a.getStartTime() + a.getTime() == bundle.getCurrentAction().getStartTime()) {
				model.endAnAction(bundle.getCurrentEnvironment(), a);
			}
		});
		
		Action action = model.getWorkableAction(bundle.getCurrentEnvironment());
		
		if (bundle.getCurrentAction() != null) {
			if ("EMPTY".equals(bundle.getCurrentAction().getName()) && 
					bundle.getCurrentAction().getName().equals(action.getName())) {
				action.setStartTime(bundle.getCurrentAction().getStartTime() + 1);
			} else {
				action.setStartTime(bundle.getCurrentAction().getStartTime());
			}
		} else {
			action.setStartTime(0);
		}
		model.startAnAction(bundle.getCurrentEnvironment(), action);
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
						System.out.println((i++) + " " + g.getAllele());
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
	public Double fitness(Genotype<ActionGene> ind) {
		return ind.stream()
				.flatMapToDouble(c -> c.stream()
						.mapToDouble(action -> model.getFitness().eval(action.getAllele())))
				.sum();
	}

}
