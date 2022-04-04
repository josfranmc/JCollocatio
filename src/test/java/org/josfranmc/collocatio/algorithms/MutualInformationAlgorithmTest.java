package org.josfranmc.collocatio.algorithms;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.josfranmc.collocatio.algorithms.CollocationAlgorithmBuilder;
import org.josfranmc.collocatio.algorithms.MutualInformationAlgorithm;
import org.josfranmc.collocatio.triples.StanfordTriplesExtractor;
import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleEvents;
import org.josfranmc.collocatio.triples.TriplesCollection;
import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase MutualInformationAlgorithm
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class MutualInformationAlgorithmTest {

	@Test
	public void createMutualInformationAlgorithmTest() {
		Properties config = new Properties();
		config.setProperty("type", "MUTUAL_INFORMATION");
		config.setProperty("textFiles", "books");
		config.setProperty("adjustedFrequency", "0.5");

		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(config).build();
		
		assertEquals("Wrong files path to process", "books", mia.getFilesFolderToParser());
		assertEquals("Wrong totalThreads property", Runtime.getRuntime().availableProcessors()-1, mia.getTotalThreads());
		assertEquals("Wrong parser model", StanfordTriplesExtractor.MODEL_PARSER_DEPENDENCIES, mia.getParserModel());
		assertEquals("Wrong tagger model", StanfordTriplesExtractor.MODEL_TAGGER_DEPENDENCIES, mia.getTaggerModel());
		assertEquals("Wrong adjusted frequency parameter", 0.5, mia.getAdjustedFrequency(), 0.001);
	}
	
	@Test
	public void executeMutualInformationAlgorithmTest() {
		Properties config = new Properties();
		config.setProperty("type", "MUTUAL_INFORMATION");
		config.setProperty("textFiles", "books-little");
		config.setProperty("adjustedFrequency", "1");

		MutualInformationAlgorithm mia = (MutualInformationAlgorithm) new CollocationAlgorithmBuilder().setConfiguration(config).build();
		
		assertEquals("Wrong files path to process", "books-little", mia.getFilesFolderToParser());
		assertEquals("Wrong totalThreads property", Runtime.getRuntime().availableProcessors()-1, mia.getTotalThreads());
		assertEquals("Wrong parser model", StanfordTriplesExtractor.MODEL_PARSER_DEPENDENCIES, mia.getParserModel());
		assertEquals("Wrong tagger model", StanfordTriplesExtractor.MODEL_TAGGER_DEPENDENCIES, mia.getTaggerModel());
		assertEquals("Wrong adjusted frequency parameter", 1, mia.getAdjustedFrequency(), 0.001);
		
		TriplesCollection tc = mia.extractTriples();
		
		assertEquals("Wrong number of triples", 30, tc.getTotalTriples());
		
		int total = 0;
		for (String dependency : tc.getTriples().keySet()) {
			Map<Triple, TripleEvents> triplesByDependency = tc.getTriples().get(dependency);
			for (Triple triple : triplesByDependency.keySet()) {
				TripleEvents events = triplesByDependency.get(triple);
				total += events.getTotalEvents();
			}
		}
		assertEquals("Wrong number of triples in events", 30, total);
		
		//mia.calculateMutualInformation(tc);
	}
}
