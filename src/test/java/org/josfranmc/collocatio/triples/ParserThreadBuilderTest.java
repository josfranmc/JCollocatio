package org.josfranmc.collocatio.triples;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase ParserThreadBuilder
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
class ParserThreadBuilderTest {
	
	/**
	 * Si parser model es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenParserModelWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setParser(null);
		ptb.build();
	}
	
	/**
	 * Si tagger model es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenTaggerModelWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setTagger(null);
		ptb.build();
	}
	
	/**
	 * Si TriplesCollecion es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenTriplesCollecionWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setTriplesCollection(null);
		ptb.build();
	}
	
	/**
	 * Si file es null, entonces debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenFileWhenNullThenIllegalArgumentException() {
		ParserThreadBuilder ptb = new ParserThreadBuilder();
		ptb.setFile(null);
		ptb.build();
	}
}
