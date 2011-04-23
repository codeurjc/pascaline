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
package net.sf.pascaline.cdtinterface.launch.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.launch.internal.LocalCDILaunchDelegate;
import org.eclipse.cdt.launch.internal.ui.LaunchMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

/**
 * Copied from CDT {@link LocalCDILaunchDelegate} class. We need to show an external console that displays program output and recives input. We do this
 * because the standard implementation under windows:
 * 	- doesn't flush the output (the output is not shown until the buffer is full)
 *  - when using write instead of writeln output is mixed with gdb output (see MIParser). 
 *  
 * @author Francisco Gortázar
 *
 */
public class FreePascalLaunchDelegate extends LocalCDILaunchDelegate {
	
	@Override
	public void launch(ILaunchConfiguration config, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if ( monitor == null ) {
			monitor = new NullProgressMonitor();
		}
		if ( mode.equals( ILaunchManager.RUN_MODE ) ) {
			runLocalApplication( config, launch, monitor );
		}
		if ( mode.equals( ILaunchManager.DEBUG_MODE ) ) {
			super.launch( config, mode, launch, monitor );
		}
	}

	private void runLocalApplication( ILaunchConfiguration config, ILaunch launch, IProgressMonitor monitor ) throws CoreException {
		monitor.beginTask( LaunchMessages.getString( "LocalCDILaunchDelegate.0" ), 10 ); //$NON-NLS-1$
		if ( monitor.isCanceled() ) {
			return;
		}
		monitor.worked( 1 );
		try {
			IPath exePath = verifyProgramPath( config );
			File wd = getWorkingDirectory( config );
			if ( wd == null ) {
				wd = new File( System.getProperty( "user.home", "." ) ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			String arguments[] = getProgramArgumentsArray( config );
			ArrayList command;
			if(Platform.getOS().equals(Platform.OS_WIN32)) {
				// We launch the program within a cmd in Windows
				command = new ArrayList( 3 + arguments.length );
				command.add("cmd");
				command.add("/c");
			} else {
				command = new ArrayList( 1 + arguments.length );

			}
			if(Platform.getOS().equals(Platform.OS_WIN32)) {
				Path projectPath = new Path(wd.getAbsolutePath());
				int numCommonSegments = exePath.matchingFirstSegments(projectPath);
				exePath = exePath.removeFirstSegments(numCommonSegments);
				command.add("\"start " + exePath.toOSString() + "\"");
			} else {
				command.add( exePath.toOSString() );
			}
			command.addAll( Arrays.asList( arguments ) );
			String[] commandArray = (String[])command.toArray( new String[command.size()] );
			boolean usePty = config.getAttribute( ICDTLaunchConfigurationConstants.ATTR_USE_TERMINAL, ICDTLaunchConfigurationConstants.USE_TERMINAL_DEFAULT );
			monitor.worked( 2 );
			Process process = exec( commandArray, getEnvironment( config ), wd, usePty );
			monitor.worked( 6 );
			DebugPlugin.newProcess( launch, process, renderProcessLabel( commandArray[0] ) );
		}
		finally {
			monitor.done();
		}		
	}
	
	@Override
	protected Process exec(String[] cmdLine, String[] environ,
			File workingDirectory, boolean usePty) throws CoreException {
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			return DebugPlugin.exec(cmdLine, workingDirectory, environ);
		} else {
			return super.exec(cmdLine, environ, workingDirectory, usePty);
		}
		
	}
	
}
