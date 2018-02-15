package org.ordogene.algorithme.jenetics;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.Gene;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

public class ActionGene implements Gene<Action, ActionGene> {

	private final Action action;
	private BiFunction<Model, Environment, ? extends Action> supplier;
	private Environment currentEnvironment;
	private Model model;
	
	public ActionGene(Action action, BiFunction<Model, Environment, ? extends Action> supplier, Environment currentEnvironment, Model model) {
		this.action = action;
		this.supplier = supplier;
		this.currentEnvironment = currentEnvironment;
		this.model = model;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Action getAllele() {
		return action;
	}

	@Override
	public ActionGene newInstance() {
		model.startAnAction(currentEnvironment, action);
		return new ActionGene(supplier.apply(model, currentEnvironment), supplier, currentEnvironment, model);
	}

	@Override
	public ActionGene newInstance(Action action) {
		model.startAnAction(currentEnvironment, action);
		return new ActionGene(action, supplier, currentEnvironment, model);
	}
	
	public static ActionGene of(BiFunction<Model, Environment, ? extends Action> factory, Environment currentEnvironment, Model model) {
		return new ActionGene(factory.apply(model, currentEnvironment), factory, currentEnvironment, model);
	}
	
	static ISeq<ActionGene> seq(
			final int length,
			final BiFunction<Model, Environment, ? extends Action> factory,
			//Environment currentEnvironment,
			Model model
		) {
			return MSeq.<ActionGene>ofLength(length)
				.fill(() -> of(factory, /*currentEnvironment*/model.getStartEnvironment(), model))
				.toISeq();
		}

}
