package org.ordogene.algorithme.jenetics;

import java.util.List;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Environment;

public class ActionFactoryObjectValue {
	private Model model;
	private Environment currentEnvironment;
	private Timeline timeline;
	private List<ActionGene> runningActions;
	private ActionGene currentAction;
	
	public ActionFactoryObjectValue(Model model, 
			Environment currentEnvironment,
			Timeline timeline,
			List<ActionGene> runningActions,
			ActionGene currentAction) {
		this.model = model;
		this.currentEnvironment = currentEnvironment;
		this.timeline = timeline;
		this.runningActions = runningActions;
		this.currentAction = currentAction;
	}

	public Model getModel() {
		return model;
	}

	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}
	
	public Timeline getTimeline() {
		return timeline;
	}
	
	public List<ActionGene> getRunningActions() {
		return runningActions;
	}
	
	public ActionGene getCurrentAction() {
		return currentAction;
	}
}
