package org.josfranmc.collocatio.corpus;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CorpusBuilder
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class CorpusBuilderTest {

	/**
	 * Comprueba que la ruta pasada no sea null
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenPathWhenNullThenIllegalArgumentException() {
		new CorpusBuilder(null);
	}

	/**
	 * Comprueba que la ruta pasada sea válida
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenPathWhenWrongThenIllegalArgumentException() {
		new CorpusBuilder("c:\\qwed\\no_existe\\dff");
	}
}