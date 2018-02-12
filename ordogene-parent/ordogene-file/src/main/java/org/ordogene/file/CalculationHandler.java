package org.ordogene.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

public class CalculationHandler {

	List<Calculation> getCallculations(String username) {
		List<Calculation> res = new ArrayList<>();
		if (username == null || username.equals("")) {
			return res;
		}
		Path userPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username);
		try (DirectoryStream<Path> userPathStream = Files.newDirectoryStream(userPath, p -> Files.isDirectory(p))) {
			
			userPathStream.forEach(p -> {
				Calculation currenCalculation = new Calculation();
				currenCalculation.setName(p.getFileName().toString());
				res.add(currenCalculation);
			});

		} catch (IOException e) {
			System.err.println("Error while browsing the path " + userPath.toString());
			e.printStackTrace();
		}

		return res;
	}

	boolean startCalculation(String username, String calculationName) {
		if (username == null || username.equals("")) {
			return false;
		}

		try {
			Files.createDirectories(Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username
					+ File.separatorChar + calculationName));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// do stuff
		return true;
	}

	public static void startJar() {
		List<String> cmd = new ArrayList<>();
		String jarPath = Const.getConst().get("JarAlgorithmPath");
		Process launchedJarProcess;
		if (jarPath == null) {
			System.err.println("Error : The Algorithm path (in .jar) is not well defined in config.json");
		}
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(jarPath);
		try {
			ProcessBuilder b = new ProcessBuilder(cmd);
			launchedJarProcess = b.start();

			// display the Process :
			InputStreamReader isreader = new InputStreamReader(launchedJarProcess.getInputStream());
			BufferedReader buff = new BufferedReader(isreader);
			String processLine;
			while ((processLine = buff.readLine()) != null) {
				System.out.print(processLine);
			}

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
