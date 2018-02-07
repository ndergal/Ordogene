package org.ordogene.file.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parser {

	public static <R> R parseJsonFile(Path jsonPath, Class<R> classe)
			throws IOException, JsonParseException, JsonMappingException {
		byte[] jsonData = Files.readAllBytes(jsonPath);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonData, classe);
	}

}
