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
package es.sidelab.pascaline.managedbuilder.fpc;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;



import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import es.sidelab.pascaline.cdtinterface.launch.FpcPathDiscoverer;
import es.sidelab.pascaline.managedbuilder.core.ManagedBuilderCorePlugin;

public class FreePascalEnvironmentVariableSupplier implements
		IConfigurationEnvironmentVariableSupplier {

	IBuildEnvironmentVariable path;
	
	public FreePascalEnvironmentVariableSupplier() {
		initializePathVariable();
	}

	private void initializePathVariable() {
		IPath fpcPath = FpcPathDiscoverer.getFPCPath();
	
		if(fpcPath != null) {
			path = new FreePascalEnvironmentVariable("PATH", fpcPath.toOSString(), IBuildEnvironmentVariable.ENVVAR_APPEND);
		}
		
	}
	
	@Override
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		// We always call initializePathVariable to protect ourselves from Eclipse changes (ie: the user share the workspace among different Eclipse installations). 
		initializePathVariable();
		if (path != null && variableName.equals(path.getName()))
			return path;
		else
			return null;
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return path != null
		? new IBuildEnvironmentVariable[] { path }
		: new IBuildEnvironmentVariable[0];
	}

	private static class FreePascalEnvironmentVariable implements IBuildEnvironmentVariable {
		private final String name;
		private final String value;
		private final int operation;

		public FreePascalEnvironmentVariable(String name, String value, int op) {
			this.name = name;
			this.value = value;
			this.operation = op;
		}
		
		@Override
		public String getDelimiter() {
			return ";";
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getOperation() {
			return operation;
		}

		@Override
		public String getValue() {
			return value;
		}
		
	}
}
