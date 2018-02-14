package org.ordogene.algorithme.jenetics;

import java.util.function.Function;
import java.util.function.Supplier;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.AbstractChromosome;
import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class Schedule extends AbstractChromosome<ActionGene> {
	
	private static final long serialVersionUID = 1L;
	
	private Function<Environment, ? extends Action> factory;
	private int length;
	private Supplier<Model> modelSupplier;
	
	public Schedule(
			ISeq<ActionGene> seq, 
			Function<Environment, ? extends Action> factory,
			Supplier<Model> modelSupplier,
			int length) {
		super(seq);
		this.modelSupplier = modelSupplier;
		this.factory = factory;
		this.length = length;
	}

	@Override
	public Chromosome<ActionGene> newInstance() {
		Model modelCopy = modelSupplier.get();
		return new Schedule(ActionGene.seq(IntRange.of(length), factory, /*modelCopy.getStartEnvironment(),*/ modelCopy)
				, factory, modelSupplier, length);
	}

	@Override
	public Chromosome<ActionGene> newInstance(ISeq<ActionGene> genes) {
		return new Schedule(genes, factory, modelSupplier, length);
	}
	
	public static Schedule of(Function<Environment, ? extends Action> factory, int length, Supplier<Model> modelSupplier) {
		Model modelCopy = modelSupplier.get();
		return new Schedule(ActionGene.seq(IntRange.of(length), factory, /*modelCopy.getCurrentEnvironment(),*/ modelCopy)
				, factory, modelSupplier, length);
	}


}
