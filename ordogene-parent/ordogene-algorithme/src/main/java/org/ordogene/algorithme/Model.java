package org.ordogene.algorithme;

<<<<<<< HEAD
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
import java.util.ArrayList;
import java.util.Iterator;
>>>>>>> branch 'dev' of https://ndergal@bitbucket.org/darwinners/ordogene.git
import java.util.Objects;
import java.util.Optional;

import org.ordogene.algorithme.models.Action;
import org.ordogene.algorithme.models.Entity;
import org.ordogene.algorithme.models.Environment;
import org.ordogene.algorithme.models.Fitness;
import org.ordogene.algorithme.models.Input;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Model {
	private final Optional<List<Integer>> snaps;
	private final int slots;
	private final int execTime;
	private final Environment environment;
<<<<<<< HEAD
	private final List<Action> actions;
	private final List<Fitness> fitnesses;

	private Model(Optional<List<Integer>> snaps, int slots, int execTime, Environment environment, List<Action> actions,
			List<Fitness> fitnesses) {
		if (slots <= 0) {
=======
	private final ArrayList<Action> actions;
	private final Fitness fitness;
	
	
	//TODO change to public temporally
	public Model(int slots, int execTime,Environment environment,ArrayList<Action> actions,Fitness fitness) {
		if(slots <= 0) {
>>>>>>> branch 'dev' of https://ndergal@bitbucket.org/darwinners/ordogene.git
			throw new IllegalArgumentException("slots has to be a positive integer");
		}
		if (execTime <= 0) {
			throw new IllegalArgumentException("execTime has to be a positive integer");
		}
		this.snaps = Objects.requireNonNull(snaps);
		this.slots = slots;
		this.execTime = execTime;
		this.environment = Objects.requireNonNull(environment);
		this.actions = Objects.requireNonNull(actions);
		this.fitness = Objects.requireNonNull(fitness);
	}

	public static Model createModel(Path jsonPath) throws IOException {
		Map<String, String> myMap = parseJsonFile(jsonPath);

		return null;
	}
<<<<<<< HEAD

	private static Map<String, String> parseJsonFile(Path jsonPath)
			throws IOException, JsonParseException, JsonMappingException {
		byte[] jsonData = Files.readAllBytes(jsonPath);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> myMap = new HashMap<>();
		myMap = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, String>>() {
		});
		return myMap;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(Model.parseJsonFile(Paths.get("/home/ordogene/Documents/examples/fitness1.json")));
	}
=======
	
	public boolean workable(Action a) {
		Iterator<Input> it = a.getInputs().iterator();
		while(it.hasNext()) {
			Input requirementToChecked = it.next();
			String entityName = requirementToChecked.getEntity().getName();
			Entity entityToChecked = environment.getEntity(entityName);
			if(entityToChecked.getQuantity() < requirementToChecked.getEntity().getQuantity()) {
				return false;
			}
		}
		return true;
	}
	
	public Action getWorkableAction() {
		for(Action a : actions) {
			if(this.workable(a)) {
				return a;
			}
		}
		return Action.EMPTY(1);
	}
	
	
	
>>>>>>> branch 'dev' of https://ndergal@bitbucket.org/darwinners/ordogene.git
}
