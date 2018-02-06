package org.ordogene.algorithme.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.ordogene.algorithme.models.Action;

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
		NavigableMap<Long, Action> map = new TreeMap<>();
		long total = 0;
		
		for(SimpleEntry<Action, Long> e : actions) {
			total += e.getValue() + lowerWeight;
			map.put(total, e.getKey());
		}
		
		long value = (random.nextLong()%total) + 1;
		return map.ceilingEntry(value).getValue();
	}
	
}
