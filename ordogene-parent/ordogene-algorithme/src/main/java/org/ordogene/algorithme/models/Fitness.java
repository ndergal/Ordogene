package org.ordogene.algorithme.models;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import org.ordogene.file.models.JSONFitness;
import org.ordogene.file.models.Type;

public class Fitness {
	private final Type type;
	private final HashMap<String, Long> operands = new HashMap<>();

	public Fitness(Type type, HashMap<String, Long> operands) {
		this.type = Objects.requireNonNull(type);
		Objects.requireNonNull(operands);
		for(Entry<String, Long> e : operands.entrySet()) {
			String name = Objects.requireNonNull(e.getKey());
			Long coef = Objects.requireNonNull(e.getValue());
			this.operands.put(name, coef);
		}
	}
	
	public static Fitness createFitness(JSONFitness jf) {
		HashMap<String, Long> operands = new HashMap<>();
		jf.getOperands().forEach(jo -> {
			operands.put(jo.getName(), jo.getCoef());
		});
		return new Fitness(jf.getType(), operands);
	}
	
	/**
	 * Evaluate the fitness for the given {@link Action}
	 * @param a {@link Action} to evaluate
	 * @return the fitness
	 */
	public long eval(Action a) {
		Objects.requireNonNull(a);
		int score = 0;
		for(Input i : a.getInputs()) {
			Objects.requireNonNull(i);
			String entityName = i.getName();
			long coef = operands.getOrDefault(entityName, Long.valueOf(0));
			long quantity = i.getQuantity();
			score -= coef * quantity;
		}
		for(Entity e : a.getOutputs()) {
			Objects.requireNonNull(e);
			String entityName = e.getName();
			long coef = operands.getOrDefault(entityName, Long.valueOf(0));
			long quantity = e.getQuantity();
			score += coef * quantity;
		}
		return score;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * operands.hashCode();
		result = prime * result + type.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Fitness))
			return false;
		Fitness fit = (Fitness) obj;
		if (!operands.equals(fit.operands))
			return false;
		if (type != fit.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Fitness [type=" + type + ", operands=" + operands + "]";
	}
}
