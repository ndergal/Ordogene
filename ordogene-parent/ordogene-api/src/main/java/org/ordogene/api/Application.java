package org.ordogene.api;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ordogene.algorithme.master.Master;
import org.ordogene.api.utils.CustomArgsParser;
import org.ordogene.file.FileService;
import org.ordogene.file.utils.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class Application {

	private final static Master algoMaster;
	static {
		algoMaster = new Master();
	}

	public static void main(String[] args) {

		Options options = new Options();

		Option config = new Option("conf", "config", true, "input file path");
		config.setRequired(true);
		options.addOption(config);

		Option port = new Option("port", "port", true, "port to launch the server");
		port.setRequired(false);
		options.addOption(port);

		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		boolean ignoreUnknowArg = true;
		try {
			cmd = new CustomArgsParser(ignoreUnknowArg).parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			String jarName = new java.io.File(
					Application.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
			formatter.printHelp("java -jar " + jarName, options);
			return;
		}

		String configFilePath = cmd.getOptionValue("config");
		String optionalPort = cmd.getOptionValue("port");
		boolean hasPort = (optionalPort != null);
		int portParameterInt = -1;
		if (hasPort) {
			try {
				portParameterInt = Integer.parseInt(optionalPort);
				if (portParameterInt > 65535 || portParameterInt < 1) {
					System.err.println("Ordogene Server : The port parameter must be a positive number below 65535.");
					return;
				}
			} catch (NumberFormatException e) {
				System.err.println("Ordogene Server : The port parameter must be a positive number below 65535.");
				return;
			}
		}
		if (Const.loadConfig(configFilePath)) {
			if (!hasPort) {
				System.out.println("Launch Ordogene server on default port.");
				SpringApplication.run(Application.class, args);
				// --server.port=8081
			} else {
				String[] newArgs = new String[args.length+1];
				System.out.println("Launch Ordogene server on port "+portParameterInt+".");
				System.arraycopy(args, 0, newArgs, 0, args.length);
				newArgs[args.length] = "--server.port="+portParameterInt;
				SpringApplication.run(Application.class, newArgs);

			}
		}
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

	@Bean
	public FileService buildFileService() {
		return new FileService();
	}

	@Bean
	public Master buildMasterAlgo() {
		return algoMaster;
	}

	/*
	 * @Bean public CalculationHandler getCalculationHandler() { return
	 * calculationHandler; }
	 */
}