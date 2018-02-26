package org.ordogene.algorithme.jenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.jenetics.Phenotype;

public class Drawer {

	static String buildHtmlTableHeader(String prefix, Object[][] content) {
		int nbCol = 0;
		for (Object[] aga : content) {
			if (aga.length > nbCol)
				nbCol = aga.length;
		}
		StringBuilder sb = new StringBuilder();
		// header
		sb.append("<tr>");
		for (int j = 0; j < nbCol; j++) {
			sb.append("<th>").append(prefix).append(" ").append(j).append("</th>");
		}

		sb.append("</tr>");
		return sb.toString();
	}

	static ActionGene[][] buildStringActionMatrix(Phenotype<ActionGene, Long> individu) { // maxSize = model.getslot()
		Schedule s = (Schedule) individu.getGenotype().getChromosome();
		List<ActionGene> actions = s.stream().collect(Collectors.toList());
		int maxSize = s.getModel().getSlots();
		HashMap<Integer, ActionGene> indexedActions = new HashMap<>();
		int nextIndex = 1; // index start from 1
		List<int[]> indexedList = new ArrayList<>(); // list = (Y axis on the paper) int[] = X axis
		int realMaxSize = 0;
		for (ActionGene action : actions) {
			int startTime = action.getStartTime();
			int line = searchFreeCell(indexedList, startTime); // Find the next index : the next free int[] at (time).
																// <-
			// current list index
			if (line == -1) {
				// no free space at (time) : create a new int[]
				int[] newLine = new int[maxSize];
				Arrays.fill(newLine, -1);
				indexedList.add(newLine);
				line = indexedList.size() - 1;
			}

			indexedActions.put(nextIndex, action);
			addIntToArray(nextIndex, indexedList.get(line), startTime, action.getAllele().getTime());
			if (startTime + action.getAllele().getTime() > realMaxSize) {
				realMaxSize = startTime + action.getAllele().getTime();
			}
			nextIndex++;

			int[][] toPrintArray2 = new int[indexedList.size()][];
			toPrintArray2 = indexedList.toArray(toPrintArray2);
		}

		// remove all "-1" useless cells

		final int realMaxSizef = realMaxSize;
		//
		// for (int i = 0; i < indexedList.size(); i++) {
		// int[] intArray = indexedList.get(i);
		// System.out.println("intArray before = " + intArray.length);
		// int[] truncatedArray = new int[realMaxSizef];
		// System.arraycopy(intArray, 0, truncatedArray, 0, realMaxSizef);
		// indexedList.set(i, truncatedArray);
		// System.out.println("truncatedArray after = " + truncatedArray.length);
		// }
		List<ActionGene[]> actionReplacedList = new ArrayList<>();
		indexedList.forEach(intArray -> {
			int[] truncatedArray = new int[realMaxSizef];
			System.arraycopy(intArray, 0, truncatedArray, 0, realMaxSizef);

			actionReplacedList.add(replaceRefByAction(truncatedArray, indexedActions));

		});

		ActionGene[][] res = new ActionGene[actionReplacedList.size()][];
		res = actionReplacedList.toArray(res);

		System.out.println();
		print2DArray(res);
		System.out.println();

		return res;

	}

	private static ActionGene[] replaceRefByAction(int[] indexed, Map<Integer, ActionGene> corresp) {
		ActionGene[] res = new ActionGene[indexed.length];
		for (int counter = 0; counter < indexed.length; counter++) {
			if (indexed[counter] > 0) {
				res[counter] = corresp.get(indexed[counter]);

			} else {
				res[counter] = null;
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

	static String htmlTableBuilder(String title, String header, double cellSize, String unit, ActionGene[][] toPrintData,
			boolean display) {
		// content
		StringBuilder sbTr = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><h2>").append(title).append("</h2><table border=1>");
		int nbColsMax = 0;
		for (ActionGene[] row : toPrintData) {
			sbTr.append("<tr>");
			for (int i = 0; i < row.length; i++) { // write data
				// check if the nexts cells are == to row[i]
				int currentActionDuration = 1;
				int nbCols = 0;
				if (row[i] != null && row[i].getAllele() != null) {
					currentActionDuration = row[i].getAllele().getTime();
				}
				nbCols += currentActionDuration;
				if (currentActionDuration > 1) {
					sbTr.append("<td style='width:" + cellSize * currentActionDuration + unit + "' colspan="
							+ currentActionDuration + ">");
					if (row[i] != null) {
						sbTr.append(row[i].getAllele().getName());
					}
					sbTr.append("</td>");
					i = i + currentActionDuration - 1;
				} else {
					sbTr.append("<td style='width:" + cellSize + unit + "'>");
					if (row[i] != null) {
						sbTr.append(row[i].getAllele().getName());
					}
					sbTr.append("</td>");
				}
				if (nbCols > nbColsMax) {
					nbColsMax = nbCols;
				}
			}
			sbTr.append("</tr>");
		}
		sbTr.append("</table>");

		sb.append(header);

		String res = sb.toString() + sbTr.toString();
		if (display) {
			System.out.println(res);
			JOptionPane.showMessageDialog(null, new JLabel(res));
		}
		return res;

		// JOptionPane.showMessageDialog(null, html);
	}

}
