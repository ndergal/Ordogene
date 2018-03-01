package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculationManager {
	private final static Logger log = LoggerFactory.getLogger(CalculationManager.class);

	public List<Calculation> getCalculations(String username) {
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
				res.add(currenCalculation);
			});

		} catch (IOException e) {
			log.error("Error while browsing the path " + userPath.toString());
			e.printStackTrace();
		}

		return res;
	}
	
	// TODO createDirectories
	// new File("/path/directory").mkdirs();

	public boolean removeCalculation(String username, int calculationID, String calculationName) {
		if (username == null || username.equals("")) {
			return false;
		}

		File todelete = new File(Const.getConst().get("ApplicationPath") + File.separatorChar + username
				+ File.separatorChar + calculationID + "_" + calculationName);
		if(!todelete.exists()) {
			return false;
		}
		try(Stream<Path> paths = Files.walk(todelete.toPath())) {
		    paths.sorted(Comparator.reverseOrder())
		    .map(Path::toFile)
		    .forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

}
