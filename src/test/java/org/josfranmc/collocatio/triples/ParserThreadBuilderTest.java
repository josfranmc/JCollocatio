package org.josfranmc.collocatio.triples;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * Clase que implementa los test para probar los métodos de la clase ParserThreadBuilder
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ParserThreadBuilderTest {

	private static LexicalizedParser lp;
	private static TreebankLanguagePack tlp;
	private static GrammaticalStructureFactory gsf;
	private static TriplesCollection tc;
	private static List<? extends HasWord> sentence;
	
	
	/**
	 * Método ejecutado una vez antes de la ejecución de todos los tests
	 */
	@BeforeClass 
	public static void setUp() {
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		tlp = lp.getOp().langpack();
	    gsf = tlp.grammaticalStructureFactory();
	    tc = new TriplesCollection();
	    sentence = new ArrayList<>();
	}
	
	/**
	 * Si LexicalizedParser es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenLexicalizedParseWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setLexicalizedParser(null);
		ptb.build();
	}
	
	/**
	 * Si GrammaticalStructureFactory es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenGrammaticalStructureFactoryWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setLexicalizedParser(lp);
		ptb.setGrammaticalStructureFactory(null);
		ptb.build();
	}
	
	/**
	 * Si TriplesCollecion es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenTriplesCollecionWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setLexicalizedParser(lp);
		ptb.setGrammaticalStructureFactory(gsf);
		ptb.setTriplesCollection(null);
		ptb.build();
	}
	
	/**
	 * Si sentence es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenSentenceWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setLexicalizedParser(lp);
		ptb.setGrammaticalStructureFactory(gsf);
		ptb.setTriplesCollection(tc);
		ptb.setSentence(null);
		ptb.build();
	}	
	
	/**
	 * Si book es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenBookThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setLexicalizedParser(lp);
		ptb.setGrammaticalStructureFactory(gsf);
		ptb.setTriplesCollection(tc);
		ptb.setSentence(sentence);
		ptb.setBook(null);
		ptb.build();
	}	
}
