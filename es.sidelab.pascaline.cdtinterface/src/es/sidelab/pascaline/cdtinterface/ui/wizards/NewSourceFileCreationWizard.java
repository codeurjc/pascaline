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
package es.sidelab.pascaline.cdtinterface.ui.wizards;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;
import es.sidelab.pascaline.core.PascalineCorePlugin;

public class NewSourceFileCreationWizard extends org.eclipse.cdt.ui.wizards.NewSourceFileCreationWizard {

	public NewSourceFileCreationWizard() {
		super();
		setDefaultPageImageDescriptor(CDTInterfacePlugin.imageDescriptorFromPlugin(PascalineCorePlugin.PLUGIN_ID,
				"icons/wizban/newpfile_wiz.gif"));
		setWindowTitle("New Pascal Source File");
	}
	
	@Override
	public void addPages() {
//		super.addPages();
        fPage = new NewSourceFileCreationWizardPage();
        addPage(fPage);
        fPage.init(getSelection());
	}

}
