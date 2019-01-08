package org.ordogene.file.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.UnmarshalException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class used to parse json in java object
 * @author darwinners team
 *
 */
public class Parser {

	private Parser() {
	}

	/**
	 * 
	 * @param jsonPath : Path of the file to parse
	 * @param classe : class of the Object to deserialize into <- Must implements Validable.
	 * @return parsed file in a Validable object.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IllegalAccessException
	 * @throws UnmarshalException : if all the required fields are not presents in the JSON.
	 */
	public static Validable parseJsonFile(Path jsonPath, Class<? extends Validable> classe)
			throws IOException, JsonParseException, JsonMappingException, IllegalAccessException, UnmarshalException {
		byte[] jsonData = Files.readAllBytes(jsonPath);
		return parseJsonFile(jsonData, classe);
	}


	/**
	 * 
	 * @param jsonPath : String to parse
	 * @param classe : class of the Object to deserialize into <- Must implements Validable.
	 * @return parsed file in a Validable object.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IllegalAccessException
	 * @throws UnmarshalException : if all the required fields are not presents in the JSON.
	 */
	public static Validable parseJsonFile(String jsonString, Class<? extends Validable> classe)
			throws JsonParseException, JsonMappingException, IOException, UnmarshalException {
		byte[] jsonData = jsonString.getBytes();
		return parseJsonFile(jsonData, classe);
	}

	/**
	 * 
	 * @param jsonPath : byte array to parse
	 * @param classe : class of the Object to deserialize into (Must implements Validable)
	 * @return parsed file in a Validable object.
	 * @throws IOException : if impossible to read the data
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IllegalAccessException
	 * @throws UnmarshalException : if all the required fields are not presents in the JSON.
	 */
	public static Validable parseJsonFile(byte[] jsonData, Class<? extends Validable> classe)
			throws JsonParseException, JsonMappingException, IOException, UnmarshalException {
		ObjectMapper objectMapper = new ObjectMapper();
		Validable instance = objectMapper.readValue(jsonData, classe);
		if (!instance.isValid()) {
			throw new UnmarshalException("Missing fields in the JSON");
		}
		return instance;
	}

}
