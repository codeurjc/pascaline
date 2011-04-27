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
package es.sidelab.pascaline.internal.ui.editor;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

public class PascalScanner extends RuleBasedScanner {

	public static class BiggerStringRule implements IRule {

		private String[] words;

		private IToken token;

		public BiggerStringRule(String[] words, IToken token) {
			this.words = words;
			this.token = token;
		}

		public IToken evaluate(ICharacterScanner scanner) {

			List<String> candidateWords = new LinkedList<String>();
			Collections.addAll(candidateWords, words);

			String biggerRecognizedWord = null;

			int index = 0;
			int c = -1;
			do {
				c = scanner.read();
				for (Iterator<String> it = candidateWords.iterator(); it
						.hasNext();) {
					String word = it.next();
					if (word.length() == index) {
						if (isDelimiter((char) c) || c == ICharacterScanner.EOF) {
							biggerRecognizedWord = word;
						}
						it.remove();
					} else {
						if (word.charAt(index) != c) {
							it.remove();
						}
					}
				}
				index++;
			} while (candidateWords.size() > 0);

			scanner.unread();

			if (biggerRecognizedWord != null) {
				for (int i = 0; i < index - 1 - biggerRecognizedWord.length(); i++) {
					scanner.unread();
				}
				return token;
			} else {
				for (int i = 0; i < index - 1; i++) {
					scanner.unread();
				}
				return Token.UNDEFINED;
			}
		}

		protected boolean isDelimiter(char c) {
			return true;
		}
	}

	public static class ReservedWordRule extends BiggerStringRule {

		public ReservedWordRule(String[] words, IToken token) {
			super(words, token);
		}

		@Override
		protected boolean isDelimiter(char c) {
			return !Character.isLetter(c) && !Character.isDigit(c) && (c != '_');
		}

	}

	private static final String[] RESERVED_WORDS = new String[] { "and", "asm",
			"array", "as", "begin", "break", 
			"case", "class", "const", "continue", "constructor", "destructor",
			"dispose",
			"div", "do", "downto", "else", "end", "except",  
			"exit", "exports", "file", "finalization", "finally", "for",
			"function", "goto", "if", "implementation", 
			"initialization", "in", "inherited",
			"inline", "interface", "is", "label", "library", "mod", "new", "nil", "not",
			"object", "of", "on", "out", "operator",  
			"or", "override", "packed", "procedure", "program", "record",
			"private", "property", "public", "protected",
			"raise", "reintroduce", "repeat", 
			"self", "set", "shl", "shr", "string", "then", "to", "type",
			"try", "threadvar",
			"unit", "until", "uses", "var", "virtual", "while", "with", "xor", "AND",
			"AS", "ASM", "ARRAY", "BEGIN", "BREAK", 
			"CASE", "CLASS", "CONST", "CONSTRUCTOR", "CONTINUE",
			"DESTRUCTOR", "DISPOSE", "DIV", "DO", "DOWNTO", "ELSE", "END", 
			"EXCEPT", "EXIT", "EXPORTS", "FINALIZATION", "FINALLY",
			"FILE", "FOR", "FUNCTION", "GOTO", "IF", "IMPLEMENTATION", 
			"INITIALIZATION", "IN",
			"INHERITED", "INLINE", "INTERFACE", "IS", "LABEL", "LIBRARY", "MOD", "NEW",
			"NIL", "NOT", "OBJECT", "OF", "ON", "OUT", "OPERATOR", 
			"OR", "OVERRIDE", "PACKED", "PROCEDURE",
			"PRIVATE", "PROPERTY", "PUBLIC", "PROTECTED",
			"PROGRAM", "RAISE", "RECORD", "REINTRODUCE", "REPEAT", 
			"SELF", "SET", "SHL", "SHR", "STRING",
			"THEN", "TRY", "THREADVAR", "TO", "TYPE", "UNIT", "UNTIL", "USES", "VAR", "VIRTUAL", "WHILE",
			"WITH", "XOR", };

	private static final String[] PREDEFINED_IDENTIFIERS = new String[] {
			"char", "integer", "real", "boolean", "true", "false", "CHAR",
			"INTEGER", "REAL", "BOOLEAN", "TRUE", "FALSE" };

	private static final String[] OPERATORS = new String[] { "*", "/", "-",
			"+", "=", ":", ",", ";", "<", ">", "@", "$" };

	private static final String[] BRACKETS = new String[] { "(", ")", "[", "]",
			"{", "}" };

	public static class IdentifiersDetector implements IWordDetector {
		public boolean isWordPart(char c) {
			return Character.isLetterOrDigit(c) || c == '_';
		}

		public boolean isWordStart(char c) {
			return Character.isLetter(c) || (c == '_');
		}
	}

	public PascalScanner(ColorManager manager) {

		IToken comment = new Token(new TextAttribute(manager
				.getColor(IPascalColorConstants.COMMENT)));
		IToken reserved = new Token(new TextAttribute(manager
				.getColor(IPascalColorConstants.RESERVED_WORD), null,
				SWT.BOLD));
		IToken string = new Token(new TextAttribute(manager
				.getColor(IPascalColorConstants.STRING)));
		IToken numbers = new Token(new TextAttribute(manager
				.getColor(IPascalColorConstants.DEFAULT)));
		IToken identifiers = new Token(new TextAttribute(manager
				.getColor(IPascalColorConstants.DEFAULT)));
		IToken operators = new Token(new TextAttribute(manager
				.getColor(IPascalColorConstants.OPERATOR)));

		IRule[] rules = new IRule[] {				
				new SingleLineRule("'", "'", string),
 				new SingleLineRule("{$", "}", comment),
				new MultiLineRule("/*", "*/)", comment),
				new MultiLineRule("(*", "*)", comment),
				new MultiLineRule("{", "}", comment),
				new WhitespaceRule(new PascalWhitespaceDetector()),
				new ReservedWordRule(RESERVED_WORDS, reserved),
				new ReservedWordRule(PREDEFINED_IDENTIFIERS, reserved),
				new WordRule(new IdentifiersDetector(), identifiers),
				new NumberRule(numbers),
				new BiggerStringRule(OPERATORS, operators),
				new BiggerStringRule(BRACKETS, operators) };

		setRules(rules);
	}

}
