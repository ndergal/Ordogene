package org.ordogene.api;

import org.ordogene.algorithme.master.Master;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ordogene.api.utils.CustomArgsParser;
import org.ordogene.file.FileService;
import org.ordogene.file.utils.Const;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
			System.exit(1);
			return;
		}

		String configFilePath = cmd.getOptionValue("config");

		System.out.println(configFilePath);
		if (Const.loadConfig(configFilePath))
			SpringApplication.run(Application.class, args);
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