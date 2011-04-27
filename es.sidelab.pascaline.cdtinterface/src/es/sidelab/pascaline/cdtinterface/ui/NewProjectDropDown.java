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
package es.sidelab.pascaline.cdtinterface.ui;

import org.eclipse.cdt.internal.ui.ICHelpContextIds;
import org.eclipse.cdt.internal.ui.wizards.AbstractWizardDropDownAction;
import org.eclipse.cdt.internal.ui.wizards.CWizardRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;

public class NewProjectDropDown extends AbstractWizardDropDownAction {

	/**
	 * 
	 */
	public NewProjectDropDown() {
		super();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ICHelpContextIds.OPEN_PROJECT_WIZARD_ACTION);
	}
	
	@Override
	protected IAction[] getWizardActions() {
		return CWizardRegistry.getProjectWizardActions();
	}

}
