package org.ordogene.api;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordogene.file.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	 @Before
	 public void setUpStreams() {
	 System.setOut(new PrintStream(outContent));
	 System.setErr(new PrintStream(errContent));
	 }
	
	 @After
	 public void restoreStreams() {
	 System.setOut(System.out);
	 System.setErr(System.err);
	 }

	@Test
	public void mainMultipleFails() throws Exception {

		String[] args2 = { "--config=./src/test/resources/ordogene.conf.json", "--port=49155" };

		Application.main(args2);
		assertTrue(Const.getConst() != null);
		assertTrue(Const.getConst().size() > 0);

		String[] args3 = { "--config=./src/test/resources/ordogene.conf.json", "--port=655356" };

		String[] args4 = { "--config=./src/test/resources/ordogene.conf.json", "--port=-56" };

		Application.main(args4);

		assertTrue(outContent.toString().contains("Ordogene Server : The port parameter must be a positive number below 65535."));
		outContent.reset();
		Application.main(args3);
		assertTrue(outContent.toString().contains("Ordogene Server : The port parameter must be a positive number below 65535."));
		outContent.reset();
		String[] args = {};
		System.out.println(outContent.toString());
		Application.main(args);
		assertTrue(outContent.toString().contains("Missing argument --config=<configuration_file_location>"));

	}

}
