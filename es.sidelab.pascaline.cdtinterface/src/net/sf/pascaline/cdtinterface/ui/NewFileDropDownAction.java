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
package net.sf.pascaline.cdtinterface.ui;

import org.eclipse.cdt.internal.ui.wizards.AbstractWizardDropDownAction;
import org.eclipse.cdt.internal.ui.wizards.CWizardRegistry;
import org.eclipse.jface.action.IAction;

public class NewFileDropDownAction extends AbstractWizardDropDownAction {

	@Override
	protected IAction[] getWizardActions() {
		return CWizardRegistry.getFileWizardActions();
	}

}
