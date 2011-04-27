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
package es.sidelab.pascaline.managedbuilder.core.makegen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Vector;


import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.CSourceEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.util.CDataUtil;
import org.eclipse.cdt.core.settings.model.util.PathSettingsContainer;
import org.eclipse.cdt.internal.core.model.Util;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFileInfo;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.macros.BuildMacroException;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator2;
import org.eclipse.cdt.managedbuilder.makegen.gnu.GnuMakefileGenerator;
import org.eclipse.cdt.managedbuilder.makegen.gnu.ManagedBuildGnuToolInfo;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import es.sidelab.pascaline.internal.core.model.PascalElement;
import es.sidelab.pascaline.managedbuilder.core.ManagedBuilderCorePlugin;


/**
 * @author Patxi Gortázar
 * 
 */
public class TurboPascalMakefileGenerator implements IManagedBuilderMakefileGenerator2 {

	/**
	 * This class is used to recursively walk the project and determine which
	 * modules contribute buildable source files. 
	 */
	protected class ResourceProxyVisitor implements IResourceProxyVisitor {
		private TurboPascalMakefileGenerator generator;
		private IConfiguration config;
//		private IManagedBuildInfo info;

		/**
		 * Constructs a new resource proxy visitor to quickly visit project
		 * resources.
		 */
		public ResourceProxyVisitor(TurboPascalMakefileGenerator generator, IManagedBuildInfo info) {
			this.generator = generator;
			this.config = info.getDefaultConfiguration();
		}

		public ResourceProxyVisitor(TurboPascalMakefileGenerator generator, IConfiguration cfg) {
			this.generator = generator;
			this.config = cfg;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceProxyVisitor#visit(org.eclipse.core.resources.IResourceProxy)
		 */
		public boolean visit(IResourceProxy proxy) throws CoreException {
			// No point in proceeding, is there 
			if (generator == null) {
				return false;
			}
			
			IResource resource = proxy.requestResource();
			boolean isSource = isSource(resource.getProjectRelativePath());
			
			// Is this a resource we should even consider
			if (proxy.getType() == IResource.FILE) {
				// If this resource has a Resource Configuration and is not excluded or
				// if it has a file extension that one of the tools builds, add the sudirectory to the list
//				boolean willBuild = false; 
				IResourceInfo rcInfo = config.getResourceInfo(resource.getProjectRelativePath(), false);
				if (isSource/* && !rcInfo.isExcluded()*/) {
					boolean willBuild = false;
					if(rcInfo instanceof IFolderInfo){
						String ext = resource.getFileExtension();				
						if (((IFolderInfo)rcInfo).buildsFileType(ext) &&
								// If this file resource is a generated resource, then it is uninteresting
								!generator.isGeneratedResource(resource)) {
							willBuild = true;
						}
					} else {
						willBuild = true;
					}
					
					if(willBuild)
						generator.appendBuildSubdirectory(resource);
				}
//				if (willBuild) {
//					if ((resConfig == null) || (!(resConfig.isExcluded()))) {
//						generator.appendBuildSubdirectory(resource);
//					}
//				}				
				return false;
			} else if (proxy.getType() == IResource.FOLDER){
				
				if(!isSource || generator.isGeneratedResource(resource))
					return false;
				return true;
			}

			// Recurse into subdirectories
			return true;
		}

	}

	/**
	 * 
	 */
	private static final String MAKEFILE = "makefile";
	private IPath buildWorkingDir;
	private IProject project;
	private IResource[] projectResources;
	private IProgressMonitor monitor;
	private String buildTargetName;
	private String buildTargetExt;
	private IConfiguration config;
	private IBuilder builder;
	private IPath topBuildDir;
	private ICSourceEntry[] srcEntries;
	private PathSettingsContainer toolInfos;
	private Vector subdirList;

