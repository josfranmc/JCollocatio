package org.josfranmc.collocatio.triples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase StanfordTriplesExtractor
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class StanfordTriplesExtractorTest {

	/**
	 * Comprueba que al crear el objeto StanfordTriplesExtractor se inicializa la referencia a un objeto TriplesCollection
	 */
	@Test
	public void createTriplesCollectionOnConstructorTest() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		assertNotNull("TriplesCollection is null [1]", ste.getTriplesCollection());
		
		Properties p = new Properties();
		p.setProperty("textFiles", "");
		ste = new StanfordTriplesExtractor(p);
		assertNotNull("TriplesCollection is null [2]", ste.getTriplesCollection());
	}

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
	
//	@Test
//	public void extractTriplesTest() {
//		Properties config = new Properties();
//		config.setProperty("textFiles", "books-little");
//		
//		StanfordTriplesExtractor ste = new StanfordTriplesExtractor(config);
//		TriplesCollection tc = ste.extractTriples();
//		
//		assertEquals("Wrong number of triples", 34, tc.getTotalTriples());
//		
//		int total = 0;
//		for (Triple triple: tc.getTriples().keySet()) {
//			TripleEvents events = tc.getTriples().get(triple);
//			total += events.getTotalEvents();
//		}
//		assertEquals("Wrong number of triples in events", 34, total);
//		
//		assertEquals("Wrong files path to process", "books-little", ste.getFilesFolderToParser());
//		assertEquals("Wrong totalThreads property", Runtime.getRuntime().availableProcessors()-1, ste.getTotalThreads());
//		assertEquals("Wrong parser model", StanfordTriplesExtractor.MODEL_PARSER_DEPENDENCIES, ste.getParserModel());
//		assertEquals("Wrong tagger model", StanfordTriplesExtractor.MODEL_TAGGER_DEPENDENCIES, ste.getTaggerModel());
//	}
}
