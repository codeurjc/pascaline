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
package es.sidelab.pascaline.managedbuilder.core.makegen;

public class FreePascalMakeFileGenerator extends TurboPascalMakefileGenerator {

	@Override
	public String getLanguageModeOption() {
		return "-Mfpc";
	}
}
