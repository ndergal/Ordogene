package org.ordogene.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.UnmarshalException;

import org.junit.Test;
import org.ordogene.file.parser.Parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ParserTest {

	@Test(expected = UnmarshalException.class)
	public void should_UE_if_Missing_Field_in_file() throws JsonParseException, JsonMappingException,
			InstantiationException, IllegalAccessException, UnmarshalException, IOException {
		Parser.parseJsonFile(Paths.get(
				"/home/ordogene/git/ordogene/ordogene-parent/ordogene-file/src/test/java/org/ordogene/file/testJson/missingFieldJson.json"),
				JSONModel.class);
	}

	@Test(expected = UnmarshalException.class)
	public void should_UE_if_Missing_Field_in_string() throws JsonParseException, JsonMappingException,
			InstantiationException, IllegalAccessException, UnmarshalException, IOException {
		Path path = Paths.get(
				"/home/ordogene/git/ordogene/ordogene-parent/ordogene-file/src/test/java/org/ordogene/file/testJson/missingFieldJson.json");
		BufferedReader br = Files.newBufferedReader(path);
		StringBuilder sb = new StringBuilder();
		String str;
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		str = sb.toString();
		Parser.parseJsonFile(str, JSONModel.class);
	}
}
