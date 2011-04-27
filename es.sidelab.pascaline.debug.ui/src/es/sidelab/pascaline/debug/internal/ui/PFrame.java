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


import org.eclipse.cdt.debug.core.model.ICVariable;
import org.eclipse.cdt.debug.internal.core.model.CStackFrame;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

import es.sidelab.pascaline.internal.core.model.PascalElement;


/**
 * @author patxi
 *
 */
public class PFrame {

	private CStackFrame frame;

	/**
	 * @return the frame
	 */
	public CStackFrame getFrame() {
		return frame;
	}

	public PFrame(CStackFrame frame) {
		this.frame = frame;
	}

	public IVariable[] getVariables() throws DebugException {
		IVariable[] locals = frame.getVariables();

		String file = frame.getFile();
		PascalElement ast = getAST(file);
		
		IVariable[] globals = new IVariable[0];
		if (ast != null) {
			globals = getGlobals(ast);
		}
		
		IVariable[] variables = new IVariable[locals.length + globals.length];
		for (int i = 0; i < globals.length; i++) {
			variables[i] = new PVariable((ICVariable) globals[i], null);
		}
		for (int i = globals.length; i < variables.length; i++) {
			variables[i] = new PVariable((ICVariable) locals[i - globals.length], null);
		}

		for (IVariable var : variables) {
			((PVariable) var).setFrame(this);
		}
		
		return variables;
	}

	/**
	 * @param ast
	 */
	private IVariable[] getGlobals(PascalElement ast) {
		return new IVariable[0];		
	}

	/**
	 * @param file
	 */
	private PascalElement getAST(String file) {
		Object source = frame.getLaunch().getSourceLocator().getSourceElement(frame);
		return null;
	}

}
