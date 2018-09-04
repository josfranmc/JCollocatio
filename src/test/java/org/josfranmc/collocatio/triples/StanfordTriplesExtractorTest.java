package org.josfranmc.collocatio.triples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase StanfordTriplesExtractor
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class StanfordTriplesExtractorTest {

	/**
	 * Comprueba si los valores por defecto de los parámetros del analizador de Stanford son los correctos
	 */
	@Test
	public void testSetStanfordOptions() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		try {
			ste.setStanfordOptions(null);
			fail("Argumento null para setStanfordOptions");
		} catch (IllegalArgumentException e) {
			
		} catch (NullPointerException e) {
			fail("Argumento null para setStanfordOptions");
		}
		
		HashMap<String, String> stanfordOptions = new HashMap<String, String>();
		stanfordOptions.put("-example1", "example");
		stanfordOptions.put("-example2", null);
		ste.setStanfordOptions(stanfordOptions);
		
		stanfordOptions = ste.getStanfordOptions();

		assertTrue("No existe opción -retainTmpSubcategories", stanfordOptions.containsKey("-retainTmpSubcategories"));
		assertTrue("No existe opción -maxLength", stanfordOptions.containsKey("-maxLength"));
		assertNull("La opción -retainTmpSubcategories no es null", ste.getStanfordOption("-retainTmpSubcategories"));
		assertEquals("La opción -maxLength no tiene el valor por defecto adecuado", StanfordTriplesExtractor.DEFAULT_SENTENCE_MAX_LENGTH, Integer.parseInt(ste.getStanfordOption("-maxLength")));
	}
	

	/**
	 * Comprueba que al crear el objeto StanfordTriplesExtractor se inicializa la referencia a un objeto TriplesCollection
	 */
	@Test
	public void testCreateTriplesCollectionOnConstructor() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		assertNotNull("TriplesCollection es null", ste.getTriplesCollection());
	}

	/**
	 * Si no se indica ruta de los archivos a procesar se debe obtener IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFilesPathToProcessDefault() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		ste.extractTriples();
	}
	
	/**
	 * Si la ruta de los archivos a procesar es null se debe obtener IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFilesPathToProcessNull() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		ste.setTextsPathToProcess(null);
		ste.extractTriples();
	}
	
	/**
	 * Si la ruta de los archivos a procesar no es válida se debe obtener IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFilesPathToProcessWrong() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		ste.setTextsPathToProcess("C:\\ssdds\\gh\\ty");
		ste.extractTriples();
	}
}
