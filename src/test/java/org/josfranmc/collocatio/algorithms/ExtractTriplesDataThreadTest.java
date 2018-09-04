package org.josfranmc.collocatio.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleEvents;
import org.josfranmc.collocatio.triples.TriplesCollection;
import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase ExtractTriplesDataThread
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ExtractTriplesDataThreadTest {

	/**
	 * Si no se indica colección de tripletas a analizar se debe lanzar excepción IllegalArgumentException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testParameter1Constructor() {
		new ExtractTriplesDataThread(null, "nsubj");
	}
	
	/**
	 * Si no se indica tipo de dependencia a buscar se debe lanzar excepción IllegalArgumentException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testParameter2Constructor() {
		new ExtractTriplesDataThread(new HashMap<Triple, TripleEvents>(), null);
	}
	
	/**
	 * Comprueba que se obtienen valores correctos al ejecutar el método call()
	 */
	@Test
	public void testCall() {
		TriplesCollection tc = new TriplesCollection();
		tc.save(getTriple1(), "111");
		tc.save(getTriple2(), "222");
		tc.save(getTriple2(), "222");
		
		ExtractTriplesDataThread etdt = new ExtractTriplesDataThread(tc.getTriplesCollection(), "dep");
		try {
			TriplesData td = etdt.call();
			assertEquals("Tipo de dependencia debe ser 'dep'", "dep", td.getDependency());
			assertTrue("No se encuentra palabra 'un' como palabra 1", td.getWord1FrecuencyMap().containsKey("un"));
			assertTrue("No se encuentra palabra 'ejemplo' como palabra 2", td.getWord2FrecuencyMap().containsKey("ejemplo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		etdt = new ExtractTriplesDataThread(tc.getTriplesCollection(), "nsubj");
		try {
			TriplesData td = etdt.call();
			assertEquals("Tipo de dependencia debe ser 'nsubj'", "nsubj", td.getDependency());
			assertTrue("No se encuentra palabra 'la' como palabra 1", td.getWord1FrecuencyMap().containsKey("la"));
			assertTrue("No se encuentra palabra 'prueba' como palabra 2", td.getWord2FrecuencyMap().containsKey("prueba"));
			
			assertEquals("El total existente de 'prueba' debe ser 1", Long.valueOf(1), td.getWord2FrecuencyMap().get("prueba"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tc.save(getTriple1(), "111");
		etdt = new ExtractTriplesDataThread(tc.getTriplesCollection(), "nsubj");
		try {
			TriplesData td = etdt.call();
			assertEquals("Tipo de dependencia debe ser 'nsubj' (2)", "nsubj", td.getDependency());
			assertEquals("El total existente de 'prueba' debe ser 2", Long.valueOf(2), td.getWord2FrecuencyMap().get("prueba"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tc.save(getTriple1(), "111");
		etdt = new ExtractTriplesDataThread(tc.getTriplesCollection(), "nsubj");
		try {
			TriplesData td = etdt.call();
			assertEquals("Tipo de dependencia debe ser 'nsubj' (3)", "nsubj", td.getDependency());
			assertEquals("El total existente de 'la' debe ser 3", Long.valueOf(3), td.getWord1FrecuencyMap().get("la"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return un objeto Triple de prueba
	 */
	private Triple getTriple1() {
		Triple t = new Triple();
		t.setDependency("nsubj");
		t.setWord1("la");
		t.setWord2("prueba");
		return t;
	}
	
	/**
	 * @return un objeto Triple de prueba
	 */
	private Triple getTriple2() {
		Triple t = new Triple();
		t.setDependency("dep");
		t.setWord1("un");
		t.setWord2("ejemplo");
		return t;
	}
}
