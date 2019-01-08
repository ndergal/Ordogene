package org.ordogene.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordogene.file.utils.Const;
import org.springframework.boot.context.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {

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

	/*
	 * @Test public void out() { System.out.print("hello"); assertTrue(
	 * outContent.toString().startsWith("Missing required option: conf")); }
	 * 
	 * @Test public void err() { System.err.print("hello again");
	 * assertEquals("hello again", errContent.toString()); }
	 */
	@Test
	public void mainMultipleFails() throws Exception {
		// System.out.println("All is in one test to save Spring launch time...");

		String[] args2 = { "-conf", "./src/test/resources/ordogene.conf.json", "-port", "49155" };
		Application.main(args2);
		assertTrue(Const.getConst() != null);
		assertTrue(Const.getConst().size() > 0);

		String[] args3 = { "-conf", "./src/test/resources/ordogene.conf.json", "-port", "655356" };

		String[] args4 = { "-conf", "./src/test/resources/ordogene.conf.json", "-port", "-6743" };

		Application.main(args4);

		assertTrue(errContent.toString()
				.startsWith("Ordogene Server : The port parameter must be a positive number below 65535."));
 
		Application.main(args3);
		assertTrue(errContent.toString()
				.startsWith("Ordogene Server : The port parameter must be a positive number below 65535."));

		String[] args = {};
		System.out.println(outContent.toString());
		Application.main(args);
		assertTrue(outContent.toString().contains("Missing required option: conf"));
		

	}

}
