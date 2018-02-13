package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;

import io.jenetics.Genotype;
import io.jenetics.engine.Engine;

public class ScheduleBuilder {
	
	private final ThreadHandler th;
	private final Model model;
	
	public ScheduleBuilder(ThreadHandler th, Model model) {
		this.th = th;
		this.model = model;
	}

	public void run() {
		Engine<ActionGene, Double> engine = Engine
			.builder(this::fitness, Genotype.of(new Schedule()))
			.build();
	}
	
	public Double fitness(Genotype<ActionGene> genotype) {
		return genotype.toSeq().stream()
				.flatMapToDouble(schedule -> schedule.toSeq().stream()
						.mapToDouble(action -> model.getFitness().eval(action.getAllele())))
				.sum();
	}

}
