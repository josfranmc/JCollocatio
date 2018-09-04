package org.josfranmc.collocatio.service;

/**
 * Enumera el tipo de consultas que pueden realizarse sobre las bases de datos que almacenane colocaciones.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public enum QueryType {
	/**
	 * Consultar todas las colocaciones.
	 */
	ALL,
	/**
	 * Consultar las colocaciones que contengan una serie de palabras determinadas.
	 */
	BY_WORDS,
	/**
	 * Consultar las colocaciones cuya primera palabra sea igual a alguna de las palabras dadas por una lista.
	 */
	START_WITH,
	/**
	 * Consultar las colocaciones cuya segunda palabra sea igual a alguna de las palabras dadas por una lista.
	 */
	END_WITH,
	/**
	 * Consultar las colocaciones con mejor valor de información mutua.
	 */
	BEST_MI
}
