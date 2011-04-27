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
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizardPage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.widgets.Composite;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;

public class NewSourceFileCreationWizardPage extends org.eclipse.cdt.internal.ui.wizards.filewizard.NewSourceFileCreationWizardPage {

	@Override
	public IPath getFileFullPath() {
		IPath fullPath = super.getFileFullPath();
		if(fullPath != null && !"pas".equals(fullPath.getFileExtension())) {
			if(fullPath.getFileExtension() != null) {
				if("".equals(fullPath.getFileExtension())) {
					return fullPath.removeLastSegments(1).append(fullPath.lastSegment().substring(0, fullPath.lastSegment().indexOf('.')) + ".pas");
				} 
					
				return fullPath.removeFileExtension().addFileExtension("pas");
				
			} else {
				return fullPath.removeLastSegments(1).append(fullPath.lastSegment() + ".pas");
			}
		}
		return fullPath;
	}
	@Override
	protected Template[] getApplicableTemplates() {
		return StubUtility.getFileTemplatesForContentTypes(
				new String[] { CDTInterfacePlugin.CONTENT_TYPE_PASCAL_SOURCE }, null);
	}

}