	public String getLanguageModeOption() {
		return "-Mtp";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator2#initialize(int, org.eclipse.cdt.managedbuilder.core.IConfiguration, org.eclipse.cdt.managedbuilder.core.IBuilder, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initialize(int buildKind, IConfiguration cfg, IBuilder builder, IProgressMonitor monitor) {
		// Save the project so we can get path and member information
		this.project = cfg.getOwner().getProject();
		if (builder == null) {
			builder = cfg.getEditableBuilder();
		}
		try {
			projectResources = project.members();
		} catch (CoreException e) {
			projectResources = null;
		}
		// Save the monitor reference for reporting back to the user
		this.monitor = monitor;
		// Get the build info for the project
		// this.info = info;
		// Get the name of the build target
		buildTargetName = cfg.getArtifactName();
		// Get its extension
		buildTargetExt = cfg.getArtifactExtension();

		try {
			// try to resolve the build macros in the target extension
			buildTargetExt = ManagedBuildManager.getBuildMacroProvider().resolveValueToMakefileFormat(buildTargetExt,
					"", //$NON-NLS-1$
					" ", //$NON-NLS-1$
					IBuildMacroProvider.CONTEXT_CONFIGURATION, builder);
		} catch (BuildMacroException e) {
		}
		
		try {
			// try to resolve the build macros in the target name
			String resolved = ManagedBuildManager.getBuildMacroProvider().resolveValueToMakefileFormat(buildTargetName,
					"", //$NON-NLS-1$
					" ", //$NON-NLS-1$
					IBuildMacroProvider.CONTEXT_CONFIGURATION, builder);
			if ((resolved = resolved.trim()).length() > 0)
				buildTargetName = resolved;
		} catch (BuildMacroException e) {
		}

		
		if (buildTargetExt == null) {
			buildTargetExt = new String();
		}
		// Cache the build tools
		config = cfg;
		this.builder = builder;

		initToolInfos();
		// set the top build dir path
		topBuildDir = project.getFolder(cfg.getName()).getFullPath();

		srcEntries = config.getSourceEntries();
		if (srcEntries.length == 0) {
			srcEntries = new ICSourceEntry[] { new CSourceEntry(Path.EMPTY, null, ICSettingEntry.RESOLVED
					| ICSettingEntry.VALUE_WORKSPACE_PATH) };
		} else {
			ICConfigurationDescription cfgDes = ManagedBuildManager.getDescriptionForConfiguration(config);
			srcEntries = CDataUtil.resolveEntries(srcEntries, cfgDes);
		}		
	}

	private void initToolInfos() {
		toolInfos = PathSettingsContainer.createRootContainer();

		IResourceInfo rcInfos[] = config.getResourceInfos();
		for (int i = 0; i < rcInfos.length; i++) {
			IResourceInfo rcInfo = rcInfos[i];
			if (rcInfo.isExcluded() /*&& !((ResourceInfo)rcInfo).isRoot()*/)
				continue;

			ToolInfoHolder h = getToolInfo(rcInfo.getPath(), true);
			if (rcInfo instanceof IFolderInfo) {
				IFolderInfo fo = (IFolderInfo) rcInfo;
				h.buildTools = fo.getFilteredTools();
				h.buildToolsUsed = new boolean[h.buildTools.length];
				h.gnuToolInfos = new ManagedBuildGnuToolInfo[h.buildTools.length];
			} else {
				IFileInfo fi = (IFileInfo) rcInfo;
				h.buildTools = fi.getToolsToInvoke();
				h.buildToolsUsed = new boolean[h.buildTools.length];
				h.gnuToolInfos = new ManagedBuildGnuToolInfo[h.buildTools.length];
			}
		}
	}

