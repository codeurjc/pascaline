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
package es.sidelab.pascaline.debug.ui.views.variables;


import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import es.sidelab.pascaline.debug.internal.ui.PFrame;
import es.sidelab.pascaline.debug.internal.ui.PVariable;


/**
 * @author patxi
 *
 */
public class PascalVariablesContentProvider implements ITreeContentProvider {

	PFrame frame = null;
	Object[] EMPTY_ARRAY = new Object[0];

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PFrame) {
			PFrame frame = (PFrame) parentElement;
			try {
				IVariable variables[] = frame.getVariables();
				return variables;
			} catch (DebugException e) {
				e.printStackTrace();
			}
		}
		
		if (parentElement instanceof PVariable) {
			PVariable var = (PVariable) parentElement;
			try {
				return var.getValue().getVariables();
			} catch (DebugException e) {
				e.printStackTrace();
			}
		}

		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PVariable) {
			return ((PVariable) element).getParent();
		}
		return frame;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		frame = ((PFrame) newInput);
	}

}
