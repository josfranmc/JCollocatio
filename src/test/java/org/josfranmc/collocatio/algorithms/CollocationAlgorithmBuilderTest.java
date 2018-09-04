package org.josfranmc.collocatio.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CollocationAlgorithmBuilder
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class CollocationAlgorithmBuilderTest {

	/**
	 * Si no se asignan parámetros antes de llamar a build, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testParamsAlgorithmBeforeBuild() {
		new CollocationAlgorithmBuilder().build();
	}
	
	/**
	 * Si el objeto que encapsula los parámetros de configuración del algoritmo es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenParamsAlgorithmWhenNullThenThrowIllegalArgumentException() {
		ParamsAlgorithm params = null;
		new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
	}

	/**
	 * Si el parámetro que indica el tipo de algoritmo es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenAlgorithmTypeWhenNullThenThrowIllegalArgumentException() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(null);
		new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
	}
	
	/**
	 * Si el parámetro TotalThreads es menor que uno, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenTotalThreadsWhenLessThanOneThenThrowIllegalArgumentException() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setTotalThreads(0);
		new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
	}	
	
	/**
	 * Si el parámetro TextsPathToProcess para el algoritmo MutualInformation es null, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenTextsPathToProcessIsNullThenThrowIllegalArgumentException() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
		params.setTextsPathToProcess(null);
		new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
	}
	
	/**
	 * Si el parámetro TextsPathToProcess para el algoritmo MutualInformation es una cadena vacía, entonces se debe obtener una excepción del tipo IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenTextsPathToProcessIsEmptyThenThrowIllegalArgumentException() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
		params.setTextsPathToProcess("");
		new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
	}
	
	/**
	 * Si el modelo (parser) utilizado para el algoritmo MutualInformation es null, entonces se debe cargar el modelo para el idioma inglés
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenModelIsNullThenLoadEnglishModel() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
		params.setModel(null);
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
		assertEquals("No se ha cargado english model por defecto", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", mia.getModel());
	}
	
	/**
	 * Si se indica un nombre para una nueva base de datos se le debe añadir el prefijo col_
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenNewDbThenAddPrefix() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
		params.setNewDataBase("ejemplo");
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
		assertEquals("Nombre de base de datos incorrecto", "col_ejemplo", mia.getDataBaseName());
	}
	
	/**
	 * Si se pasa null como nombre para una nueva base de datos se debe devolver null al consultarlo
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenNoNewDbThenNull() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
		params.setNewDataBase(null);
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
		assertNull("No se ha indicado nueva db, el nombre debería ser null", mia.getDataBaseName());
	}
	
	/**
	 * Si se pasa una cadena vacía como nombre para una nueva base de datos se debe devolver null al consultarlo
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenMutualInformationWhenNewDbEmptyThenNull() {
		ParamsAlgorithm params = new ParamsAlgorithm();
		params.setAlgorithmType(AlgorithmType.MUTUAL_INFORMATION);
		params.setNewDataBase("");
		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
		assertNull("Nombre para nueva db vacío, el nombre debería ser null", mia.getDataBaseName());
	}
}
