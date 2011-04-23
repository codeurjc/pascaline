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
package net.sf.pascaline.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class PascalPartitionScanner extends RuleBasedPartitionScanner {

	class ProgramRule extends SingleLineRule {

		/**
		 * Constructor for Vars.
		 * @param startSequence
		 * @param endSequence
		 * @param token
		 */
		public ProgramRule(IToken token) {
			super("PROGRAM", ";", token);
		}

		/**
		 * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(ICharacterScanner, char[], boolean)
		 */
		protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
			return super.sequenceDetected(scanner, sequence, eofAllowed);
		}

	}

	class ProcedureRule extends SingleLineRule {

		/**
		 * Constructor for Vars.
		 * @param startSequence
		 * @param endSequence
		 * @param token
		 */
		public ProcedureRule(IToken token) {
			super("PROCEDURE", ";", token);
		}

		/**
		 * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(ICharacterScanner, char[], boolean)
		 */
		protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
			return super.sequenceDetected(scanner, sequence, eofAllowed);
		}

	}

	class VarsRule extends SingleLineRule {

		/**
		 * Constructor for Vars.
		 * @param startSequence
		 * @param endSequence
		 * @param token
		 */
		public VarsRule(IToken token) {
			super("VAR", ";", token);
		}

		/**
		 * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(ICharacterScanner, char[], boolean)
		 */
		protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
			return super.sequenceDetected(scanner, sequence, eofAllowed);
		}

	}

	class ConstsRule extends SingleLineRule {

		/**
		 * Constructor for Vars.
		 * @param startSequence
		 * @param endSequence
		 * @param token
		 */
		public ConstsRule(IToken token) {
			super("CONST", ";", token);
		}

		/**
		 * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(ICharacterScanner, char[], boolean)
		 */
		protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
			return super.sequenceDetected(scanner, sequence, eofAllowed);
		}

	}
	class ModuleRule extends MultiLineRule {

		public ModuleRule(IToken token) {
			super("BEGIN", "END.", token);
		}
		protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
			int c = scanner.read();
			if (sequence[0] == 'P') {
				if (c == 'R' || c == 'O' || c == 'G' || c == 'A' || c == 'M') {
					// processing instruction - abort
					scanner.unread();
					return false;
				}
				if (c == '('
					|| c == ')'
					|| c == '/'
					|| c == '}'
					|| c == '{'
					|| c == '*') {
					scanner.unread();
					// comment - abort
					return false;
				}
			} else if (sequence[0] == '.') {
				scanner.unread();
			}
			return super.sequenceDetected(scanner, sequence, eofAllowed);
		}
	}
	public final static String PAS_DEFAULT = "__pas_default";
	public final static String PAS_COMMENT = "__pas_comment";
	public final static String PAS_VARS = "__pas_vars";
	public final static String PAS_CONSTS = "__pas_consts";
	public final static String PAS_MOD = "__pas_mod";
	public final static String PAS_PROG = "__pas_prog";
	public final static String PAS_PROC = "__pas_proc";

	public PascalPartitionScanner() {

		List<PatternRule> rules = new ArrayList<PatternRule>();

		IToken pasComment = new Token(PAS_COMMENT);
		IToken pasModule = new Token(PAS_MOD);
		IToken pasConsts = new Token(PAS_CONSTS);
		IToken pasVars = new Token(PAS_VARS);
		IToken pasProg = new Token(PAS_PROG);
		IToken pasProc = new Token(PAS_PROG);
		

		rules.add(new MultiLineRule("(*", "*)", pasComment));
		rules.add(new MultiLineRule("/*", "*/", pasComment));
		rules.add(new MultiLineRule("{", "}", pasComment));
		rules.add(new ProgramRule(pasProg));
		rules.add(new ProcedureRule(pasProc));
		rules.add(new VarsRule(pasVars));
		rules.add(new ConstsRule(pasConsts));
		rules.add(new ModuleRule(pasModule));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
