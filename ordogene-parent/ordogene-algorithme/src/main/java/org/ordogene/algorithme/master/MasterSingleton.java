package org.ordogene.algorithme.master;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.UnmarshalException;

import org.ordogene.algorithme.Model;
import org.ordogene.file.JSONModel;
import org.ordogene.file.parser.Parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MasterSingleton {

	private static final MasterSingleton SINGLETON;
	private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
	private final List<Thread> threadList = new ArrayList<>();

	static {
		SINGLETON = new MasterSingleton();
	}

	private MasterSingleton() {

	}

	public static MasterSingleton getInstance() {
		return SINGLETON;
	}

	public static String compute(Path modelPath) throws JsonParseException, JsonMappingException,
			InstantiationException, IllegalAccessException, UnmarshalException, IOException {
		JSONModel jmodel = (JSONModel) Parser.parseJsonFile(modelPath, JSONModel.class);
		Model model = Model.createModel(jmodel);

		// TODO
		return null;
	}
}
