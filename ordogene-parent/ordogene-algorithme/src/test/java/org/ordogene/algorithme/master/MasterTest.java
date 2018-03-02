package org.ordogene.algorithme.master;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.UnmarshalException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterTest {
	
	private final static Logger log = LoggerFactory.getLogger(MasterTest.class);
	
	String JSONmodel = "{\n" + 
			"    \"name\" : \"small_strategy_game.json\",\n" + 
			"    \"slots\" : 2,\n" + 
			"    \"exec_time\" : 5,\n" + 
			"    \"environment\" : [\n" + 
			"	{\"name\" : \"Nexus\", \"quantity\" : 1},\n" + 
			"	{\"name\" : \"Peon\", \"quantity\" : 1},\n" + 
			"	{\"name\" : \"Gold\", \"quantity\" : 34},\n" + 
			"	{\"name\" : \"Barrack\", \"quantity\" : 0},\n" + 
			"	{\"name\" : \"Knight\", \"quantity\" : 0}\n" + 
			"    ],\n" + 
			"    \"actions\" : [\n" + 
			"        {\"name\" : \"Build_nexus\", \"time\" : 20,\n" + 
			"         \"input\" : [\n" + 
			"    	     { \"name\" : \"Gold\", \"quantity\" : 20, \"relation\" : \"c\" },\n" + 
			"	     { \"name\" : \"Peon\", \"quantity\" : 1, \"relation\" : \"p\" }\n" + 
			"	 ],\n" + 
			"	 \"output\" : [\n" + 
			"	     {\"name\" : \"Nexus\", \"quantity\" : 1}\n" + 
			"	 ]\n" + 
			"	},\n" + 
			"	{\"name\" : \"Build_peon\", \"time\" : 2,\n" + 
			"         \"input\" : [\n" + 
			"    	     { \"name\" : \"Nexus\", \"quantity\" : 1, \"relation\" : \"p\" },\n" + 
			"	     { \"name\" : \"Gold\", \"quantity\" : 5, \"relation\" : \"c\" }\n" + 
			"	 ],\n" + 
			"	 \"output\" : [\n" + 
			"	     {\"name\" : \"Peon\", \"quantity\" : 1}\n" + 
			"	 ]\n" + 
			"	},\n" + 
			"	{\"name\" : \"Dig_gold\", \"time\" : 1,\n" + 
			"         \"input\" : [\n" + 
			"    	     { \"name\" : \"Nexus\", \"quantity\" : 1, \"relation\" : \"r\" },\n" + 
			"	     { \"name\" : \"Peon\", \"quantity\" : 1, \"relation\" : \"p\" }\n" + 
			"	 ],\n" + 
			"	 \"output\" : [\n" + 
			"	     {\"name\" : \"Gold\", \"quantity\" : 1}\n" + 
			"	 ]\n" + 
			"	},\n" + 
			"	{\"name\" : \"Build_barrack\", \"time\" : 10,\n" + 
			"         \"input\" : [\n" + 
			"    	     { \"name\" : \"Nexus\", \"quantity\" : 1, \"relation\" : \"r\" },\n" + 
			"	     { \"name\" : \"Peon\", \"quantity\" : 1, \"relation\" : \"p\" },\n" + 
			"	     { \"name\" : \"Gold\", \"quantity\" : 15, \"relation\" : \"c\" }\n" + 
			"	 ],\n" + 
			"	 \"output\" : [\n" + 
			"	     {\"name\" : \"Barrack\", \"quantity\" : 1}\n" + 
			"	 ]\n" + 
			"	},\n" + 
			"	{\"name\" : \"Build_barrack\", \"time\" : 10,\n" + 
			"         \"input\" : [\n" + 
			"    	     { \"name\" : \"Nexus\", \"quantity\" : 1, \"relation\" : \"r\" },\n" + 
			"	     { \"name\" : \"Peon\", \"quantity\" : 1, \"relation\" : \"p\" },\n" + 
			"	     { \"name\" : \"Gold\", \"quantity\" : 15, \"relation\" : \"c\" }\n" + 
			"	 ],\n" + 
			"	 \"output\" : [\n" + 
			"	     {\"name\" : \"Barrack\", \"quantity\" : 1}\n" + 
			"	 ]\n" + 
			"	},\n" + 
			"	{\"name\" : \"Train_knight\", \"time\" : 10,\n" + 
			"         \"input\" : [\n" + 
			"	     { \"name\" : \"Barrack\", \"quantity\" : 1, \"relation\" : \"p\" },\n" + 
			"	     { \"name\" : \"Gold\", \"quantity\" : 10, \"relation\" : \"c\" },\n" + 
			"	     { \"name\" : \"Peon\", \"quantity\" : 1, \"relation\" : \"c\" }\n" + 
			"	 ],\n" + 
			"	 \"output\" : [\n" + 
			"	     {\"name\" : \"Knight\", \"quantity\" : 1}\n" + 
			"	 ]\n" + 
			"	}\n" + 
			"    ],\n" + 
			"    \"fitness\" : {\n" + 
			"	\"type\" : \"max\",\n" + 
			"	\"operands\" : [\n" + 
			"	    {\"name\" : \"Gold\", \"coef\" : 1},\n" + 
			"	    {\"name\" : \"Peon\", \"coef\" : 1},\n" + 
			"	    {\"name\" : \"Knight\", \"coef\" : 20}\n" + 
			"	]\n" + 
			"    }\n" + 
			"}\n";
	
	String userIdTest = "tester";

	@Before
	public void init() throws URISyntaxException {
		String configFileLocation = MasterTest.class.getClassLoader().getResource("ordogene.conf.json").toURI()
				.toString();
		if (configFileLocation.startsWith("file:")) {
			configFileLocation = configFileLocation.substring(5);

		}
		Const.loadConfig(configFileLocation);
		// create user tester
		try {
			Files.createDirectories(Paths.get(Const.getConst().get("ApplicationPath") + File.separator + "tester"));
		} catch (IOException e) {
			log.error("Error while creating the directory " + Const.getConst().get("ApplicationPath")
					+ File.separator + "tester");
			e.printStackTrace();
		}

	}

	@Test
	public void execCalculationDummy() throws IOException, URISyntaxException, InstantiationException,
			IllegalAccessException, InterruptedException, UnmarshalException {
		URL urlTestFile = MasterTest.class.getClassLoader()
				.getResource("OrdogeneCalculationExamples" + File.separator + "short_path_10.json");
		byte[] contentFileTest = Files.readAllBytes(Paths.get(urlTestFile.toURI()));
		String jsonContentPost = new String(contentFileTest);
		int res = new Master().compute("tester", jsonContentPost);
		assertNotEquals(-2, res);
	}
	
	public void MasterTest_defaultConstructor() {
		new Master();
	}

	@Test
	public void MasterTest_argumentConstructor() {
		new Master(12);
	}

	@Test(expected=IllegalArgumentException.class)
	public void MasterTest_argumentConstructor_negative_value() {
		new Master(-3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void MasterTest_argumentConstructor_zero_value() {
		new Master(0);
	}
	
	@Test
	public void computeTest_null_idUser() {
		Master m = new Master();
		Assertions.assertThatThrownBy(() -> {
			m.compute(null, JSONmodel);
		}).isInstanceOf(NullPointerException.class);
	}
	
	@Test
	public void computeTest_null_jsonModel() {
		Master m = new Master();
		Assertions.assertThatThrownBy(() -> {
			m.compute(userIdTest, null);
		}).isInstanceOf(NullPointerException.class);
	}
	
	@Test
	public void computeTest_OK() throws Exception {
		Master m = new Master();
		
		Assertions.assertThat(m.compute(userIdTest, JSONmodel)).isNotNull();
	}
	
	@Test
	public void computeTest_serverFull() throws Exception {
		Master m = new Master(1);
		
		Assertions.assertThat(m.compute(userIdTest, JSONmodel)).isNotNull();
		Assertions.assertThat(m.compute(userIdTest, JSONmodel)).isNull();
	}
	
	@Test
	public void updateCalculationTest_OK_isRunning() throws Exception {
		Master m = new Master();
		
		int calculationId = m.compute(userIdTest, JSONmodel);
		
		Calculation toTest = new Calculation();
		toTest.setId(calculationId);
		toTest.setName("small_strategy_game.json");
		
		m.updateCalculation(toTest, userIdTest);
		
		assertNotEquals(0, toTest.getIterationNumber());
		assertNotEquals(0, toTest.getMaxIteration());
		assertNotEquals(0, toTest.getStartTimestamp());
		assertNotEquals(0, toTest.getFitnessSaved());
		assertTrue(toTest.isRunning());
	}
	
	@Test
	public void updateCalculationTest_OK_notRunning() throws Exception {
		Master m = new Master();
		
		int calculationId = m.compute(userIdTest, JSONmodel);
		
		//TODO change by something else?
		Thread.sleep(5000); //wait end of calculation
		
		Calculation toTest = new Calculation();
		toTest.setId(calculationId);
		toTest.setName("small_strategy_game.json");
		
		m.updateCalculation(toTest, userIdTest);
		
		assertNotEquals(0, toTest.getIterationNumber());
		assertNotEquals(0, toTest.getMaxIteration());
		assertNotEquals(0, toTest.getStartTimestamp());
		assertNotEquals(0, toTest.getFitnessSaved());
		assertFalse(toTest.isRunning());
	}
	
	@Test
	public void updateCalculationTest_KO_notExist() throws Exception {
		Master m = new Master();
		
		int calculationId = m.compute(userIdTest, JSONmodel);
		
		Calculation toTest = new Calculation();
		toTest.setId(calculationId + 1);
		toTest.setName("small_strategy_game.json");
		
		m.updateCalculation(toTest, userIdTest);
		
		assertEquals(0, toTest.getIterationNumber());
		assertEquals(0, toTest.getMaxIteration());
		assertEquals(0, toTest.getStartTimestamp());
		assertEquals(0, toTest.getFitnessSaved());
		assertFalse(toTest.isRunning());
	}
	
	@Test
	public void interruptCalculationTest_OK() throws Exception {
		Master m = new Master();
		
		int calculationId = m.compute(userIdTest, JSONmodel);
		
		assertTrue(m.interruptCalculation(calculationId));
	}
	
	@Test
	public void interruptCalculationTest_OK_notLaunch() throws Exception {
		Master m = new Master();
		
		assertFalse(m.interruptCalculation(12));
	}

}
