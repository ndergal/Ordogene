package org.ordogene.algorithme.jenetics;

/**
 * Chargé de la gestion des temps de démarrage des actions
 * @author ordogene
 */
public class Timeline {
	private int time = 0;
	
	public void step() {
		step(1);
	}
	
	public void step(int t) {
		time += t;
	}
	
	public int getTime() {
		return time;
	}
}
