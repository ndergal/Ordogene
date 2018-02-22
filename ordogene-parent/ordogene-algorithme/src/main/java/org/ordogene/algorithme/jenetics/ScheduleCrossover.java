package org.ordogene.algorithme.jenetics;

import io.jenetics.Crossover;
import io.jenetics.util.MSeq;

public class ScheduleCrossover extends Crossover<ActionGene, Long> {

	public ScheduleCrossover(double probability) {
		super(probability);
	}

	@Override
	protected int crossover(MSeq<ActionGene> papa, MSeq<ActionGene> mama) {
		return 0;
	}

}
