package org.josfranmc.collocatio.triples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase TriplesCollection
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class TriplesCollectionTest {

	/**
	 * Comprueba que al crear el objeto TriplesCollection se inicializan sus variables miembro correctamente
	 */
	@Test
	public void testCreateTriplesCollection() {
		TriplesCollection tc = new TriplesCollection();
		assertNotNull("Atributo triplesCollection es null", tc.getTriplesCollection());
		assertNotNull("Atributo dependenciesCollection es null", tc.getDependenciesCollection());
		assertNotNull("Atributo totalTriples es null", tc.getTotalTriples());
		assertEquals("Atributo totalTriples no es cero", 0, tc.getTotalTriples());
	}
	
	/**
	 * Comprueba que se guardan correctamente objetos Triple
	 */
	@Test
	public void testSave() {
		TriplesCollection tc = new TriplesCollection();
		tc.save(getTriple1(), "111");
		
		Map<Triple, TripleEvents> entry = tc.getTriplesCollection();
		TripleEvents t = entry.get(getTriple1());

		assertTrue("Identificador de libro erróneo", t.getBooks().contains("111"));
		assertEquals("Valor de totalEvents no es 1", 1, t.getTotalEvents());
		assertEquals("Atributo totalTriples no es 1", 1, tc.getTotalTriples());
		
		tc.save(getTriple2(), "222");
		entry = tc.getTriplesCollection();
		t = entry.get(getTriple2());
		
		assertTrue("Identificador de libro erróneo", t.getBooks().contains("222"));
		assertEquals("Valor de totalEvents no es 1", 1, t.getTotalEvents());
		assertEquals("Atributo totalTriples no es 2", 2, tc.getTotalTriples());		
		
		tc.save(getTriple2(), "222");
		entry = tc.getTriplesCollection();
		t = entry.get(getTriple2());
		
		assertTrue("Identificador de libro erróneo", t.getBooks().contains("222"));
		assertEquals("Valor de totalEvents no es 2", 2, t.getTotalEvents());
		assertEquals("Atributo totalTriples no es 3", 3, tc.getTotalTriples());	
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
