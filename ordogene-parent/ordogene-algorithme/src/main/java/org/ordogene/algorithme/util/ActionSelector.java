package org.ordogene.algorithme.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import io.jenetics.util.RandomRegistry;

public class ActionSelector {
	private Random random = RandomRegistry.getRandom();
	
	private long lowerWeight = 0;
	private ArrayList<SimpleEntry<Action, Long>> actions = new ArrayList<>();
	
	public boolean isReset() {
		return actions.isEmpty();
	}
	
	public void reset() {
		lowerWeight = 0;
		actions.clear();
	}
	
	public void add(Action a, long weight) {
		lowerWeight = Long.min(lowerWeight, weight);
		actions.add(new SimpleEntry<>(a, weight));
	}
	
	public Action select() {
		if(actions.isEmpty()) {
			throw new IllegalStateException("You can select in a empty selector");
		}
		
		NavigableMap<Long, Action> map = new TreeMap<>();
		long total = 0;
		
		for(SimpleEntry<Action, Long> e : actions) {
			Long weight = e.getValue();
			total += weight + (Math.abs(lowerWeight) + 1);
			map.put(total, e.getKey());
		}
		
		if(total == 0) {
			throw new IllegalStateException("The ActionSelector have an problem with action's weight.");
		}
		
		long value = (Math.abs(random.nextLong())%total) + 1;
		return map.ceilingEntry(value).getValue();
	}
	
}
