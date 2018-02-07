package org.ordogene.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CalculationHandler {

	boolean startCalculation(String username, String calculationName) {
		if (username == null || username.equals("")) {
			return false;
		}

		try {
			Files.createDirectories(Paths.get(Const.getConst().get("ApplicationPath") + File.separatorChar + username+ File.separatorChar + calculationName));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// do stuff
		return true;
	}
	
	//START EXECUTER JAR AND EXIT MONITOR i.e. this APPLICATION
	public static void startExecutorJar(){		
		try{
			List<String> command = new ArrayList<>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add(Const.getConst().get("JarAlgorithmPath"));
		    
		    ProcessBuilder builder = new ProcessBuilder(command);		    
		    Process process = builder.start();		
		    
		    System.exit(0);
		    
		    /*InputStream is = process.getInputStream();		    
		    // any error message?
		    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");      
	        // any output?
		    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
	        
	        // kick them off
	        errorGobbler.start();
	        outputGobbler.start();
	        
	        // any error???
	       int exitVal=-1; 
	       try {
	    	    exitVal = process.waitFor();
			} catch (Throwable t) {
				System.out.println("Executer, In catch");
				t.printStackTrace();
			}*/
    		
		}catch(Exception e){
			e.printStackTrace();			
		}finally{
			
		}
		
		System.out.println("Exec FINISHED");
		
	}
	public String readExecPId(String fFilePath) {
		StringBuilder executorPId = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(fFilePath+"\\"+"execPId.txt"));
			while (scanner.hasNextLine()) {
				executorPId.append(scanner.nextLine());
			}
		}catch(IOException ie){
			System.out.println("MonitorKillAndStartExec.readExecPId() could not find : " + fFilePath+"\\"+"execPId.txt");
		}finally {
			if(scanner!=null)
				scanner.close();
		}
		return executorPId.toString();
	}
	
	private static ArrayList<String> killExec(String processStr) throws IOException {
		String outStr = "";
		ArrayList<String> processOutList = new ArrayList<String>();
		int i = -1;
		
		Process p = Runtime.getRuntime().exec(processStr);
		
		// OutputStream out = p.getOutputStream();
		InputStream in = p.getInputStream();
		
		x11: while ((i = in.read()) != -1) {
			if ((char) i == '\n') {
				processOutList.add((outStr));
				outStr = "";
				continue x11;
			}
			outStr += (char) i;
		}
		
		return processOutList;
	}

}
