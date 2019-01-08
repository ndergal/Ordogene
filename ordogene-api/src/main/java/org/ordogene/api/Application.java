package org.ordogene.api;

import org.ordogene.algorithme.master.Master;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;

@SpringBootApplication

public class Application {

	private final static Master algoMaster = new Master();

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {

		SimpleCommandLinePropertySource ps = new SimpleCommandLinePropertySource(args);
		if (!ps.containsProperty("config")) {
			log.error("Missing argument --config=<configuration_file_location>");
			return;
		}
		String configFilePath = (String) ps.getProperty("config");
		if (configFilePath == null || configFilePath.isEmpty()) {
			log.error("<configuration_file_location> parameter is empty");
			return;
		}

		String optPort = null;
		boolean hasPort = true;
		if (ps.containsProperty("port")) {
			optPort = (String) ps.getProperty("port");
		}

		if (optPort == null || optPort.isEmpty()) {
			hasPort = false;
		}
 		int portParameterInt = -1;
		if (hasPort) {
			try {
				portParameterInt = Integer.parseInt(optPort);
				if (portParameterInt > 65535 || portParameterInt < 1) {
					log.error("Ordogene Server : The port parameter must be a positive number below 65535.");
					return;
				}
			} catch (NumberFormatException e) {
				log.error("Ordogene Server : The port parameter must be a positive number below 65535.");
				return;
			}
		}
		if (Const.loadConfig(configFilePath)) {
			if (!hasPort) {
				log.info("Launch Ordogene server on default port.");
				SpringApplication.run(Application.class, args);
				// --server.port=8081
			} else {
				String[] newArgs = new String[args.length + 1];
				log.info("Launch Ordogene server on port {}.", portParameterInt);
				System.arraycopy(args, 0, newArgs, 0, args.length);
				newArgs[args.length] = "--server.port=" + portParameterInt;
				SpringApplication.run(Application.class, newArgs);

			}
		}
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			ctx.getBeanDefinitionNames();
		};
	}

	@Bean
	public Master buildMasterAlgo() {
		return algoMaster;
	}
}