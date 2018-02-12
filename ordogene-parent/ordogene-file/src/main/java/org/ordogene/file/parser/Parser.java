package org.ordogene.file.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.UnmarshalException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parser {

	public static Validable parseJsonFile(Path jsonPath, Class<? extends Validable> classe)
			throws IOException, JsonParseException, JsonMappingException, InstantiationException,
			IllegalAccessException, UnmarshalException {
		byte[] jsonData = Files.readAllBytes(jsonPath);
		ObjectMapper objectMapper = new ObjectMapper();
		Validable instance = classe.newInstance();
		instance = objectMapper.readValue(jsonData, classe);
		if (!instance.isValid()) {
			throw new UnmarshalException("Missing fields in the JSON");
		}
		return instance;
	}

	public static Validable parseJsonFile(String jsonString, Class<? extends Validable> classe)
			throws IOException, JsonParseException, JsonMappingException, InstantiationException,
			IllegalAccessException, UnmarshalException {
		byte[] jsonData = jsonString.getBytes();
		ObjectMapper objectMapper = new ObjectMapper();
		Validable instance = classe.newInstance();
		instance = objectMapper.readValue(jsonData, classe);
		if (!instance.isValid()) {
			throw new UnmarshalException("Missing fields in the JSON");
		}
		return instance;
	}

}
