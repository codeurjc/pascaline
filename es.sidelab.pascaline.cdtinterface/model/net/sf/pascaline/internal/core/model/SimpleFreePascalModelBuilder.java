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
package net.sf.pascaline.internal.core.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.model.Parent;
import org.eclipse.cdt.internal.core.model.TranslationUnit;

/**
 * @author patxi
 *
 */
public class SimpleFreePascalModelBuilder implements IFreePascalModelBuilder {

	private TranslationUnit translationUnit;

	/* (non-Javadoc)
	 * @see net.sf.pascaline.internal.core.model.IFreePascalModelBuilder#setTranslationUnit(org.eclipse.cdt.core.model.ITranslationUnit)
	 */
	@Override
	public void setTranslationUnit(ITranslationUnit tu) {
		// There is a bug in the corresponding Photran code, because it makes
		// instanceof against
		// ITranslationUnit again, when it should be TranslationUnit
		if (!(tu instanceof TranslationUnit))
			throw new Error("Unexpected subclass of ITranslationUnit");
		
		this.translationUnit = (TranslationUnit) tu;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.model.IContributedModelBuilder#parse(boolean)
	 */
	@Override
	public void parse(boolean quickParseMode) throws Exception {
		
		BufferedReader br = new BufferedReader(new StringReader(translationUnit.getBuffer().getContents()));
		boolean wasSuccesful = true;
		
		try {
			String line;
			while ((line = br.readLine()) != null) {
				processLine(line);
			}
			if (stackElements.isEmpty()) {
				// At least we create an Empty PascalElement.Program
				PascalElement.Program program = new PascalElement.Program(translationUnit, "noname");
				((Parent) translationUnit).addChild(program);
			}
		} catch (Exception e) {
			createParseFailureNode(translationUnit, e.getMessage());
			wasSuccesful = false;
		}

		translationUnit.setIsStructureKnown(wasSuccesful);
	}

	/**
	 * @param parent
	 * @param errorMessage
	 * @throws CModelException
	 */
	private PascalElement createParseFailureNode(Parent parent, String errorMessage) throws CModelException {
		PascalElement element = new PascalElement.ErrorNode(parent, errorMessage);
		parent.addChild(element);
		return element;
	}

	private static final Pattern programPattern = Pattern.compile("program\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern unitPattern = Pattern.compile("unit\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern procedurePattern = Pattern.compile("procedure\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern functionPattern = Pattern.compile("function\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern objectPattern = Pattern.compile("(\\w+)\\s*=\\s*object", Pattern.CASE_INSENSITIVE);
	private static final Pattern endPattern = Pattern.compile("end", Pattern.CASE_INSENSITIVE);
	
	private Stack<PascalElement> stackElements = new Stack<PascalElement>();

	private Parent currentParent() {
		return stackElements.isEmpty() ? translationUnit : stackElements.peek();
	}
	
	/**
	 * @param line
	 * @throws CModelException
	 */
	private void processLine(String line) throws CModelException {
		Matcher matcher = programPattern.matcher(line);
		if (matcher.find()) {
			String name = matcher.group(1);
			PascalElement.Program program = new PascalElement.Program(currentParent(), name);
			// stackElements.peek().addChild(program);
			currentParent().addChild(program);
			stackElements.add(program);
		} else if(line.contains("program")) {
			// We lack the name of the program. Create a Program node with name <noname>.
			PascalElement.Program program = new PascalElement.Program(currentParent(), "<noname>");
			currentParent().addChild(program);
			stackElements.add(program);
		}
		
		matcher = unitPattern.matcher(line);
		if (matcher.find()) {
			String name = matcher.group(1);
			PascalElement.Unit unit = new PascalElement.Unit(currentParent(), name);
			// stackElements.peek().addChild(unit);
			currentParent().addChild(unit);
			stackElements.add(unit);
		} else if(line.contains("unit")) {
			// We lack the name of the unit. Create a Unit node with name <noname>
			PascalElement.Unit unit = new PascalElement.Unit(currentParent(), "<noname>");
			currentParent().addChild(unit);
			stackElements.add(unit);
		}
		
		matcher = procedurePattern.matcher(line);
		if (matcher.find()) {
			String name = matcher.group(1);
			PascalElement.Procedure procedure = new PascalElement.Procedure(currentParent(), name);
			currentParent().addChild(procedure);
		}
		
		matcher = functionPattern.matcher(line);
		if (matcher.find()) {
			String name = matcher.group(1);
			PascalElement.Function function = new PascalElement.Function(currentParent(), name);
			currentParent().addChild(function);
		}
		
		matcher = objectPattern.matcher(line);
		if (matcher.find()) {
			String name = matcher.group(1);
			PascalElement.ObjectType object = new PascalElement.ObjectType(currentParent(), name);
			currentParent().addChild(object);
			stackElements.push(object);
		}
		
		matcher = endPattern.matcher(line);
		if (matcher.find()) {
			if (currentParent() instanceof PascalElement.ObjectType) {
				stackElements.pop();
			}
		}
	}

}
