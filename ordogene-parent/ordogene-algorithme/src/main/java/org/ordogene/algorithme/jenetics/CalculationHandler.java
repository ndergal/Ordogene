package org.ordogene.algorithme.jenetics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.file.FileService;
import org.ordogene.file.models.Type;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;

public class CalculationHandler {
	private final int POPULATION_SIZE = 10;
	private final double CHANCE_TO_STOP_SCHEDULE_CREATION = 0.01;
	
	private final Date currentDate = new Date();
	private final ThreadHandler th;
	private final Model model;
	private final String userId;
	private final int calculationId;
	
	public CalculationHandler(ThreadHandler th, Model model, String userId, int calculationId) {
		this.th = th;
		this.model = model;
		this.userId = userId;
		this.calculationId = calculationId;
	}

	public void launchCalculation() {
		
		Engine<ActionGene, Long> engine = Engine
			.builder(this::fitness, Genotype.of(Schedule.of(model, CHANCE_TO_STOP_SCHEDULE_CREATION), 1))
			.optimize(Type.min.equals(model.getFitness().getType()) ? Optimize.MINIMUM : Optimize.MAXIMUM)
			.fitnessScaler(this::fitnessScaler)
			.populationSize(POPULATION_SIZE)
			.selector(new TournamentSelector<>())
			.alterers(new ScheduleCrossover(0.2))
			.build();
		
		EvolutionStatistics<Long, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();
		
		Iterator<EvolutionResult<ActionGene, Long>> itEngine = engine.iterator();
		int iteration = 0;
		int maxIteration = 10; // TODO change it by model.getExecTime()
		boolean interupted = false;
		Phenotype<ActionGene, Long> best = null;
		
		while(itEngine.hasNext() && iteration < maxIteration && !interupted) {
			EvolutionResult<ActionGene, Long> generation = itEngine.next();
			statistics.accept(generation);
			
			best = generation.getBestPhenotype();
			
			iteration++;

			try {
				String str = th.threadFromMaster();
				if (str != null && str.equals("state")) {
					// TODO change 1 by real value
					String msg = constructStateString(iteration, maxIteration, 1,best.getFitness());
					th.threadToMaster(msg.toString());
				} else if (str != null && str.equals("interrupt")) {
					interupted = true;
				}
			} catch (InterruptedException e) {
				interupted = true;
				Thread.currentThread().interrupt();
			}
		}

		// TODO change 1 by real value
		Calculation tmpCalc = new Calculation();
		
		if(best != null) {
			tmpCalc.setCalculation(currentDate.getTime(), iteration, 1, maxIteration, calculationId,
					model.getName(), best.getFitness());
		} else {
			tmpCalc.setCalculation(currentDate.getTime(), iteration, 1, maxIteration, calculationId,
					model.getName(), 0);
		}
		
		try {
			String calculationSaveDest = Const.getConst().get("ApplicationPath") + File.separator + userId
					+ File.separator + tmpCalc.getId() + "_" + model.getName() + File.separatorChar + "state.json";
			FileService.writeInFile(tmpCalc, Paths.get(calculationSaveDest));
			System.out.println(tmpCalc + " saved in " + calculationSaveDest);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(tmpCalc + " not saved.");
		}
		
		System.out.println(statistics);
	}

	private String constructStateString(int iteration, int maxIter, int lastIterationSaved, long fitness) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentDate.getTime()).append("_");
		sb.append(iteration).append("_");
		sb.append(lastIterationSaved).append("_");
		sb.append(maxIter).append("_");
		sb.append(fitness);
		return sb.toString();
	}
	
	/**
	 * Fitness Scaler de l'engine
	 * @param value
	 * @return
	 */
	public Long fitnessScaler(Long value) {
		if (Type.value.equals(model.getFitness().getType())) {
			if (value == model.getFitness().getValue()) {
				return Long.MAX_VALUE;
			}
			return (long) (Long.MAX_VALUE / Math.pow((value - model.getFitness().getValue()), 2));
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
