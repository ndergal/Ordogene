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
	private Environment currentEnvironment;
	private Model model;
	
	public ActionGene(Action action, Environment currentEnvironment, Model model, MSeq<ActionGene> curSeq) {
		this.action = action;
		this.currentEnvironment = currentEnvironment;
		this.model = model;
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
		return new ActionGene(action, currentEnvironment, model, curSeq);
	}

	@Override
	public ActionGene newInstance(Action action) {
		model.startAnAction(currentEnvironment, action);
		return new ActionGene(action, currentEnvironment.copy(), model, curSeq);
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
		return new ActionGene(curAction, currentEnvironment.copy(), model, curSeq);
	}
	
	static ISeq<ActionGene> seq(Function<ActionFactoryObjectValue, ? extends Action> factory, Model model) {
			MSeq<ActionGene> curSeq = MSeq.ofLength(model.getSlots()); //TODO to change with correct value
			return curSeq.fill(() -> of(factory, model.getStartEnvironment(), model, curSeq)).toISeq();
		}

}
