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
package net.sf.pascaline.managedbuilder.core.makegen;

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator;
import org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator2;
import org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyInfo;
import org.eclipse.cdt.managedbuilder.pdomdepgen.PDOMDependencyGenerator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * Extracted from {@link PDOMDependencyGenerator} and DefaultFortranDependencyCalculator
 * 
 * @author patxi
 * 
 */
public class DefaultFreePascalDependencyCalculator implements IManagedDependencyGenerator, IManagedDependencyGenerator2 {

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGeneratorType#getCalculatorType()
	 */
	@Override
	public int getCalculatorType() {
		return TYPE_EXTERNAL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator2#getDependencyFileExtension(org.eclipse.cdt.managedbuilder.core.IConfiguration, org.eclipse.cdt.managedbuilder.core.ITool)
	 */
	@Override
	public String getDependencyFileExtension(IConfiguration buildContext, ITool tool) {
		return "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator2#getDependencySourceInfo(org.eclipse.core.runtime.IPath, org.eclipse.core.resources.IResource, org.eclipse.cdt.managedbuilder.core.IBuildObject, org.eclipse.cdt.managedbuilder.core.ITool, org.eclipse.core.runtime.IPath)
	 */
	@Override
	public IManagedDependencyInfo getDependencySourceInfo(IPath source, IResource resource, IBuildObject buildContext,
			ITool tool, IPath topBuildDirectory) {
		return new FreePascalCompilerDependencyCalculator(source, resource, buildContext, tool, topBuildDirectory);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator2#getDependencySourceInfo(org.eclipse.core.runtime.IPath, org.eclipse.cdt.managedbuilder.core.IBuildObject, org.eclipse.cdt.managedbuilder.core.ITool, org.eclipse.core.runtime.IPath)
	 */
	@Override
	public IManagedDependencyInfo getDependencySourceInfo(IPath source, IBuildObject buildContext, ITool tool,
			IPath topBuildDirectory) {
		return getDependencySourceInfo(source, null, buildContext, tool, topBuildDirectory);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator2#postProcessDependencyFile(org.eclipse.core.runtime.IPath, org.eclipse.cdt.managedbuilder.core.IConfiguration, org.eclipse.cdt.managedbuilder.core.ITool, org.eclipse.core.runtime.IPath)
	 */
	@Override
	public boolean postProcessDependencyFile(IPath dependencyFile, IConfiguration buildContext, ITool tool,
			IPath topBuildDirectory) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator#findDependencies(org.eclipse.core.resources.IResource, org.eclipse.core.resources.IProject)
	 */
	@Override
	public IResource[] findDependencies(IResource resource, IProject project) {
		FreePascalCompilerDependencyCalculator depCalculator = new FreePascalCompilerDependencyCalculator(null,
				resource, null, null, null);
		IPath[] dependencies = depCalculator.getDependencies();
		IResource[] resources = new IResource[dependencies.length];
		int index = 0;
		for (IPath path : dependencies) {
			resources[index++] = project.getFile(path);
		}
		
		return resources;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyGenerator#getDependencyCommand(org.eclipse.core.resources.IResource, org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo)
	 */
	@Override
	public String getDependencyCommand(IResource resource, IManagedBuildInfo info) {
		return null;
	}


}
