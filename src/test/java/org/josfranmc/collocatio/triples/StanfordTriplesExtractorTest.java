package org.josfranmc.collocatio.triples;

import java.util.Properties;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase StanfordTriplesExtractor
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class StanfordTriplesExtractorTest {

	/**
	 * Si no se indica ruta de los archivos a procesar se debe obtener IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void filesPathToProcessDefaultTest() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		ste.extractTriples();
	}
	
	/**
	 * Si la ruta de los archivos a procesar es null se debe obtener IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void filesPathToProcessNullTest() {
		Properties p = new Properties();
		p.setProperty("textFiles", "");
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor(p);
		ste.extractTriples();
	}
	
	/**
	 * Si la ruta de los archivos a procesar no es válida se debe obtener IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void filesPathToProcessWrongTest() {
		Properties p = new Properties();
		p.setProperty("textFiles", "C:\\ssdds\\gh\\ty");
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		ste.setConfiguration(p);
		ste.extractTriples();
	}
}
