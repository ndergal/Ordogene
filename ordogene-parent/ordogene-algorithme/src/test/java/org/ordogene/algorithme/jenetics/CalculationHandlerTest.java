package org.ordogene.algorithme.jenetics;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.Const;

import io.jenetics.util.RandomRegistry;

@RunWith(MockitoJUnitRunner.class)
public class CalculationHandlerTest {

	private static final String userId = "tester";

	@Before
	public void init() throws URISyntaxException {
		String configFileLocation = CalculationHandlerTest.class.getClassLoader().getResource("ordogene.conf.json").toURI()
				.toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
	}

	// @Ignore
	@Test
	public void testCalculationHandler() throws Exception {
		RandomRegistry.setRandom(new Random(0));
		URL urlTestFile = CalculationHandlerTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_10.json");
		byte[] contentFile = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContent = new String(contentFile);

		CalculationHandler sb = new CalculationHandler(new ThreadHandler(),
				Model.createModel((JSONModel) Parser.parseJsonFile(jsonContent, JSONModel.class)), userId, 0);
		sb.launchCalculation();
	}
}
