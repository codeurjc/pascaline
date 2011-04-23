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
package net.sf.pascaline.debug.ui.views.variables;

import net.sf.pascaline.debug.internal.ui.PVariable;
import net.sf.pascaline.debug.ui.PascalDebugUI;

import org.eclipse.cdt.debug.core.model.ICGlobalVariable;
import org.eclipse.cdt.debug.core.model.ICVariable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * @author patxi
 *
 */
public class PascalVariablesLabelProvider implements ITableLabelProvider {

	// @Override
	// public Image getImage(Object element) {
	// return null;
	// }
	//
	// @Override
	// public String getText(Object element) {
	// if (element instanceof IVariable) {
	// IVariable variable = (IVariable) element;
	// try {
	// String name = variable.getName();
	// name += ":" + variable.getReferenceTypeName();
	// name += "=" + variable.getValue().getValueString();
	// return name;
	// } catch (DebugException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return "Unknown";
	// }

	/**
	 * 
	 */
	private static final String DUMMY_VALUE_LABEL = "{...}";

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if(element instanceof ICGlobalVariable) {
			return PascalDebugUI.imageDescriptorFromPlugin(PascalDebugUI.PLUGIN_ID, PascalDebugUI.GLOBAL_ICON_PATH)
					.createImage();
		} else if (element instanceof ICVariable) {
			return PascalDebugUI.imageDescriptorFromPlugin(PascalDebugUI.PLUGIN_ID, PascalDebugUI.LOCAL_ICON_PATH)
					.createImage();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof PVariable) {
			PVariable var = (PVariable) element;
			if (columnIndex == 0) {
				try {
					String name = var.getName();
					String typeName = var.getReferenceTypeName();
					return name + " : " + typeName;
				} catch (DebugException e) {
					PascalDebugUI.log("Debug exception thrown when retrieving data from a variable", e);
				}
			}

			if (columnIndex == 1) {
				try {
					if(var.isString()) {
						StringBuilder sb = new StringBuilder();
						sb.append(var.getName());
						PVariable parent = (PVariable) var.getParent();
						while (parent != null) {
							sb.insert(0, ".");
							sb.insert(0, parent.getName());
							parent = (PVariable) parent.getParent();
						}
						String stringValue = var.getFrame().getFrame().evaluateExpressionToString(sb.toString());
						return stringValue;
					}
					
					if (var.isObject()) {
						return DUMMY_VALUE_LABEL;
					}

					return var.getValue().getValueString();
				} catch (DebugException e) {
					e.printStackTrace();
				}
				
				return DUMMY_VALUE_LABEL;
			}
		}

		return "Unknown";
	}

}
