package org.ordogene.algorithme.jenetics;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Executors;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.file.FileUtils;
import org.ordogene.file.models.Type;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;

/**
 * Handle one calculation per instance
 * 
 * @author darwinners team
 *
 * 
 */
public class CalculationHandler {
	private final Logger logger = LoggerFactory.getLogger(CalculationHandler.class);

	private final int POPULATION_SIZE = 100;
	private final double CHANCE_TO_STOP_SCHEDULE_CREATION = 0.002;

	private final ThreadHandler th;
	private final Model model;
	private final String username;
	private final int cid;

	public CalculationHandler(ThreadHandler th, Model model, String username, int cid) {
		this.th = th;
		this.model = model;
		this.username = username;
		this.cid = cid;
	}

	/**
	 * launch its calculation
	 */
	public void launchCalculation() {

		FileUtils.createCalculationDirectory(Paths.get(FileUtils.getCalculationDirectoryPath(username, cid, model.getName())));
		
		Engine<ActionGene, Long> engine = Engine
				.builder(this::fitness, Genotype.of(Schedule.of(model, CHANCE_TO_STOP_SCHEDULE_CREATION), 1))
				.optimize(Type.min.equals(model.getFitness().getType()) ? Optimize.MINIMUM : Optimize.MAXIMUM)
				.fitnessScaler(this::fitnessScaler).populationSize(POPULATION_SIZE).selector(new TournamentSelector<>())
				.alterers(new ScheduleCrossover(model, 0.2)).executor(Executors.newSingleThreadExecutor()).build();

		// Initialize all variable to do loop
		// The engine have an iterator which gie next generation on next method
		Iterator<EvolutionResult<ActionGene, Long>> itEngine = engine.iterator();
		// A variable to count the number of loop done
		int iteration = 0;
		// The number of loop to do
		int maxIteration = model.getExecTime();
		// A boolean to end correctly the calculation
		boolean interupted = false;
		// The best element of the population (to draw it later)
		Phenotype<ActionGene, Long> best = null;
		long lastSave = System.currentTimeMillis();
		long lastSavedIteration = 0;

		while (itEngine.hasNext() && iteration < maxIteration && !interupted) {
			// Get the next generation
			EvolutionResult<ActionGene, Long> generation = itEngine.next();

			// Get the best member
			best = generation.getBestPhenotype();

			// Increased the iteration variable
			iteration++;

			// Block to read master commands
			try {
				// Get the message
				String cmd = th.threadFromMaster();
				// If the message is state, give informations
				// If the message is interrupt, stop the loop
				if (cmd != null && cmd.equals("interrupt")) {
					interupted = true;
				}
			} catch (InterruptedException e) {
				interupted = true;
				Thread.currentThread().interrupt();
			}
			
			updateState(iteration, lastSavedIteration, best.getFitness());

			long currentTime = System.currentTimeMillis();
			int interval = Integer.parseInt(Const.getConst().getOrDefault("ResultSaveInterval", "60")) * 1000;
			if (lastSave + interval < currentTime) {
				Calculation tmpCalc = new Calculation();

				lastSave = currentTime;
				lastSavedIteration = generation.getGeneration();
				tmpCalc.setCalculation(th.getCalculation().getStartTimestamp(), iteration, iteration, maxIteration, cid,
						model.getName(), best.getFitness());

				saveBest(best);
			}
		}

		// Create a calculation information to saved it on disk
		Calculation tmpCalc = new Calculation();
		if (best != null) {
			tmpCalc.setCalculation(th.getCalculation().getStartTimestamp(), iteration, iteration, maxIteration, cid,
					model.getName(), best.getFitness());

			saveBest(best);
		} else {
			tmpCalc.setCalculation(th.getCalculation().getStartTimestamp(), iteration, iteration, maxIteration, cid,
					model.getName(), 0);
		}

		// Save the information
		saveState(tmpCalc);
	}

	private void updateState(int iterationNumber, long lastIterationSaved, long fitnessSaved) {
		Calculation c = th.getCalculation();
		c.setIterationNumber(iterationNumber);
		c.setLastIterationSaved(lastIterationSaved);
		c.setFitnessSaved(fitnessSaved);
	}

	private void saveState(Calculation tmpCalc) {
		try {
			FileUtils.writeJsonInFile(tmpCalc, username, tmpCalc.getId(), tmpCalc.getName());
			logger.info("{} saved", tmpCalc);
		} catch (IOException e) {
			logger.debug(Arrays.toString(e.getStackTrace()));
			logger.error("{} not saved", tmpCalc);
		}
	}

	private void saveBest(Phenotype<ActionGene, Long> best) {
		ActionGene[][] actionGeneArray = Drawer.buildStringActionMatrix(best);
		String htmlTableHeader = Drawer.buildHtmlTableHeader("", actionGeneArray);
		Schedule s = (Schedule) best.getGenotype().getChromosome();
		String htmlArray = Drawer.htmlTableBuilder(htmlTableHeader, actionGeneArray, model, best.getFitness(),
				s.getEndEnv(), false);
		logger.info("try to save : pngFile and htmlFile ... ");
		if (FileUtils.saveResult(htmlArray, Paths.get(FileUtils.getCalculationDirectoryPath(username, cid, model.getName())))) {
			logger.info(" Success ");
		} else {
			logger.info(" Fail ");
		}
	}

	/**
	 * Fitness Scaler de l'engine
	 * 
	 * @param value
	 * @return
	 */
	private Long fitnessScaler(Long value) {
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
	 * 
	 * @param ind
	 * @return the fitness of this calculation
	 */
	private Long fitness(Genotype<ActionGene> ind) {
		long startFitness = model.getFitness().evalEnv(model.getStartEnvironment());
		long transformationFitness = ind.stream().flatMap(c -> c.stream())
				.mapToLong(ag -> model.getFitness().eval(ag.getAllele())).sum();
		return startFitness + transformationFitness - ((Schedule) ind.getChromosome()).getDuration();
	}

}
