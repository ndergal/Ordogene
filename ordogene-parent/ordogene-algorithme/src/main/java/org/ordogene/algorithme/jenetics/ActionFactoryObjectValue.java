package org.ordogene.algorithme.jenetics;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Environment;

import io.jenetics.util.ISeq;

public class ActionFactoryObjectValue {
	private Model model;
	private Environment currentEnvironment;
	private Action currentAction;
	private ISeq<ActionGene> curSeq;
	
	public ActionFactoryObjectValue(Model model, Environment currentEnvironment,
			Action currentAction, ISeq<ActionGene> curSeq) {
		super();
		this.model = model;
		this.currentEnvironment = currentEnvironment;
		this.currentAction = currentAction;
		this.curSeq = curSeq;
	}

	public Model getModel() {
		return model;
	}

	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public ISeq<ActionGene> getCurSeq() {
		return curSeq;
	}
	
}
