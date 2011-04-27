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
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author patxi
 *
 */
public class PVariable implements IVariable {
	
	private ICVariable variable;
	private IDebugElement parent;
	private PFrame frame;
	private boolean string = false;
	private boolean resolved = false;
	private String typeName = null;
	private String name = null;
	private PValue value;

	public PVariable(ICVariable variable, IDebugElement parent) {
		this.variable = variable;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	@Override
	public String getName() throws DebugException {
		if (!resolved) {
			resolve();
		}
		return name;
	}

	/**
	 * @throws DebugException
	 * 
	 */
	private void resolve() throws DebugException {
		typeName = PascalNamesResolutor.resolveTypeName(variable.getReferenceTypeName());
		name = PascalNamesResolutor.resolveName(variable.getName());
		resolved = true;
		if (PascalNamesResolutor.STRING.equals(typeName)) {
			string = true;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		if (!resolved) {
			resolve();
		}
		return typeName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	@Override
	public IValue getValue() throws DebugException {
		if (value == null) {
		if (isString()) {
				IVariable[] fields = variable.getValue().getVariables();
				for (IVariable field : fields) {
					if ("st".equals(((ICVariable) field).getName())) {
						value = new PValue(field.getValue(), this);
					}
				}
			} else {
				value = new PValue(variable.getValue(), this); 
			}
		}
		
		return value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	@Override
	public boolean hasValueChanged() throws DebugException {
		return variable.hasValueChanged();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	@Override
	public IDebugTarget getDebugTarget() {
		return variable.getDebugTarget();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	@Override
	public ILaunch getLaunch() {
		return variable.getLaunch();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	@Override
	public String getModelIdentifier() {
		return variable.getModelIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return variable.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String expression) throws DebugException {
		variable.setValue(expression);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public void setValue(IValue value) throws DebugException {
		variable.setValue(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	@Override
	public boolean supportsValueModification() {
		return variable.supportsValueModification();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	@Override
	public boolean verifyValue(String expression) throws DebugException {
		return variable.verifyValue(expression);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		return variable.verifyValue(value);
	}

	/**
	 * @param frame
	 */
	public void setFrame(PFrame frame) {
		this.frame = frame;
	}

	/**
	 * @return the parent
	 */
	public IDebugElement getParent() {
		return parent;
	}

	/**
	 * @return the frame
	 */
	public PFrame getFrame() {
		return frame;
	}

	/**
	 * @return the string
	 * @throws DebugException
	 */
	public boolean isString() throws DebugException {
		if (!resolved) {
			resolve();
		}
		return string;
	}

	/**
	 * @return
	 * @throws DebugException
	 */
	public boolean isObject() throws DebugException {
		return ((PValue) getValue()).isObject();
	}


}
