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

import org.eclipse.cdt.debug.mi.core.command.MIGDBSetNewConsole;
import org.eclipse.cdt.debug.mi.core.command.factories.win32.StandardWinCommandFactory;

public class FreePascalStandardWinCommandFactory extends StandardWinCommandFactory {
	
	@Override
	public MIGDBSetNewConsole createMIGDBSetNewConsole() {
		// Override the overriden method by Cygwin in StandardWinCommandFactory to revert to the default implementation (open new console)
		return new MIGDBSetNewConsole(getMIVersion(), "on");
	}

}
