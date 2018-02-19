package org.ordogene.algorithme.jenetics;

import java.util.function.Function;
import java.util.function.Supplier;

import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.models.Action;

import io.jenetics.AbstractChromosome;
import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;

public class Schedule extends AbstractChromosome<ActionGene> {
	
	private static final long serialVersionUID = 1L;
	
	private Model model;
	
	public Schedule(ISeq<ActionGene> seq, Model model) {
		super(seq);
		this.model = model;
	}
	
	/**
	 * Factory d'action utilisé par l'engine de jenetics
	 * @param bundle
	 * @return
	 */
	public static Action createAction(ActionFactoryObjectValue bundle) {
		Model model = bundle.getModel();
		bundle.getCurSeq().stream().filter(g -> g != null).forEach(g -> {
			Action a = g.getAllele();
			if (bundle.getCurrentAction() != null && 
					a.getStartTime() + a.getTime() == bundle.getCurrentAction().getStartTime()) {
				model.endAnAction(bundle.getCurrentEnvironment(), a);
			}
		});
		
		Action action = model.getWorkableAction(bundle.getCurrentEnvironment());
		
		if (bundle.getCurrentAction() != null) {
			if ("EMPTY".equals(bundle.getCurrentAction().getName()) && 
					bundle.getCurrentAction().getName().equals(action.getName())) {
				action.setStartTime(bundle.getCurrentAction().getStartTime() + 1);
			} else {
				action.setStartTime(bundle.getCurrentAction().getStartTime());
			}
		} else {
			action.setStartTime(0);
		}
		model.startAnAction(bundle.getCurrentEnvironment(), action);
		return action;
	}

	@Override
	public Chromosome<ActionGene> newInstance() {
		return new Schedule(ActionGene.seq(Schedule::createAction, model), model);
	}

	@Override
	public Chromosome<ActionGene> newInstance(ISeq<ActionGene> genes) {
		return new Schedule(genes, model);
	}
	
	public static Schedule of(Model model) {
		return new Schedule(ActionGene.seq(Schedule::createAction, model), model);
	}


}
