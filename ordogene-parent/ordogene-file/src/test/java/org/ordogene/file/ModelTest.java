package org.ordogene.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.xml.bind.UnmarshalException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.ordogene.file.parser.Parser;
import org.ordogene.file.utils.Const;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RunWith(MockitoJUnitRunner.class)
public class ModelTest {
	
	private String location;
	
	@Before
	public void init() {
		PermissionCollection permCollection = Const.class.getProtectionDomain().getPermissions();
		
		if (permCollection != null) {
			Enumeration<Permission> en = permCollection.elements();
			while (en.hasMoreElements() && location == null) {
				Permission perm = en.nextElement();
				if (perm.toString().startsWith("(\"java.io.FilePermission\"")) {
					location = perm.getName();
				}
			}
		}
		
		if (location.endsWith("-")) {
			location = location.substring(0, location.length() - 1);
		}
	}

	@Test
	public void sould_assert_true_after_parsing() throws JsonParseException, JsonMappingException, IOException,
			InstantiationException, IllegalAccessException, UnmarshalException {
		JSONModel m = mock(JSONModel.class);
		when(m.toString()).thenReturn(
				"Model\n[snaps=[5, 10, 20, 100],\nslots=300,\nexecTime=10000,\nenvironment=[Entity [name=FUEL, quantity=200], Entity [name=BIG_GOOD, quantity=0], Entity [name=SMALL_BAD, quantity=0]],\nactions=[Action [name=MAKE_GOOD, time=5, input=[Input [name=FUEL, quantity=60, relation=c]], output=[Entity [name=BIG_GOOD, quantity=1]]], Action [name=MAKE_BAD, time=2, input=[Input [name=FUEL, quantity=6, relation=c]], output=[Entity [name=SMALL_BAD, quantity=1]]]],\nfitness=Fitness [type=max, operands=[Operand [name=BIG_GOOD, coef=11], Operand [name=SMALL_BAD, coef=1]]],\ncurrentEnvironment=null]");
		JSONModel m1 = (JSONModel) Parser.parseJsonFile(Paths.get(location + "org/ordogene/file/testJson/fitness1.json"), JSONModel.class);
		assertEquals(m.toString(), m1.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_IAE_if_a_snap_negatif() {
		JSONModel m = new JSONModel();
		ArrayList<Integer> l = new ArrayList<>();
		l.add(-2);
		m.setSnaps(l);
	}

	@Test(expected = JsonMappingException.class)
	public void shouldIAE_if_a_snap_value_negatif() throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, UnmarshalException {
		
		System.out.println(location);
		Parser.parseJsonFile(Paths.get(
				location + "org/ordogene/file/testJson/test1.json"),
				JSONModel.class);
	}

}
