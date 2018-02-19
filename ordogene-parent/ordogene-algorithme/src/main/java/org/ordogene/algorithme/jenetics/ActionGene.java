package org.ordogene.algorithme.jenetics;

import java.util.LinkedList;
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
	private Model model;
	private Environment currentEnvironment;
	private int startTime;
	
	public ActionGene(Action action, 
			Function<ActionFactoryObjectValue, ? extends Action> supplier,
			Model model,
			Environment currentEnvironment,
			int startTime) {
		this.action = action;
		this.supplier = supplier;
		this.model = model;
		this.currentEnvironment = currentEnvironment;
		this.startTime = startTime;
	}

	@Override
	public boolean isValid() {
		return model.workable(action, currentEnvironment);
	}

	@Override
	public Action getAllele() {
		return action;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public Environment getCurrentEnvironment() {
		return currentEnvironment.copy();
	}

	@Override
	public ActionGene newInstance() {
		return new ActionGene(action, supplier, model, currentEnvironment, startTime);
	}

	@Override
	public ActionGene newInstance(Action action) {
		return new ActionGene(action, supplier, model, currentEnvironment, startTime);
	}
	
	public static ActionGene of(Function<ActionFactoryObjectValue, ? extends Action> factory, 
			Model model,
			Timeline timeline,
			List<ActionGene> runningActions,
			MSeq<ActionGene> curSeq) {
		
		ActionGene currentAction = null;
		Environment currentEnvironment = model.getStartEnvironment().copy();
		List<ActionGene> currentActions = curSeq.toISeq().stream().filter(a -> a != null).collect(Collectors.toList());
		if (currentActions.size() > 0) {
			currentAction = currentActions.get(currentActions.size() - 1);
			currentEnvironment = currentActions.get(currentActions.size() - 1).getCurrentEnvironment();
		}
		
		ActionGene newAction = new ActionGene(
				factory.apply(
						new ActionFactoryObjectValue(
								model, 
								currentEnvironment, 
								timeline, 
								runningActions,
								currentAction)), 
				factory, model, currentEnvironment, timeline.getTime());
		
		runningActions.add(newAction);
		
		return newAction;
	}
	
	static ISeq<ActionGene> seq(
			final int length,
			final Function<ActionFactoryObjectValue, ? extends Action> factory,
			Model model
		) {
			MSeq<ActionGene> curSeq = MSeq.ofLength(length);
			Timeline timeline = new Timeline();
			List<ActionGene> runningActions = new LinkedList<>();
			return curSeq.fill(() -> of(factory, model, timeline, runningActions, curSeq))
				.toISeq();
		}

}
