package org.ordogene.algorithme.master;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;

import org.ordogene.algorithme.master.Master.ThreadHandler;
import org.ordogene.file.utils.Const;

public class Dummy {

	
	public static void fakeCalculation(ThreadHandler th, String calculationName, String uid, int cid, int occur) throws InterruptedException, IOException {
		if (uid == null) {
			System.err.println("Pas de dossier ou écrire spécifié en argument!");
		}
		String location = Const.getConst().get("ApplicationPath");
		if (location == null) { // avoid problem with noconfig file : only for the dev
			location = "/home/ordogene/testProjectFiles/";
		}
		location = location + File.separator + uid + File.separator + cid + "_"+calculationName;
		if (!Files.exists(Paths.get(location))) {
			Files.createDirectories(Paths.get(location));
		}
		try {
			Files.createDirectories(Paths.get(location));
		} catch (IOException e) {
			System.err.println("Error while creating the directory " + location);
			e.printStackTrace();
		}

		Thread.sleep(900);
		try (FileOutputStream fos = new FileOutputStream(location + File.separator + "result" + occur + ".jpg")) {
			URL doge = new URL("https://quiteirregular.files.wordpress.com/2014/02/doge.png");
			ReadableByteChannel rbc = Channels.newChannel(doge.openStream());
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (FileAlreadyExistsException e) {
			System.err.println("already exists: " + e.getMessage());
		}
	}

	private static String imgAsBase64(URL imgUrl) throws IOException {
		BufferedInputStream in = new BufferedInputStream(imgUrl.openConnection().getInputStream());
		byte[] contents = new byte[1024];

		int bytesRead = 0;
		String strFileContents = null;
		while ((bytesRead = in.read(contents)) != -1) {
			strFileContents += new String(contents, 0, bytesRead);
		}
		return strFileContents;

	}

}
