package org.ordogene.algorithme;

import java.util.ArrayList;

import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;
import org.ordogene.algorithme.models.Relation;
import org.ordogene.algorithme.models.Type;

public class Main {
	public static void main(String[] args) {
		ArrayList<Entity> entities = new ArrayList<>();
		entities.add(new Entity("A", 10));
		entities.add(new Entity("B", 1));
		entities.add(new Entity("C", 5));
		entities.add(new Entity("D", 0));
		entities.add(new Entity("E", 0));
		entities.add(new Entity("F", 0));
		
		Environment env = new Environment(entities);
		
		ArrayList<Input> inputs = new ArrayList<>();
		inputs.add(new Input(new Entity("A", 2), Relation.c));
		inputs.add(new Input(new Entity("B", 1), Relation.c));
		
		ArrayList<Entity> outputs = new ArrayList<>();
		outputs.add(new Entity("D", 3));
		
		Action a = new Action("1", 1, inputs, outputs);
		
		Model m = new Model(15, 1000, env, new ArrayList<>(), new Fitness(Type.max, new ArrayList<>()));
		
		System.out.println(m.workable(a));
		
	}
}
