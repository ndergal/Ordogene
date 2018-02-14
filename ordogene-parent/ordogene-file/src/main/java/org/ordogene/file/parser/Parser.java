package org.ordogene.file.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.UnmarshalException;

import org.ordogene.file.JSONModel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parser {

	private Parser() {
		// TODO Auto-generated constructor stub
	}

	public static Validable parseJsonFile(Path jsonPath, Class<? extends Validable> classe)
			throws IOException, JsonParseException, JsonMappingException, IllegalAccessException, UnmarshalException {
		byte[] jsonData = Files.readAllBytes(jsonPath);
		return parseJsonFile(jsonData, classe);
	}

	public static Validable parseJsonFile(String jsonString, Class<? extends Validable> classe)
			throws JsonParseException, JsonMappingException, IOException, UnmarshalException {
		byte[] jsonData = jsonString.getBytes();
		return parseJsonFile(jsonData, classe);
	}

	public static Validable parseJsonFile(byte[] jsonData, Class<? extends Validable> classe)
			throws JsonParseException, JsonMappingException, IOException, UnmarshalException {
		ObjectMapper objectMapper = new ObjectMapper();
		Validable instance = objectMapper.readValue(jsonData, classe);
		if (!instance.isValid()) {
			throw new UnmarshalException("Missing fields in the JSON");
		}
		return instance;
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException,
			InstantiationException, IllegalAccessException, UnmarshalException {
		System.out.println(Parser.parseJsonFile(Paths.get(
				"/home/ordogene/git/ordogene/ordogene-parent/ordogene-file/src/main/java/org/ordogene/file/testJson/fitness1.json"),
				JSONModel.class));
	}

}
