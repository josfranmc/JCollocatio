package org.josfranmc.collocatio.algorithms;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.josfranmc.collocatio.triples.StanfordTriplesExtractor;
import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CollocationAlgorithmBuilder
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class CollocationAlgorithmBuilderTest {
	
	/**
	 * Si el objeto que encapsula los parámetros de configuración del algoritmo es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenPropertiesWhenNullThenThrowIllegalArgumentException() {
		Properties properties = null;
		new CollocationAlgorithmBuilder().setConfiguration(properties).build();
	}

	/**
	 * Si el parámetro que indica el tipo de algoritmo es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenAlgorithmTypeWhenNullThenThrowIllegalArgumentException() {
		Properties properties = new Properties();
		properties.put("type", "");
		new CollocationAlgorithmBuilder().setConfiguration(properties).build();
	}
	
	/**
	 * Si el parámetro que indica el tipo de algoritmo es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenAlgorithmTypeWhenWrongValueThenThrowIllegalArgumentException() {
		Properties properties = new Properties();
		properties.put("type", "BadBad");
		new CollocationAlgorithmBuilder().setConfiguration(properties).build();
	}	
	
	/**
	 * Si el parámetro filesPathToProcess es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenFilesPathToProcessIsNullThenThrowIllegalArgumentException() {
		Properties p = new Properties();
		p.setProperty("type", "MUTUAL_INFORMATION");
		new CollocationAlgorithmBuilder().setConfiguration(p).build();
	}
	
	/**
	 * Si el parámetro FilesPathToProcess es una ruta inválida, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenFilesPathToProcessIsEmptyThenThrowIllegalArgumentException() {
		Properties p = new Properties();
		p.setProperty("type", "MUTUAL_INFORMATION");
		p.setProperty("textFiles", "badbad");
		new CollocationAlgorithmBuilder().setConfiguration(p).build();
	}
	
	/**
	 * Si el modelo (parser) utilizado para el algoritmo MutualInformation es null, entonces se debe cargar el modelo por defecto
	 */
	@Test
	public void givenMutualInformationWhenParserModelIsNullThenLoadDefault() {
		Properties p = new Properties();
		p.setProperty("type", "MUTUAL_INFORMATION");
		p.setProperty("textFiles", "books");
		
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(p).build();
		assertEquals("Wrong parser model", StanfordTriplesExtractor.MODEL_PARSER_DEPENDENCIES, mia.getParserModel());
	}
	
	/**
	 * Si el tagger utilizado para el algoritmo MutualInformation es null, entonces se debe cargar el por defecto
	 */
	@Test
	public void givenMutualInformationWhenTaggerrModelIsNullThenLoadDefault() {
		Properties p = new Properties();
		p.setProperty("type", "MUTUAL_INFORMATION");
		p.setProperty("textFiles", "books");
		
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(p).build();
		assertEquals("Wrong tagger model", StanfordTriplesExtractor.MODEL_TAGGER_DEPENDENCIES, mia.getTaggerModel());
	}
	
	/**
	 * Si el tagger utilizado para el algoritmo MutualInformation es null, entonces se debe cargar el por defecto
	 */
	@Test
	public void givenMutualInformationWhenAdjustedFrequencyIsNullThenLoadDefault() {
		Properties p = new Properties();
		p.setProperty("type", "MUTUAL_INFORMATION");
		p.setProperty("textFiles", "books");
		
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(p).build();
		assertEquals("Wrong adjusted frequency", 1, mia.getAdjustedFrequency(), 0.001);
	}
	
//	/**
//	 * Si se indica un nombre para una nueva base de datos se le debe añadir el prefijo col_
//	 */
//	@Test(expected=IllegalArgumentException.class)
//	public void givenMutualInformationWhenNewDbThenAddPrefix() {
//		ParamsAlgorithm params = new ParamsAlgorithm();
//		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
//		params.setNewDataBase("ejemplo");
//		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(params).build();
//		assertEquals("Nombre de base de datos incorrecto", "col_ejemplo", mia.getDataBaseName());
//	}
//	
//	/**
//	 * Si se pasa null como nombre para una nueva base de datos se debe devolver null al consultarlo
//	 */
//	@Test(expected=IllegalArgumentException.class)
//	public void givenMutualInformationWhenNoNewDbThenNull() {
//		ParamsAlgorithm params = new ParamsAlgorithm();
//		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
//		params.setNewDataBase(null);
//		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(params).build();
//		assertNull("No se ha indicado nueva db, el nombre debería ser null", mia.getDataBaseName());
//	}
//	
//	/**
//	 * Si se pasa una cadena vacía como nombre para una nueva base de datos se debe devolver null al consultarlo
//	 */
//	@Test(expected=IllegalArgumentException.class)
//	public void givenMutualInformationWhenNewDbEmptyThenNull() {
//		ParamsAlgorithm params = new ParamsAlgorithm();
//		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
//		params.setNewDataBase("");
//		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(params).build();
//		assertNull("Nombre para nueva db vacío, el nombre debería ser null", mia.getDataBaseName());
//	}
}
