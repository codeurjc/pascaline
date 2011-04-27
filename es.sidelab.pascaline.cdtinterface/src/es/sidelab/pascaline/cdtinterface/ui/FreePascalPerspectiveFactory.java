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


import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import es.sidelab.pascaline.cdtinterface.ui.wizards.PascalWizardRegistry;

/**
 * @author patxi
 * 
 */
public class FreePascalPerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout folder1 = layout.createFolder("topLeft", IPageLayout.LEFT, (float) 0.25, editorArea); //$NON-NLS-1$
		folder1.addView(FreePascalView.FREEPASCAL_VIEW_ID);
		folder1.addView(IPageLayout.ID_RES_NAV);
		folder1.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		IFolderLayout folder2 = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea); //$NON-NLS-1$
		folder2.addView(IPageLayout.ID_PROBLEM_VIEW);
		folder2.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		folder2.addView(IPageLayout.ID_PROP_SHEET);

		IFolderLayout folder3 = layout.createFolder("topRight", IPageLayout.RIGHT, (float) 0.75, editorArea); //$NON-NLS-1$
		folder3.addView(IPageLayout.ID_OUTLINE);

		layout.addActionSet(CUIPlugin.SEARCH_ACTION_SET_ID); // This is the
																// "Open Type"
																// search
																// toolbar
																// action
		layout.addActionSet("es.sidelab.pascaline.cdtinterface.FreePascalElementCreationActionSet");
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		// views - build console
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

		// views - searching
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(FreePascalView.FREEPASCAL_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);

		// link - things we should do
		layout.addShowInPart(FreePascalView.FREEPASCAL_VIEW_ID);
		layout.addShowInPart(IPageLayout.ID_RES_NAV);

		addFreePascalWizardShortcuts(layout);
	}

	/**
	 * @param layout
	 */
	private void addFreePascalWizardShortcuts(IPageLayout layout) {
		// new actions - Fortran project creation wizard
		String[] wizIDs = PascalWizardRegistry.getProjectWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}

		// new actions - Fortran folder creation wizard
		wizIDs = PascalWizardRegistry.getFolderWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		// new actions - Fortran file creation wizard
		wizIDs = PascalWizardRegistry.getFileWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		// new actions - Fortran type creation wizard
//		wizIDs = CWizardRegistry.getTypeWizardIDs();
//		for (int i = 0; i < wizIDs.length; ++i) {
//			layout.addNewWizardShortcut(wizIDs[i]);
//		}
	}

}
