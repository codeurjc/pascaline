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
package es.sidelab.pascaline.internal.errorparser;

public class CompInfo {

	private int line;
	private int column;
	private String description;
	private boolean error;
	
	public CompInfo(int line, int column, boolean error) {
		this.line = line;
		this.column = column;
		this.error = error;
	}

	public void setDescription(String description) {
		this.description = description;		
	}

	public int getColumn() {
		return column;
	}

	public String getDescription() {
		return description;
	}

	public int getLine() {
		return line;
	}
	
	public boolean isError() {
		return error;
	}
	
	@Override
	public String toString() {
		return (error?"Error: " : "Warning") + "Line: "+line+" Column: "+column+" Description: "+description;
	}

}