	private ToolInfoHolder getToolInfo(IPath path, boolean create) {
		PathSettingsContainer child = toolInfos.getChildContainer(path, create, create);
		ToolInfoHolder h = null;
		if (child != null) {
			h = (ToolInfoHolder) child.getValue();
			if (h == null && create) {
				h = new ToolInfoHolder();
				child.setValue(h);
			}
		}
		return h;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#generateDependencies()
	 */
	@Override
	public void generateDependencies() throws CoreException {
		// Nothing to do here
	}

	protected boolean isSource(IPath path){
		return !CDataUtil.isExcluded(path, srcEntries);
//		path = path.makeRelative();
//		for(int i = 0; i < srcPaths.length; i++){
//			if(srcPaths[i].isPrefixOf(path))
//				return true;
//		}
//		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#generateMakefiles(org.eclipse.core.resources.IResourceDelta)
	 */
	@Override
	public MultiStatus generateMakefiles(IResourceDelta delta) throws CoreException {
		/*
		 * Let's do a sanity check right now. 
		 * 
		 * 1. This is an incremental build, so if the top-level directory is not 
		 * there, then a rebuild is needed.
		 */
		// IFolder folder = project.getFolder(config.getName());
		// if (!folder.exists()) {
		// return regenerateMakefiles();
		// }

		// Return value
		MultiStatus status;

		// Visit the resources in the delta and compile a list of subdirectories to regenerate
		//		updateMonitor(ManagedMakeMessages.getFormattedString("MakefileGenerator.message.calc.delta", project.getName()));	//$NON-NLS-1$
		// ResourceDeltaVisitor visitor = new ResourceDeltaVisitor(this, config);
		// delta.accept(visitor);
		// checkCancel();

		// Get all the subdirectories participating in the build
		updateMonitor("Finding sources");	
		ResourceProxyVisitor resourceVisitor = new ResourceProxyVisitor(this, config);
		project.accept(resourceVisitor, IResource.NONE);
		checkCancel();

		// Make sure there is something to build
		if (getSubdirList().isEmpty()) {
			 String info = "No source"; 
			 updateMonitor(info);
			 status = new MultiStatus(ManagedBuilderCorePlugin.PLUGIN_ID, IStatus.INFO, new String(), null);
			 status.add(new Status(IStatus.INFO, ManagedBuilderCorePlugin.PLUGIN_ID, NO_SOURCE_FOLDERS, info, null));
			 return status;
		}

		// Make sure the build directory is available
		topBuildDir = createDirectory(config.getName());
		checkCancel();

		// Make sure that there is a makefile containing all the folders participating
		// IPath srcsFilePath = topBuildDir.append(SRCSFILE_NAME);
		// IFile srcsFileHandle = createFile(srcsFilePath);
		// buildSrcVars.clear();
		// buildOutVars.clear();
		// buildDepVars.clear();
		// topBuildOutVars.clear();
		// populateSourcesMakefile(srcsFileHandle);
		// checkCancel();

		// Regenerate any fragments that are missing for the exisiting directories NOT modified
		// Iterator iter = getSubdirList().listIterator();
		// while (iter.hasNext()) {
		// IContainer subdirectory = (IContainer)iter.next();
		// if (!getModifiedList().contains(subdirectory)) {
		// // Make sure the directory exists (it may have been deleted)
		// if (!subdirectory.exists()) {
		// appendDeletedSubdirectory(subdirectory);
		// continue;
		// }
		// // Make sure a fragment makefile exists
		// IPath fragmentPath = getBuildWorkingDir().append(subdirectory.getProjectRelativePath()).append(MODFILE_NAME);
		// IFile makeFragment = project.getFile(fragmentPath);
		// if (!makeFragment.exists()) {
		// // If one or both are missing, then add it to the list to be generated
		// getModifiedList().add(subdirectory);
		// }
		// }
		// }

		// Delete the old dependency files for any deleted resources
		// iter = getDeletedFileList().listIterator();
		// while (iter.hasNext()) {
		// IResource deletedFile = (IResource)iter.next();
		// deleteDepFile(deletedFile);
		// deleteBuildTarget(deletedFile);
		// }

		// Regenerate any fragments for modified directories
		// iter = getModifiedList().listIterator();
		// while (iter.hasNext()) {
		// IContainer subDir = (IContainer) iter.next();
		// // Make sure the directory exists (it may have been deleted)
		// if (!subDir.exists()) {
		// appendDeletedSubdirectory(subDir);
		// continue;
		// }
		// //populateFragmentMakefile(subDir); // See below
		// checkCancel();
		// }

		// Recreate all module makefiles
		// NOTE WELL: For now, always recreate all of the fragment makefile. This is necessary
		// in order to re-populate the buildVariable lists. In the future, the list could
		// possibly segmented by subdir so that all fragments didn't need to be
		// regenerated
		// iter = getSubdirList().listIterator();
		// while (iter.hasNext()) {
		// IContainer subDir = (IContainer)iter.next();
		// try {
		// populateFragmentMakefile(subDir);
		// } catch (CoreException e) {
		// // Probably should ask user if they want to continue
		// checkCancel();
		// continue;
		// }
		// checkCancel();
		// }

		// Calculate the inputs and outputs of the Tools to be generated in the main makefile
		// calculateToolInputsOutputs();
		// checkCancel();

		// Re-create the top-level makefile
		IPath makefilePath = topBuildDir.append(MAKEFILE_NAME);
		IFile makefileHandle = createFile(makefilePath);
//		IToolChain toolChain = config.getToolChain();
//		ITool tool = toolChain.getTools()[0];
//		IOption ooProject = tool.getOptionById(OO_PROJECT);
//		IOption languageMode = tool.getOptionById(LANGUAGE_MODE_SETTING);
//		try {
//			String language = languageMode.getSelectedEnum();
			populateTopMakefile(makefileHandle, false);
//		} catch (BuildException e) {
//			ManagedBuilderCorePlugin.log(e);
//		}
		checkCancel();

		// Remove deleted folders from generated build directory
		// iter = getDeletedDirList().listIterator();
		// while (iter.hasNext()) {
		// IContainer subDir = (IContainer) iter.next();
		// removeGeneratedDirectory(subDir);
		// checkCancel();
		// }

		// How did we do
		// if (!getInvalidDirList().isEmpty()) {
		// status = new MultiStatus(ManagedBuilderCorePlugin.getUniqueIdentifier(), IStatus.WARNING, new String(),
		// null);
		// // Add a new status for each of the bad folders
		// // TODO: fix error message
		// iter = getInvalidDirList().iterator();
		// while (iter.hasNext()) {
		// status.add(new Status(IStatus.WARNING, ManagedBuilderCorePlugin.getUniqueIdentifier(), SPACES_IN_PATH,
		// ((IContainer) iter.next()).getFullPath().toString(), null));
		// }
		// } else {
			status = new MultiStatus(ManagedBuilderCorePlugin.PLUGIN_ID, IStatus.OK, new String(), null);
		// }

		return status;
	}

	/*  (non-Javadoc)
	 * Create the entire contents of the makefile.
	 * 
	 * @param fileHandle The file to place the contents in.
	 * @param rebuild FLag signalling that the user is doing a full rebuild
	 * @throws CoreException
	 */
	protected void populateTopMakefile(IFile fileHandle, boolean rebuild) throws CoreException {
		StringBuffer buffer = new StringBuffer();

		// Add the header
		buffer.append("##########################################################" + NEWLINE);
		buffer.append("## Autogenerated makefile, do not edit!" + NEWLINE);
		buffer.append("##########################################################" + NEWLINE);

		// Add the macro definitions
		buffer.append("RM := rm -rf" + NEWLINE);

		buffer.append("all: " + NEWLINE);

		IPath programPath = ManagedBuilderCorePlugin.getProgram(project, false);
		if(programPath != null) {
			String program = programPath.toFile().getName();
			IPath projectLocation = project.getLocation();
			String buildDirFullPath = projectLocation.toFile().getAbsolutePath() +File.separator +topBuildDir.removeFirstSegments(1).toOSString();
			String sourcesPath = projectLocation.toFile().getAbsolutePath();
			buffer.append("\tfpc -B -g ");
			
			// This should be done retrieving the command from the Option object
			buffer.append(getLanguageModeOption() + " ");
			buffer.append("-Fu\"\"" + sourcesPath);
			String programName = ManagedBuilderCorePlugin.getProgram(project, false).removeFileExtension().lastSegment();
			if(Platform.getOS().equals(Platform.OS_WIN32)) {
				buffer.append("\"\" -o" + programName + ".exe");
			} else {
				buffer.append("\" -o" + programName);
			}
				
			buffer.append(" -FE\"\"" + buildDirFullPath);
			buffer.append("\"\" ../" + program + NEWLINE);
			buffer.append(NEWLINE);

			buffer.append("clean: $(RM) $(EXECUTABLES) " + program);

			buffer.append(NEWLINE);
		} 
			
			// Save the file
			Util.save(buffer, fileHandle);
	}

//	/**
//	 * @param project
//	 * @return
//	 * @throws CoreException
//	 * @throws CModelException
//	 */
//	private IPath getProgram(IProject project) throws CoreException, CModelException {
//		ICProjectDescription cProjectDesc = CCorePlugin.getDefault().createProjectDescription(project, true);
//		CoreModel model = CCorePlugin.getDefault().getCoreModel();
//		ICProject icProject = model.create(project);
//		ICElement[] children = icProject.getChildren();
//		for (ICElement element : children) {
//			if (element instanceof ISourceRoot) {
//				ISourceRoot sr = (ISourceRoot) element;
//				ICElement[] children2 = sr.getChildren();
//				for (ICElement element2 : children2) {
//					if (element2 instanceof ITranslationUnit) {
//						ITranslationUnit tu = (ITranslationUnit) element2;
//						ICElement[] children3 = tu.getChildren();
//						for (ICElement element3 : children3) {
//							if (element3 instanceof PascalElement.Program) {
//								return tu.getLocation();
//							}
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}
	
	/* (non-Javadoc)
	 * Return or create the makefile needed for the build. If we are creating 
	 * the resource, set the derived bit to true so the CM system ignores 
	 * the contents. If the resource exists, respect the existing derived 
	 * setting.
	 *  
	 * @param makefilePath
	 * @return IFile
	 */
	private IFile createFile(IPath makefilePath) throws CoreException {
		// Create or get the handle for the makefile
		IWorkspaceRoot root = CCorePlugin.getWorkspace().getRoot();
		IFile newFile = root.getFileForLocation(makefilePath);
		if (newFile == null) {
			newFile = root.getFile(makefilePath);
		}
		// Create the file if it does not exist
		ByteArrayInputStream contents = new ByteArrayInputStream(new byte[0]);
		try {
			newFile.create(contents, false, new SubProgressMonitor(monitor, 1));
			// Make sure the new file is marked as derived
			if (!newFile.isDerived()) {
				newFile.setDerived(true);
			}

		} catch (CoreException e) {
			// If the file already existed locally, just refresh to get contents
			if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED)
				newFile.refreshLocal(IResource.DEPTH_ZERO, null);
			else
				throw e;
		}

		return newFile;
	}

