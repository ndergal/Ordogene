package org.ordogene.algorithme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;
import org.ordogene.algorithme.models.Relation;
import org.ordogene.algorithme.models.Type;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Model {
	private final Optional<List<Integer>> snaps;
	private final int slots;
	private final int execTime;
	private final Environment startEnvironment;
	private Environment currentEnvironment;
	private final ArrayList<Action> actions;
	private final Fitness fitness;
	
	private Long totalWeight = Long.valueOf(0);
	private Long lowerWeight = Long.valueOf(0);
	private HashMap<Action, Long> workableActionCache = new HashMap<>();
	
	
	private Model(Optional<List<Integer>> snaps, int slots, int execTime, Environment environment, ArrayList<Action> actions, Fitness fitness) {
		if(slots <= 0) {
			throw new IllegalArgumentException("slots has to be a positive integer");
		}
		if(execTime <= 0) {
			throw new IllegalArgumentException("execTime has to be a positive integer");
		}
		this.snaps = Objects.requireNonNull(snaps);
		this.slots = slots;
		this.execTime = execTime;
		this.startEnvironment = Objects.requireNonNull(environment);
		this.currentEnvironment = new Environment(environment.getEntities());
		this.actions = Objects.requireNonNull(actions);
		this.fitness = Objects.requireNonNull(fitness);
	}

	public static Model createModel(Path jsonPath) throws IOException {
		Map<String, String> myMap = parseJsonFile(jsonPath);

		return null;
	}

	private static Map<String, String> parseJsonFile(Path jsonPath)
			throws IOException, JsonParseException, JsonMappingException {
		byte[] jsonData = Files.readAllBytes(jsonPath);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> myMap = new HashMap<>();
		myMap = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, String>>() {
		});
		return myMap;
	}
	
	/**
	 * Check if the action can be done with the actual environment
	 * @param a The action to checked
	 * @return True if the action can be done, else False
	 */
	public boolean workable(Action a) {
		Iterator<Input> it = a.getInputs().iterator();
		while(it.hasNext()) {
			Input requirementToChecked = it.next();
			String entityName = requirementToChecked.getEntity().getName();
			Entity entityToChecked = currentEnvironment.getEntity(entityName);
			if(entityToChecked.getQuantity() < requirementToChecked.getEntity().getQuantity()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Give an {@link Action} workable in the environment
	 * @return an {@link Action workable} else the empty Action
	 */
	//TODO add random
	public Action getWorkableAction() {
		if(!workableActionCache.isEmpty()) {
			//Select one action here
			return getOneActionInCache();
		}
		for(Action a : actions) {
			if(this.workable(a)) {
				Long actionWeight = actionWeight(a);
				totalWeight += actionWeight;
				workableActionCache.put(a, actionWeight);
			}
		}
		
		return getOneActionInCache();
	}
	
	private Long actionWeight(Action a) {
		return fitness.eval(a);
	}

	private Action getOneActionInCache() {
		// TODO Auto-generated method stub
		return null;
	}

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
		
		HashMap<String,Long> h = new HashMap<>();
		h.put("A", Long.valueOf(8));
		
		Fitness f = new Fitness(Type.max, h);
		
		Model m = new Model(Optional.empty(), 15, 1000, env, new ArrayList<>(), f);
		
		System.out.println(m.workable(a));
		
		try {
			System.out.println(Model.parseJsonFile(Paths.get("/home/ordogene/Documents/examples/fitness1.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
