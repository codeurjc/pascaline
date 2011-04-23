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
package net.sf.pascaline.internal.errorparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.pascaline.core.PascalineCorePlugin;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.core.resources.IFile;

public class FreePascalErrorParser implements IErrorParser {

	private IFile currentFile;
	
	@Override
	public boolean processLine(String line, ErrorParserManager epm) {
		// First we test if the problem is that there is no program
		Matcher m = NO_SOURCE_FILE_TO_COMPILE.matcher(line);
		if(m.matches()) {
			epm.generateMarker(epm.getProject(), 0, "No main program found in the project", IMarkerGenerator.SEVERITY_ERROR_BUILD, null);
		} else {
			m = COMPILING.matcher(line);
			if(m.matches()) {
				currentFile = epm.findFileName(m.group(1));
				return false;
			}
			
			m = ERROR_LINE.matcher(line);
			boolean hasLineInfo = m.find();
			if (hasLineInfo && !line.contains(COMPILATION_ABORTED)) {
				String filename = m.group(1);
				IFile file = epm.findFileName(filename);
				currentFile = file;
				int lineNumber = Integer.parseInt(m.group(2).trim());
				int column = Integer.parseInt(m.group(3).trim());
				if (line.contains("Warning") || line.contains("Note")) {
					CompInfo warning = new CompInfo(lineNumber, column - 1, false);
					m = WARNING_DESC.matcher(line);
					m.find();
					warning.setDescription(m.group(2).trim());
					errors.add(warning);
					epm.generateMarker(file, lineNumber, warning.getDescription(), IMarkerGenerator.SEVERITY_WARNING, null);
				} else {
					CompInfo error = new CompInfo(lineNumber, column - 1, true);
					m = ERROR_DESC.matcher(line);
					m.find();
					error.setDescription(m.group(2).trim());
					errors.add(error);
					epm.generateMarker(file, lineNumber, error.getDescription(), IMarkerGenerator.SEVERITY_ERROR_RESOURCE,
							null);
				}

			} else if ((m = FATAL_ERROR.matcher(line)).find() && !line.contains(COMPILATION_ABORTED)) {
				CompInfo error = new CompInfo(0, 0, true);
				error.setDescription(m.group(1).trim());
				errors.add(error);
				epm.generateMarker(currentFile, 0, error.getDescription(), IMarkerGenerator.SEVERITY_ERROR_RESOURCE, null);
			}
		}
		
		
		return false;
	}
	
	private static final Pattern ERROR_DESC = Pattern
			.compile("(Error:|Fatal:)\\s*(.*)\\s*");
	private static final Pattern ERROR_LINE = Pattern
			.compile("(.*\\.pas)\\((\\d+),(\\d+)\\)");
	private static final Pattern WARNING_DESC = Pattern
			.compile("(Warning|Note):\\s*(.*)\\s*");
	private static final Pattern FATAL_ERROR = Pattern
			.compile("Fatal:\\s*(.*)\\s*");
	private static final Pattern NO_SOURCE_FILE_TO_COMPILE = Pattern.compile("Fatal: No source file name in command line");
	private static final Pattern COMPILING = Pattern.compile("Compiling\\s*(.*)");
	private static final String COMPILATION_ABORTED = "Compilation aborted";

	private List<CompInfo> errors = new ArrayList<CompInfo>();

	public List<CompInfo> getErrors() {
		return errors;
	}


}
