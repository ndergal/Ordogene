package org.ordogene.api;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.algorithme.Model;
import org.ordogene.algorithme.jenetics.ScheduleBuilder;
import org.ordogene.algorithme.master.ThreadHandler;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleBuilderTest {
	
//	@Ignore
	@Test
	public void testScheduleBuilder() throws Exception {
		URL urlTestFile = ScheduleBuilderTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "tiny_strategy_game.json");
		byte[] contentFile = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContent = new String(contentFile);
		
		ScheduleBuilder sb = new ScheduleBuilder(new ThreadHandler(), Model.createModel((JSONModel) Parser.parseJsonFile(jsonContent, JSONModel.class)));
		sb.run();
	}
}
