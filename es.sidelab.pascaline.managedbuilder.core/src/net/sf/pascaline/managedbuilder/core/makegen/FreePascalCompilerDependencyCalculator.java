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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sf.pascaline.cdtinterface.core.FreePascalLanguage;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyCalculator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentType;


/**
 * @author patxi
 *
 */
public class FreePascalCompilerDependencyCalculator implements IManagedDependencyCalculator {

	private static final List<String> KEYWORDS_AFTER_USE = Arrays.asList(new String[] {
		"type", "var", "begin", "implementation",
			"procedure", "function", "const", "initialization", "finalization" });
	
	private IPath source; // Not used
	private IResource resource;
	private IBuildObject buildContext;
	private ITool tool;
	private IPath topBuildDirectory;

	/**
	 * @param source
	 * @param resource
	 * @param buildContext
	 * @param tool
	 * @param topBuildDirectory
	 */
	public FreePascalCompilerDependencyCalculator(IPath source, IResource resource, IBuildObject buildContext,
			ITool tool, IPath topBuildDirectory) {
		this.source = source;
		this.resource = resource;
		this.buildContext = buildContext;
		this.tool = tool;
		this.topBuildDirectory = topBuildDirectory;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyCalculator#getAdditionalTargets()
	 * @see org.eclipse.cdt.managedbuilder.pdomdepgen.PDOMDependencyCalculator#getAdditionalTargets()
	 */
	@Override
	public IPath[] getAdditionalTargets() {
		// Additional targets are those files that are generated from a source file.
		// For instance, if a file generates two files as a result of compilation.
		// In Pascal, for each file, just one file is generated.
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyCalculator#getDependencies()
	 */
	@Override
	public IPath[] getDependencies() {
		Collection pascalContentTypes = new FreePascalLanguage().getRegisteredContentTypeIds();
		IManagedBuildInfo mngInfo = ManagedBuildManager.getBuildInfo(resource);
		IConfiguration config = mngInfo.getDefaultConfiguration();

		File file = resource.getLocation().toFile();
		
		if (!isPascalFile(file, pascalContentTypes)) {
			return new IPath[0];
		}
		
		List<IPath> dependencies = new ArrayList<IPath>();
		
		dependencies.add(resource.getProjectRelativePath());
		
		String[] usedNames = findUsedUnitNames(file);
		if (usedNames.length != 0) {
			try {
				IResource[] resources = resource.getProject().members();
				IResource[] unitResources = findUnitsInResources(resource.getProject(), pascalContentTypes, resource,
						resources, config.getName(), usedNames);
				if (unitResources != null) {
					for (IResource r : unitResources) {
						dependencies.add(r.getProjectRelativePath());
					}
				}
			} catch (CoreException e) {
				// PascalineCorePlugin.getDefault().log(e.getMessage(), ManagedBuilderCorePlugin.PLUGIN_ID, e);
			}
		}
		return dependencies.toArray(new IPath[dependencies.size()]);
	}

	/**
	 * @param project
	 * @param pascalContentTypes
	 * @param resource2
	 * @param resources
	 * @param name
	 * @param usedNames
	 * @return
	 */
	private IResource[] findUnitsInResources(IProject project, Collection pascalContentTypes, IResource resource,
			IResource[] resourcesToSearch, String name, String[] usedNames) {
		List<IResource> resourcesWithUnitsNeeded = new ArrayList<IResource>();
		List<String> usedNamesList = Arrays.asList(usedNames);
		try {
			for (IResource res : resourcesToSearch) {
				if (res.equals(resource)) {
					continue;
				}
				if (res.getType() == IResource.FILE) {
					File file = res.getLocation().toFile();
					if (!isPascalFile(file, pascalContentTypes)) {
						continue;
					}
					String unitName = findUnitName(file);
					if (unitName != null) {
						if (usedNamesList.contains(unitName)) {
							resourcesWithUnitsNeeded.add(res);
						}
					}
				} else if (res.getType() == IResource.FOLDER) {
					IResource[] unitsFoundInDir = findUnitsInResources(project, pascalContentTypes, resource,
							((IFolder) res).members(), name, usedNames);
					if (unitsFoundInDir != null) {
						resourcesWithUnitsNeeded.addAll(Arrays.asList(unitsFoundInDir));
					}
				}
			}
		} catch (CoreException e) {
			
		}
		return resourcesWithUnitsNeeded.toArray(new IResource[resourcesWithUnitsNeeded.size()]);
	}

	/**
	 * @param file
	 * @return
	 */
	private String findUnitName(File file) {
		try {
			Reader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StreamTokenizer st = new StreamTokenizer(r);
			st.eolIsSignificant(false);

			int token;
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				if (st.ttype == StreamTokenizer.TT_WORD) {
					if (st.sval.equalsIgnoreCase("unit")) {
						token = st.nextToken();
						if (st.ttype == StreamTokenizer.TT_WORD) {
							return st.sval;
						}
					} else if (st.sval.equalsIgnoreCase("program")) {
						return null;
					}
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * @param file
	 * @return
	 */
	private String[] findUsedUnitNames(File file) {
		List<String> names = new ArrayList<String>();

		try {
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			Reader r = new BufferedReader(new InputStreamReader(in));
			StreamTokenizer st = new StreamTokenizer(r);
			st.eolIsSignificant(false);

			int token;
			boolean usesPart = false;
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				if (st.ttype == StreamTokenizer.TT_WORD) {
					if(st.sval.equalsIgnoreCase("uses")) {
						while(usesPart) {
							token = st.nextToken();
							if(st.ttype == StreamTokenizer.TT_WORD) {
								if(!isKeyword(st.sval)) {
									names.add(st.sval);
								} else {
									usesPart = false;
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			return new String[0];
		}
		return names.toArray(new String[names.size()]);
	}

	/**
	 * @param sval
	 * @return
	 */
	private boolean isKeyword(String sval) {
		return KEYWORDS_AFTER_USE.contains(sval.toLowerCase());
	}

	/**
	 * @param source2
	 * @param file
	 * @param pascalContentTypes
	 * @return
	 */
	private boolean isPascalFile(File file, Collection pascalContentTypes) {
		
		try {
			IContentType ct = CCorePlugin.getContentType(file.getCanonicalPath());
			if (ct != null) {
				return pascalContentTypes.contains(ct.toString());
			}
		} catch (IOException e) {
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyInfo#getBuildContext()
	 */
	@Override
	public IBuildObject getBuildContext() {
		return buildContext;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyInfo#getSource()
	 */
	@Override
	public IPath getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyInfo#getTool()
	 */
	@Override
	public ITool getTool() {
		return tool;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedDependencyInfo#getTopBuildDirectory()
	 */
	@Override
	public IPath getTopBuildDirectory() {
		return topBuildDirectory;
	}

}
