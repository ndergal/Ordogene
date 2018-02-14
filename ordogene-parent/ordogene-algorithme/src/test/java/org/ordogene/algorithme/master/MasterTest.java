package org.ordogene.algorithme.master;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.UnmarshalException;

import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Const;

public class MasterTest {

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
			System.err.println("Error while creating the directory " + Const.getConst().get("ApplicationPath")
					+ File.separator + "tester");
			e.printStackTrace();
		}

	}


}
