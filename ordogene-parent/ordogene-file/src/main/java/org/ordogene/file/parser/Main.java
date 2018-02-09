package org.ordogene.file.parser;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.bind.UnmarshalException;

import org.ordogene.file.JSONModel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Main {
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, UnmarshalException {
		System.out.println(
				Parser.parseJsonFile(Paths.get("/home/ordogene/git/ordogene/ordogene-parent/ordogene-file/src/main/java/org/ordogene/file/testJson/short_path_200.json"), JSONModel.class));
	}
}