	/* (non-Javadoc)
	 * Return or create the folder needed for the build output. If we are
	 * creating the folder, set the derived bit to true so the CM system 
	 * ignores the contents. If the resource exists, respect the existing 
	 * derived setting. 
	 * 
	 * @param string
	 * @return IPath
	 */
	private IPath createDirectory(String dirName) throws CoreException {
		// Create or get the handle for the build directory
		IFolder folder = project.getFolder(dirName);
		if (!folder.exists()) {
			// Make sure that parent folders exist
			IPath parentPath = (new Path(dirName)).removeLastSegments(1);
			// Assume that the parent exists if the path is empty
			if (!parentPath.isEmpty()) {
				IFolder parent = project.getFolder(parentPath);
				if (!parent.exists()) {
					createDirectory(parentPath.toString());
				}
			}

			// Now make the requested folder
			try {
				folder.create(true, true, null);
			} catch (CoreException e) {
				if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED)
					folder.refreshLocal(IResource.DEPTH_ZERO, null);
				else
					throw e;
			}

			// Make sure the folder is marked as derived so it is not added to CM
			if (!folder.isDerived()) {
				folder.setDerived(true);
			}
		}

		return folder.getFullPath();
	}

	/* (non-Javadoc)
	 * Check whether the build has been cancelled. Cancellation requests 
	 * propagated to the caller by throwing <code>OperationCanceledException</code>.
	 * 
	 * @see org.eclipse.core.runtime.OperationCanceledException#OperationCanceledException()
	 */
	protected void checkCancel() {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#getBuildWorkingDir()
	 */
	@Override
	public IPath getBuildWorkingDir() {
		return buildWorkingDir;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#getMakefileName()
	 */
	@Override
	public String getMakefileName() {
		return MAKEFILE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#initialize(org.eclipse.core.resources.IProject, org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initialize(IProject project, IManagedBuildInfo info, IProgressMonitor monitor) {
		// Save the project so we can get path and member information
		this.project = project;
		try {
			projectResources = project.members();
		} catch (CoreException e) {
			projectResources = null;
		}
		// Save the monitor reference for reporting back to the user
		this.monitor = monitor;
		// Get the build info for the project
		// this.info = info;
		// Get the name of the build target
		buildTargetName = info.getBuildArtifactName();
		// Get its extension
		buildTargetExt = info.getBuildArtifactExtension();

		try {
			// try to resolve the build macros in the target extension
			buildTargetExt = ManagedBuildManager.getBuildMacroProvider().resolveValueToMakefileFormat(buildTargetExt,
					"", //$NON-NLS-1$
					" ", //$NON-NLS-1$
					IBuildMacroProvider.CONTEXT_CONFIGURATION, info.getDefaultConfiguration());
		} catch (BuildMacroException e) {
		}

		try {
			// try to resolve the build macros in the target name
			String resolved = ManagedBuildManager.getBuildMacroProvider().resolveValueToMakefileFormat(buildTargetName,
					"", //$NON-NLS-1$
					" ", //$NON-NLS-1$
					IBuildMacroProvider.CONTEXT_CONFIGURATION, info.getDefaultConfiguration());
			if (resolved != null && (resolved = resolved.trim()).length() > 0)
				buildTargetName = resolved;
		} catch (BuildMacroException e) {
		}

		
		if (buildTargetExt == null) {
			buildTargetExt = new String();
		}
		// Cache the build tools
		config = info.getDefaultConfiguration();
		builder = config.getEditableBuilder();
		initToolInfos();
		// set the top build dir path
		topBuildDir = project.getFolder(info.getConfigurationName()).getFullPath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#isGeneratedResource(org.eclipse.core.resources.IResource)
	 */
	@Override
	public boolean isGeneratedResource(IResource resource) {
		// Is this a generated directory ...
		IPath path = resource.getProjectRelativePath();
		// TODO: fix to use builder output dir instead
		String[] configNames = ManagedBuildManager.getBuildInfo(project).getConfigurationNames();
		for (int i = 0; i < configNames.length; i++) {
			String name = configNames[i];
			IPath root = new Path(name);
			// It is if it is a root of the resource pathname
			if (root.isPrefixOf(path))
				return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#regenerateDependencies(boolean)
	 */
	@Override
	public void regenerateDependencies(boolean force) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#regenerateMakefiles()
	 */
	@Override
	public MultiStatus regenerateMakefiles() throws CoreException {
		return generateMakefiles(null);
	}

	class ToolInfoHolder {
		ITool[] buildTools;
		boolean[] buildToolsUsed;
		ManagedBuildGnuToolInfo[] gnuToolInfos;
		Set outputExtensionsSet;
		List dependencyMakefiles;
	}

	/* (non-javadoc)
	 * Answers the list of subdirectories (IContainer's) contributing source code to the build
	 * 
	 * @return List
	 */
	private Vector getSubdirList() {
		if (subdirList == null) {
			subdirList = new Vector();
		}
		return subdirList;
	}
	
	protected void updateMonitor(String msg) {
		if (monitor!= null && !monitor.isCanceled()) {
			monitor.subTask(msg);
			monitor.worked(1);
		}
	}

	/*************************************************************************
	 *   R E S O U R C E   V I S I T O R   M E T H O D S 
	 ************************************************************************/
	
	/**
	 * Adds the container of the argument to the list of folders in the project that
	 * contribute source files to the build. The resource visitor has already established 
	 * that the build model knows how to build the files. It has also checked that
	 * the resource is not generated as part of the build.
	 *  
	 * @param resource
	 */
	protected void appendBuildSubdirectory(IResource resource) {
		IContainer container = resource.getParent();
			// Only add the container once
			if (!getSubdirList().contains(container)) {
				getSubdirList().add(container);
			}
	}
}
