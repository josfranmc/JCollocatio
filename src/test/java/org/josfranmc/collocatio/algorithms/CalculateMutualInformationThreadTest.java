package org.josfranmc.collocatio.algorithms;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.josfranmc.collocatio.db.ConnectionFactory;
import org.josfranmc.collocatio.service.JCollocatioService;
import org.josfranmc.collocatio.service.domain.Collocatio;
import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleEvents;
import org.josfranmc.collocatio.util.ThreadFactoryBuilder;
import org.junit.Test;

public class CalculateMutualInformationThreadTest {

	private final String DB_TEST = "col_default_test";
	
	private final String DEPENDENCY_TEST_1 = "det";
	
	private final String DEPENDENCY_TEST_2 = "nsubj";

	
	/**
	 * Hacemos los calculos suponiendo que hemos obtenido un total de 34 triples, de diferentes tipos de dependencias
	 */
	private final int TOTAL_TRIPLES = 34;
	
	/**
	 * Comprueba si se guardan correctamente los datos de las tripletas pertenecientes a un tipo de dependencia.
	 */
	@Test
	public void testCalculateMutualInformationOneThread() {
		prepareDataBaseTest();
		final ExecutorService executorService = getExecutorService(1);
		CalculateMutualInformationThread cmiThread1 = new CalculateMutualInformationThread(getTriplesDataForThread1(), getConnection());
		executorService.submit(cmiThread1);
		CalculateMutualInformationThread cmiThread2 = new CalculateMutualInformationThread(getTriplesDataForThread2(), getConnection());
		executorService.submit(cmiThread2);		
		executorService.shutdown();
		try {
			while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JCollocatioService jcs = new JCollocatioService();
		jcs.setDataBase(DB_TEST);
		
		List<Collocatio> list = jcs.findAllCollocations();
		assertEquals("No se ha guardado lo que se esperaba", 7, list.size());
		List<String> words = new ArrayList<String>();
		words.add("car");
		list = jcs.findCollocationsByWords(words, 0, 0);
		assertEquals("1No se ha recuperado lo que se esperaba", 3, list.size());
		
		words = new ArrayList<String>();
		words.add("slow");
		list = jcs.findCollocationsByWords(words, 0, 0);
		assertEquals("2No se ha recuperado lo que se esperaba", 1, list.size());
		Collocatio col = list.get(0);
		assertEquals("Valor de información mutua incorrecto",1.2, col.getInfomutua(), 0);
	}  

	/**
	 * @return el objeto TriplesData para inicializar el objeto CalculateMutualInformationThread que ejecutará el hilo 1
	 */
	private TriplesData getTriplesDataForThread1() {
		TriplesData data = new TriplesData(DEPENDENCY_TEST_1);
		data.setTriplesMap(fillTriplesForThread1());
		data.setTotalTriplesByDependency(7);
		data.setWord1FrecuencyMap(fillWord1ForThread1());
		data.setWord2FrecuencyMap(fillWord2ForThread1());
		data.setAdjustedFrequency(0);
		data.setTotalTriples(TOTAL_TRIPLES);
		return data;
	}
	
	/**
	 * @return mapa de las tripletas a analizar junto a sus ocurrencias que susará el hilo 1
	 */
	private Map<Triple, TripleEvents> fillTriplesForThread1() {		
		Map<Triple, TripleEvents> triplesCollectionByDependency = new HashMap<Triple, TripleEvents>();

		triplesCollectionByDependency.put(getTripleForThread1("man", "a"), getEventsForThread1(1));
		triplesCollectionByDependency.put(getTripleForThread1("house", "the"), getEventsForThread1(1));
		triplesCollectionByDependency.put(getTripleForThread1("house", "a"), getEventsForThread1(2));	
		triplesCollectionByDependency.put(getTripleForThread1("car", "the"), getEventsForThread1(3));
			
		return triplesCollectionByDependency;
	}
	
	private Triple getTripleForThread1(String w1, String w2) {
		Triple triple = new Triple();
		triple.setDependency(DEPENDENCY_TEST_1);
		triple.setWord1(w1);
		triple.setWord2(w2);
		return triple;
	}
	
	private TripleEvents getEventsForThread1(int total) {
		TripleEvents events = new TripleEvents("111");
		for (int i = 0; i < total-1; i++) {
			events.addEvent("111");
		}
		return events;
	}
	
	private Map<String, Long> fillWord1ForThread1() {
		Map<String, Long> word1FrecuencyMap = new HashMap<String, Long>();
		word1FrecuencyMap.put("car", 3L);
		word1FrecuencyMap.put("man", 1L);
		word1FrecuencyMap.put("house", 3L);
		return word1FrecuencyMap;
	}
	
	private Map<String, Long> fillWord2ForThread1() {
		Map<String, Long> word2FrecuencyMap = new HashMap<String, Long>();
		word2FrecuencyMap.put("the", 4L);
		word2FrecuencyMap.put("a", 3L);
		return word2FrecuencyMap;
	}	
	
	/**
	 * @return el objeto TriplesData para inicializar el objeto CalculateMutualInformationThread que ejecutará el hilo 1
	 */
	private TriplesData getTriplesDataForThread2() {
		TriplesData data = new TriplesData(DEPENDENCY_TEST_2);
		data.setTriplesMap(fillTriplesForThread2());
		data.setTotalTriplesByDependency(7);
		data.setWord1FrecuencyMap(fillWord1ForThread2());
		data.setWord2FrecuencyMap(fillWord2ForThread2());
		data.setAdjustedFrequency(0);
		data.setTotalTriples(TOTAL_TRIPLES);
		return data;
	}	
	
	/**
	 * @return mapa de las tripletas a analizar junto a sus ocurrencias que usará el hilo 2
	 */
	private Map<Triple, TripleEvents> fillTriplesForThread2() {		
		Map<Triple, TripleEvents> triplesCollectionByDependency = new HashMap<Triple, TripleEvents>();

		triplesCollectionByDependency.put(getTripleForThread2("big", "house"), getEventsForThread2(1));
		triplesCollectionByDependency.put(getTripleForThread2("slow", "car"), getEventsForThread2(2));
		triplesCollectionByDependency.put(getTripleForThread2("fast", "car"), getEventsForThread2(1));	
			
		return triplesCollectionByDependency;
	}	
	
	private Triple getTripleForThread2(String w1, String w2) {
		Triple triple = new Triple();
		triple.setDependency(DEPENDENCY_TEST_2);
		triple.setWord1(w1);
		triple.setWord2(w2);
		return triple;
	}
	
	private TripleEvents getEventsForThread2(int total) {
		TripleEvents events = new TripleEvents("111");
		for (int i = 0; i < total-1; i++) {
			events.addEvent("111");
		}
		return events;
	}
	
	private Map<String, Long> fillWord1ForThread2() {
		Map<String, Long> word1FrecuencyMap = new HashMap<String, Long>();
		word1FrecuencyMap.put("big", 1L);
		word1FrecuencyMap.put("fast", 1L);
		word1FrecuencyMap.put("slow", 2L);
		return word1FrecuencyMap;
	}
	
	private Map<String, Long> fillWord2ForThread2() {
		Map<String, Long> word2FrecuencyMap = new HashMap<String, Long>();
		word2FrecuencyMap.put("car", 3L);
		word2FrecuencyMap.put("house", 1L);
		return word2FrecuencyMap;
	}	
	
	/**
	 * @return la conexión a la base de datos para inicializar el objeto CalculateMutualInformationThread
	 */
	private Connection getConnection() {
		return ConnectionFactory.getInstance(DB_TEST).getConnection();
	}
	
	private void prepareDataBaseTest() {
		Connection connection = getConnection();
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement("DELETE FROM col_aparece");
			pstatement.executeUpdate();
			pstatement.close();
			pstatement = connection.prepareStatement("DELETE FROM col_collocatio");
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				} 
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
					e.printStackTrace();
			}		
		}
	}
	
	/**
	 * Obtiene un ExecutorService para el control y procesamiento de los hilos a lanzar y se personalizan algunas características de estos.
	 * @return ExecutorService
	 */
	private ExecutorService getExecutorService(int totalThreads) {
		ThreadFactory threadFactoryBuilder = new ThreadFactoryBuilder()
				.setNameThread("MutualInfoThread")
                .setDaemon(false)
                .setPriority(Thread.MAX_PRIORITY)
                .build();
		return Executors.newFixedThreadPool(totalThreads, threadFactoryBuilder);
	}
}
