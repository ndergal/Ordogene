package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

public class CalculationHandler {

	List<Calculation> getCalculations(String username) {
		List<Calculation> res = new ArrayList<>();
		if (username == null || username.equals("")) {
			return res;
		}
		Path userPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
		try (DirectoryStream<Path> userPathStream = Files.newDirectoryStream(userPath, p -> Files.isDirectory(p))) {

			userPathStream.forEach(p -> {
				Calculation currenCalculation = new Calculation();
				String directoryName = p.getFileName().toString();
				String[] idAndName = directoryName.split("_", 2);
				if (idAndName.length < 2) {
					return;
				}
				try {
					currenCalculation.setId(Integer.valueOf(idAndName[0]));
				} catch (NumberFormatException e) {
					return;
				}
				currenCalculation.setName(idAndName[1]);
				// currenCalculation.set
				res.add(currenCalculation);
			});

		} catch (IOException e) {
			System.err.println("Error while browsing the path " + userPath.toString());
			e.printStackTrace();
		}

		return res;
	}

	boolean removeCalculation(String username, int calculationID, String calculationName) {
		if (username == null || username.equals("")) {
			return false;
		}

		File todelete = new File(Const.getConst().get("ApplicationPath") + File.separatorChar + username
				+ File.separatorChar + calculationID + "_" + calculationName);
		if(!todelete.exists()) {
			return false;
		}
		try {
			FileUtils.deleteDirectory(todelete);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

}
