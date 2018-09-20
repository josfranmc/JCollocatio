package org.josfranmc.collocatio.algorithms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.triples.TriplesCollection;

/**
 * Encapsula el algoritmo que describe la obtención de colocaciones mediante el cálculo del valor denominado "información mutua".
 * El algoritmo se desarrolla en dos pasos principales:
 * <ul>
 * <li>En el primero se obtiene una colección de tripletas, la cual se define mediante la clase TriplesCollection</li>
 * <li>En el segundo se utilizan éstas tripletas para calcular el valor de información mutua de cada una de ellas</li>
 * </ul>
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see ICollocationAlgorithm
 * @see TriplesCollection
 */
public abstract class AbstractMutualInformationAlgorithm implements ICollocationAlgorithm {
	
	private static final Logger log = Logger.getLogger(AbstractMutualInformationAlgorithm.class);
	
	/**
	 * Ejecuta el algoritmo, estableciendo los pasos a seguir y el orden de ejecución de los mismos.
	 */
	@Override
	public void findCollocations() {
		log.info("USANDO ALGORITMO BASADO EN INFORMACIÓN MUTUA " + getCurrentTime());
		calculateMutualInformation(extractTriples());
		log.info("FIN ALGORITMO BASADO EN INFORMACIÓN MUTUA " + getCurrentTime());
	}
	
	/**
	 * Obtención de tripletas. Es el primer paso del algoritmo.
	 */
	protected abstract TriplesCollection extractTriples();
	
	/**
	 * Cálculo del valor de información mutua de cada tripleta. Es el segundo paso del algoritmo.
	 */
	protected abstract void calculateMutualInformation(TriplesCollection triplesCollection);
	
	
	private String getCurrentTime() {
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return hourFormat.format(date);
	}
}
