//PODEMOS PRESCINDIR DE ESTA CLASE

package org.josfranmc.collocatio.algorithms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.triples.TriplesCollection;

/**
 * Encapsulates the algorithm that describes how obtain collocations by calculating the mutual information value.
 * The algorithm follows two main steps:
 * <ul>
 * <li>First, getting a collection of triples. This triples are defined through the <code>TriplesCollection</code> class.</li>
 * <li>Second, the collection obtained is used to calculate the mutual information value of each one.</li>
 * </ul>
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see ICollocationAlgorithm
 * @see TriplesCollection
 */
public abstract class AbstractMutualInformationAlgorithm implements ICollocationAlgorithm {
	
	private static final Logger log = Logger.getLogger(AbstractMutualInformationAlgorithm.class);
	
	/**
	 * Runs an algorithm, establishing the steps to follow and the order of execution of the same.
	 */
	@Override
	public void findCollocations() {
		log.info("USING MUTUAL INFORMATION ALGORITHM " + getCurrentTime());
		TriplesCollection triples = extractTriples();
		calculateMutualInformation(triples);
		log.info("END MUTUAL INFORMATION ALGORITHM " + getCurrentTime());
	}
	
	/**
	 * Obtains triples. It is the first step of the algorithm.
	 */
	protected abstract TriplesCollection extractTriples();
	
	/**
	 * Calculates mutual information value for each triple. It is the second step of the algorithm.
	 */
	protected abstract void calculateMutualInformation(TriplesCollection triplesCollection);
	
	
	private String getCurrentTime() {
		return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date());
	}
}
