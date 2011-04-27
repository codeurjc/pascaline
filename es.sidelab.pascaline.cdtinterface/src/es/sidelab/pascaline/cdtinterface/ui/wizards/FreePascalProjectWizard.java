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


import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingObject;
import org.eclipse.cdt.ui.newui.UIMessages;
import org.eclipse.cdt.ui.wizards.CDTCommonProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;


public class FreePascalProjectWizard extends CDTCommonProjectWizard {

	private static final String WIZARD_TITLE = "Free Pascal Project";
	private static final String WIZARD_DESCRIPTION = "Create Free Pascal project of selected type";

	public FreePascalProjectWizard() {
		super(WIZARD_TITLE, WIZARD_DESCRIPTION);
	}
	
	public FreePascalProjectWizard(String name, String desc) {
		super(name, desc);
	}

	@Override
	protected IProject continueCreation(IProject prj) {
		try {
			CProjectNature.addCNature(prj, new NullProgressMonitor());
			// TODO: This doesn't work, as CDT provides its own preference property for auto build
//			IWorkspace ws = ResourcesPlugin.getWorkspace();
//	        IWorkspaceDescription wsd = ws.getDescription();
//	        wsd.setAutoBuilding(true);
//	        ws.setDescription(wsd);
			ICProjectDescription desc = CCorePlugin.getDefault().getProjectDescription(prj, true);
//			ICProjectDescription desc = CoreModel.getDefault().getProjectDescription(prj, true);
			
		} catch (CoreException e) {
		}

		return prj;
	}

	@Override
	public String[] getNatures() {
		return new String[] { CProjectNature.C_NATURE_ID };
	}

	@Override
	public String[] getContentTypeIDs() {
		return new String[] {CDTInterfacePlugin.CONTENT_TYPE_PASCAL_SOURCE};
	}

	@Override
	public String[] getLanguageIDs() {
		return new String[] {CDTInterfacePlugin.LANGUAGE_ID};
	}
	
	@Override
	public String[] getExtensions() {
		return new String[] {CDTInterfacePlugin.PASCAL_EXTENSION};
	}
	
	@Override
	public void addPages() {
		fMainPage= new FreePascalMainWizardPage("Pascal Project");
		fMainPage.setDescription(WIZARD_DESCRIPTION);
		addPage(fMainPage);
	}
	
}
