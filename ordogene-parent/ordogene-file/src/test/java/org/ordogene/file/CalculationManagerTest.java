package org.ordogene.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.ordogene.file.utils.Calculation;
import org.ordogene.file.utils.Const;

/**
 */
public class CalculationManagerTest {

	public class CopyFileVisitor extends SimpleFileVisitor<Path> {
		private final Path targetPath;
		private Path sourcePath = null;

		public CopyFileVisitor(Path targetPath) {
			this.targetPath = targetPath;
		}

		@Override
		public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
			if (sourcePath == null) {
				sourcePath = dir;
			} else {
				Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
			Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
			return FileVisitResult.CONTINUE;
		}
	}

	private final String testerName = "tester";

	@Before
	public void init() throws URISyntaxException {
		Const.loadConfig("./src/test/resources/ordogene.conf.json");
		// copy fake calculation folder :
		Path sourcePath = (Paths
				.get(CalculationManagerTest.class.getClassLoader().getResource("-624472280_dummy-calc-test").toURI()));
		Path destinationPath = Paths.get(Const.getConst().get("ApplicationPath") + File.separator + testerName
				+ File.separator + "-624472280_dummy-calc-test");

		System.out.println("Move " + sourcePath.toAbsolutePath().toString() + " in "
				+ destinationPath.toAbsolutePath().toString());
		try {
			if (Files.exists(destinationPath)) {
				FileUtils.deleteDirectory(destinationPath.toFile());
				Files.deleteIfExists(destinationPath);
			}
			Files.createDirectories(destinationPath);

			Files.walkFileTree(sourcePath, new CopyFileVisitor(destinationPath));
		} catch (IOException e) {
			// problem !?
			e.printStackTrace();
		}

	}

	@Test
	public void calculationGet() {
		CalculationManager ch = new CalculationManager();
		List<Calculation> calcs = ch.getCalculations(testerName);
		boolean b = false;
		for (Calculation c : calcs) {
			if (c.getId() == -624472280) {
				assertTrue(c.getId() == -624472280);
				b=true;
				return;
			}
		}
		assertTrue(b);

	}
}
