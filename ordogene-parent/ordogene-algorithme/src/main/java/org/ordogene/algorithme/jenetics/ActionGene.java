package org.ordogene.algorithme.jenetics;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.Gene;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

public class ActionGene implements Gene<Action, ActionGene> {

	private final Action action;
	private Function<ActionFactoryObjectValue, ? extends Action> supplier;
	private Environment currentEnvironment;
	private Model model;
	private MSeq<ActionGene> curSeq;
	
	public ActionGene(Action action, 
			Function<ActionFactoryObjectValue, ? extends Action> supplier, 
			Environment currentEnvironment, 
			Model model,
			MSeq<ActionGene> curSeq) {
		this.action = action;
		this.supplier = supplier;
		this.currentEnvironment = currentEnvironment;
		this.model = model;
		this.curSeq = curSeq;
	}

	@Override
	public boolean isValid() {
		return model.workable(action, currentEnvironment);
	}

	@Override
	public Action getAllele() {
		return action;
	}

	@Override
	public ActionGene newInstance() {
		model.startAnAction(currentEnvironment, action);
		return new ActionGene(supplier.apply(new ActionFactoryObjectValue(model, currentEnvironment.copy(), action, curSeq.toISeq())), 
				supplier, 
				currentEnvironment, 
				model,
				curSeq);
	}

	@Override
	public ActionGene newInstance(Action action) {
		model.startAnAction(currentEnvironment, action);
		return new ActionGene(action, supplier, currentEnvironment.copy(), model, curSeq);
	}
	
	public static ActionGene of(Function<ActionFactoryObjectValue, ? extends Action> factory, 
			Environment currentEnvironment, 
			Model model,
			MSeq<ActionGene> curSeq) {
		Action curAction = null;
		List<ActionGene> genes = curSeq.toISeq().stream().filter(g -> g != null).collect(Collectors.toList());
		if (genes.size() > 0) {
			curAction = genes.get(genes.size() - 1).getAllele();
		}
		return new ActionGene(factory.apply(new ActionFactoryObjectValue(model, currentEnvironment.copy(), curAction, curSeq.toISeq()))
				, factory, currentEnvironment.copy(), model, curSeq);
	}
	
	static ISeq<ActionGene> seq(
			final int length,
			final Function<ActionFactoryObjectValue, ? extends Action> factory,
			Model model
		) {
			MSeq<ActionGene> curSeq = MSeq.ofLength(length);
			return curSeq.fill(() -> of(factory, model.getStartEnvironment(), model, curSeq))
				.toISeq();
		}

}
