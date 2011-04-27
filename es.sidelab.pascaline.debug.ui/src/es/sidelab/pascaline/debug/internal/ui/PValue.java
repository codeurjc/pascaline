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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.debug.core.model.ICVariable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author patxi
 *
 */
public class PValue implements IValue {

	private PVariable variable;
	private IValue value;
	private boolean object = false;
	private boolean objectResolved = false;

	public PValue(IValue value, PVariable var) {
		this.variable = var;
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		return value.getReferenceTypeName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		return value.getValueString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		List<PVariable> fields = new ArrayList<PVariable>();
		IVariable[] variables = value.getVariables();
		for (IVariable var : variables) {
			if (!var.getName().startsWith("_")) {
				PVariable pVar = new PVariable((ICVariable) var, this.variable);
				pVar.setFrame(this.variable.getFrame());
				fields.add(pVar);
			}
		}
		return fields.toArray(new PVariable[0]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return value.hasVariables();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	@Override
	public boolean isAllocated() throws DebugException {
		return value.isAllocated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	@Override
	public IDebugTarget getDebugTarget() {
		return value.getDebugTarget();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	@Override
	public ILaunch getLaunch() {
		return value.getLaunch();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	@Override
	public String getModelIdentifier() {
		return value.getModelIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return value.getAdapter(adapter);
	}

	/**
	 * @return
	 * @throws DebugException
	 */
	public boolean isObject() throws DebugException {
		if (!objectResolved) {
			resolveObject();
		}
		return object;
	}

	/**
	 * @throws DebugException
	 * 
	 */
	private void resolveObject() throws DebugException {
		for (IVariable var : value.getVariables()) {
			if (var.getName().startsWith("_")) {
				object = true;
				objectResolved = true;
				return;
			}
		}
		objectResolved = true;
	}

}
