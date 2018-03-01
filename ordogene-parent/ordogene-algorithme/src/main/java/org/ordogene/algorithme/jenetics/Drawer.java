package org.ordogene.algorithme.jenetics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.ordogene.algorithme.models.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.jenetics.Phenotype;

public class Drawer {
	private final static Logger log = LoggerFactory.getLogger(Drawer.class);

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
		List<ActionGene[]> actionReplacedList = new ArrayList<>();
		indexedList.forEach(intArray -> {
			int[] truncatedArray = new int[realMaxSizef];
			System.arraycopy(intArray, 0, truncatedArray, 0, realMaxSizef);

			actionReplacedList.add(replaceRefByAction(truncatedArray, indexedActions));

		});

		ActionGene[][] res = new ActionGene[actionReplacedList.size()][];
		res = actionReplacedList.toArray(res);

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

	private static Color randomLightColorGenerator() {
		float hue = (float) Math.random();
		int rgb = Color.HSBtoRGB(hue, 0.5f, 0.9f);
		return new Color(rgb);
	}

	static String htmlTableBuilder(String title, String header, double cellSize, String unit,
			ActionGene[][] toPrintData, boolean display) {
		Map<Action, Color> colorAction = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><header>");
		sb.append("<style>");
		sb.append("html, body {\n" + 
				"  font-family: \"Lucida Console\", Monaco, monospace;\n" + 
				"}\n" + 
				"table {\n" + 
				"  border-collapse: collapse;\n" + 
				"}\n" + 
				"thead {\n" + 
				"  border-bottom: 1px solid black;\n" + 
				"}\n" + 
				"td, th {\n" + 
				"  padding: 8px 12px;\n" + 
				"}\n" + 
				"th {\n" + 
				"  text-align: left;\n" + 
				"}\n" + 
				"td {\n" + 
				"  #border: 1px solid #AAAAAA;\n" + 
				"}\n" + 
				"td:hover {\n" + 
				"  box-shadow: 0 0 10px 5px rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);\n" + 
				"}");
		sb.append("</style>");
		sb.append("</header><body>");
		sb.append("<h2>").append(title).append("</h2>");
		sb.append("<table>");
		sb.append("<thead>").append(header).append("</thead>");
		sb.append("<tbody>");
		for (ActionGene[] row : toPrintData) {
			sb.append("<tr>");
			for (int i = 0; i < row.length; i++) {
				//COLOR
				Color currentActionColor = null;
				if (row[i] != null) {
					currentActionColor = colorAction.get(row[i].getAllele());
					if (currentActionColor == null) {
						currentActionColor = randomLightColorGenerator();
						colorAction.put(row[i].getAllele(), currentActionColor);
					}
				} else {
					currentActionColor = Color.WHITE;
				}
				String htmlRgb = "rgb(" + currentActionColor.getRed() + ',' + currentActionColor.getGreen() + ','
						+ currentActionColor.getBlue() + ')';
				//END COLOR
				
				//TD
				sb.append("<td style='");
				sb.append("background-color: ").append(htmlRgb).append(";");
				//sb.append("width:").append(cellSize).append(unit).append(";");
				sb.append("'");
				//TD LENGTH
				int currentActionDuration = 1;
				if (row[i] != null/* && row[i].getAllele() != null*/) {
					currentActionDuration = row[i].getAllele().getTime();
				}
				if (currentActionDuration > 1) {
					sb.append(" colspan=" + currentActionDuration);
				}
				//END TD LENGTH
				sb.append(">");
				if (row[i] != null) {
					sb.append(row[i].getAllele().getName());
				}
				sb.append("</td>");
				//END TD
				
				//GOTO END ACTION
				if(currentActionDuration > 1) {
					i = i + currentActionDuration - 1;
				}
			}
			sb.append("</tr>");
		}
		sb.append("</tbody></table>");

		String res = sb.toString();
		if (display) {
			log.info(res);
			JOptionPane.showMessageDialog(null, new JLabel(res));
		}
		return res;
	}

}