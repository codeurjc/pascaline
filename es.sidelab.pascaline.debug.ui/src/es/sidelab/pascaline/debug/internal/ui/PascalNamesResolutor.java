/*******************************************************************************
 * This file is part of Pascaline.
 * 
 * Copyright (c) Pascaline Team.
 * http://pascaline.sourceforge.net/
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package es.sidelab.pascaline.debug.internal.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class resolves compiler names. When compiled, some types from source code are changed for their corresponding
 * compiler types. For instance, String (which is a built-in Pascal type) is substituted by ShortString (the compiler
 * type).
 * 
 * @author patxi
 * 
 */
public class PascalNamesResolutor {
	
	static final Pattern SHORTSTRING = Pattern.compile("SHORTSTRING");
	static final String STRING = "string";
	static final Pattern SMALLINT = Pattern.compile("SMALLINT");
	static final String INTEGER = "integer";
	static final Pattern DOUBLE = Pattern.compile("DOUBLE");
	static final String REAL = "real";
	static final Pattern ARRAYOF = Pattern.compile("array\\s*\\[\\s*\\d+\\s*\\.\\.\\d+\\s*\\]\\s*of\\s+(.*)");
	
	static final Pattern THIS = Pattern.compile("this");
	static final String SELF = "self";
	

	public static String resolveTypeName(String compilerTypeName) {
		Matcher matcher = ARRAYOF.matcher(compilerTypeName);
		if (matcher.matches()) {
			String arrayType = null;
			do {
				arrayType = matcher.group(1);
				matcher = ARRAYOF.matcher(arrayType);
			} while (matcher.matches());

			String baseName = compilerTypeName.substring(0, compilerTypeName.indexOf(arrayType));
			return baseName + resolveTypeName(arrayType);
		}

		matcher = SHORTSTRING.matcher(compilerTypeName);
		if(matcher.matches()) {
			return STRING;
		}
		
		matcher = SMALLINT.matcher(compilerTypeName);
		if (matcher.matches()) {
			return INTEGER;
		}

		matcher = DOUBLE.matcher(compilerTypeName);
		if (matcher.matches()) {
			return REAL;
		}

		return compilerTypeName.toLowerCase();
	}

	public static String resolveName(String compilerName) {
		Matcher matcher = THIS.matcher(compilerName);

		if (matcher.matches()) {
			return SELF;
		}
		
		return compilerName.toLowerCase();
	}
}
