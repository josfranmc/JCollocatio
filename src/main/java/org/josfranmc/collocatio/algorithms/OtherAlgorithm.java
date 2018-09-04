package org.josfranmc.collocatio.algorithms;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.algorithms.ICollocationAlgorithm;

/**
 * Clase de ejemplo
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class OtherAlgorithm implements ICollocationAlgorithm {

	private static final Logger log = Logger.getLogger(OtherAlgorithm.class);
	
	@Override
	public void findCollocations() {
		log.info("Usando otro algoritmo todavía por definir");
	}

}
