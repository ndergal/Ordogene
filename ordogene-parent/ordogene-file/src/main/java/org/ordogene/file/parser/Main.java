package org.ordogene.file.parser;

import java.io.IOException;
import java.nio.file.Paths;

import org.ordogene.file.Model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Main {
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		System.out.println(
				Parser.parseJsonFile(Paths.get("/home/ordogene/Documents/examples/fitness1.json"), Model.class));
	}
}
