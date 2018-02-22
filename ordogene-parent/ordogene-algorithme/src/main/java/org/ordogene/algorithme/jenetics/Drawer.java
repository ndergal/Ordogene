package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JLabel;

import org.ordogene.algorithme.models.Action;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.jenetics.Phenotype;

public class Drawer {

	static void drawActionList(Phenotype<ActionGene, Long> individu) { // maxSize = model.getslot()
		Schedule s = (Schedule) individu.getGenotype().getChromosome();
		List<Action> actions = s.stream().map(ActionGene::getAllele).collect(Collectors.toList());
		int maxSize = s.getModel().getSlots();
		HashMap<Integer, Action> indexedActions = new HashMap<>();
		int nextIndex = 1; // index start from 1
		List<int[]> indexedList = new ArrayList<>(); // list = (Y axis on the paper) int[] = X axis
		int time = 0;
		int realMaxSize = 0;
		int[] currentArray = new int[maxSize];
		for (Action action : actions) {
			int line = searchFreeCell(indexedList, time); // Find the next index : the next free int[] at (time). <-
															// current list index
			if (line == -1) {
				// no free space at (time) : create a new int[]
				int[] newLine = new int[maxSize];
				Arrays.fill(newLine, -1);
				indexedList.add(newLine);
				line = indexedList.size() - 1;
			}
			if (action.equals(Action.EMPTY())) {
				addIntToArray(0, indexedList.get(line), time, action.getTime());
				time++;
			} else {
				indexedActions.put(nextIndex, action);
				addIntToArray(nextIndex, indexedList.get(line), time, action.getTime());
				if (time + action.getTime() > realMaxSize) {
					realMaxSize = time + action.getTime();
				}
				nextIndex++;
			}

			int[][] toPrintArray2 = new int[indexedList.size()][];
			toPrintArray2 = indexedList.toArray(toPrintArray2);
		}

		// remove all "-1" useless cells

		final int realMaxSizef = realMaxSize;
		System.out.println("realMaxSizef = " + realMaxSizef);
		//
		// for (int i = 0; i < indexedList.size(); i++) {
		// int[] intArray = indexedList.get(i);
		// System.out.println("intArray before = " + intArray.length);
		// int[] truncatedArray = new int[realMaxSizef];
		// System.arraycopy(intArray, 0, truncatedArray, 0, realMaxSizef);
		// indexedList.set(i, truncatedArray);
		// System.out.println("truncatedArray after = " + truncatedArray.length);
		// }
		boolean displayEmpty = true;
		List<String[]> actionReplacedList = new ArrayList<>();
		indexedList.forEach(intArray -> {
			int[] truncatedArray = new int[realMaxSizef];
			System.arraycopy(intArray, 0, truncatedArray, 0, realMaxSizef);

			actionReplacedList.add(replaceRefByAction(truncatedArray, indexedActions, displayEmpty));

		});

		String[][] toPrintArray = new String[actionReplacedList.size()][];
		toPrintArray = actionReplacedList.toArray(toPrintArray);

		// System.out.println(Arrays.deepToString(toPrintArray)); // TODO afficher en
		// colonne
		System.out.println();
		print2DArray(toPrintArray);
		System.out.println();
		drawHtmlTable(null, toPrintArray);
	}

	private static String[] replaceRefByAction(int[] indexed, Map<Integer, Action> corresp, boolean displayEmpty) {
		String[] res = new String[indexed.length];
		for (int counter = 0; counter < indexed.length; counter++) {
			if (indexed[counter] > 0) {
				Action currentAction = corresp.get(indexed[counter]);
				res[counter] = currentAction.getName();
			} else if (displayEmpty && indexed[counter] == 0) {
				res[counter] = " (Empty) ";
			} else {
				res[counter] = "         ";
			}
		}

		return res;
	}

	private static void addIntToArray(int toAdd, int[] dest, int start, int occur) {
		for (int i = 0; i < occur; i++) {
			dest[start + i] = toAdd;
		}
	}

	private static int searchFreeCell(List<int[]> list, int time) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i)[time] == -1) {
				return i;
			}
		}
		return -1;
	}

	private static void print2DArray(Object[][] mat) {

		for (Object[] row : mat) {
			System.out.println(Arrays.toString(row));
		}

	}

	private static void drawHtmlTable(String[] colNames, Object[][] toPrintData) {
		if (colNames == null) {
			colNames = new String[] {};
		}
		final String[] cName = colNames;

		/*
		 * Runnable r = new Runnable() {
		 * 
		 * @Override public void run() { htmlTableDrawer(cName, toPrintData); } };
		 * SwingUtilities.invokeLater(r);
		 */
		htmlTableDrawer(cName, toPrintData);

	}

	private static void htmlTableDrawer(String[] colNames, Object[][] toPrintData) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><table border=1>");

		sb.append("<tr>");
		for (String columnName : colNames) {
			sb.append("<th>");
			sb.append(columnName);
			sb.append("</th>");
		}
		sb.append("</tr>");
		for (Object[] row : toPrintData) {
			sb.append("<tr>");
			for (int i = 0; i < row.length; i++) { // print data
				// check if the nexts cells are == to row[i]
				int counterEquals = 1;
				while (i+counterEquals<row.length && row[i + counterEquals].equals(row[i])) {
					counterEquals++;
				}
				if (counterEquals > 1) {
					sb.append("<td colspan="+counterEquals+">");
					sb.append(row[i]);
					sb.append("</td>");
					i = i+counterEquals-1;
				} else {
					sb.append("<td>");
					sb.append(row[i]);
					sb.append("</td>");
				}

			}

			sb.append("</tr>");
		}
		sb.append("</table>");
		JLabel html = new JLabel(sb.toString());

//		JOptionPane.showMessageDialog(null, html);
	}

}
