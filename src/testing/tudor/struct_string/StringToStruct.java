package testing.tudor.struct_string;

import java.util.Vector;

import core.claim.parser.*;

public class StringToStruct {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vector<ClaimConstruct> fields = new Vector<ClaimConstruct>();
		
		fields.add(new ClaimVariable(new String("var11")));
		fields.add(new ClaimVariable(new String("var12")));
		
		// inner struct:
		Vector<ClaimConstruct> innerFields = new Vector<ClaimConstruct>();
		
		innerFields.add(new ClaimVariable(new String("var21")));
		innerFields.add(new ClaimVariable(new String("var22")));
		innerFields.add(new ClaimValue(new String("const21")));
		innerFields.add(new ClaimVariable(new String("var23")));
		innerFields.add(new ClaimVariable(new String("var24")));
		innerFields.add(new ClaimValue(new String("const22")));
		
		ClaimStructure innerStructure = new ClaimStructure(innerFields);
		////////////////
		fields.add(innerStructure);
		
		fields.add(new ClaimVariable(new String("var13")));
		fields.add(new ClaimValue(new String("const11")));
		fields.add(new ClaimVariable(new String("var14")));
		fields.add(new ClaimValue(new String("const12")));
		fields.add(new ClaimValue(new String("const13")));
		fields.add(new ClaimVariable(new String("var15")));
		fields.add(new ClaimVariable(new String("var16")));
		
		ClaimStructure structure = new ClaimStructure(fields);
		
		String unparsedStructure = structure.toString();
		structure = ClaimStructure.parseString(unparsedStructure);
		System.out.println(structure.toString());
	}

}
