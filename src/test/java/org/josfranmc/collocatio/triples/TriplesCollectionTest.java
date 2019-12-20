package org.josfranmc.collocatio.triples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase TriplesCollection
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class TriplesCollectionTest {

	/**
	 * Comprueba que al crear el objeto TriplesCollection se inicializan sus variables miembro correctamente
	 */
	@Test
	public void testCreateTriplesCollection() {
		TriplesCollection tc = new TriplesCollection();
		assertNotNull("Atributo triplesCollection es null", tc.getTriples());
		assertNotNull("Atributo dependenciesCollection es null", tc.getDependencies());
		assertEquals("Atributo totalTriples no es cero", 0, tc.getTotalTriples());
	}
	
	/**
	 * Comprueba que se guardan correctamente objetos Triple
	 */
	@Test
	public void testSave() {
		TriplesCollection tc = new TriplesCollection();
		tc.save(getTriple1(), "testfile1.txt");
		
		Map<Triple, TripleEvents> entry = tc.getTriples();
		TripleEvents t = entry.get(getTriple1());

		assertTrue("Identificador de libro erróneo", t.getFiles().contains("testfile1.txt"));
		assertEquals("Valor de totalEvents no es 1", 1, t.getTotalEvents());
		assertEquals("Atributo totalTriples no es 1", 1, tc.getTotalTriples());
		
		tc.save(getTriple2(), "testfile2.txt");
		entry = tc.getTriples();
		t = entry.get(getTriple2());
		
		assertTrue("Identificador de libro erróneo", t.getFiles().contains("testfile2.txt"));
		assertEquals("Valor de totalEvents no es 1", 1, t.getTotalEvents());
		assertEquals("Atributo totalTriples no es 2", 2, tc.getTotalTriples());		
		
		tc.save(getTriple2(), "testfile2.txt");
		entry = tc.getTriples();
		t = entry.get(getTriple2());
		
		assertTrue("Identificador de libro erróneo", t.getFiles().contains("testfile2.txt"));
		assertEquals("Valor de totalEvents no es 2", 2, t.getTotalEvents());
		assertEquals("Atributo totalTriples no es 3", 3, tc.getTotalTriples());	
	}
	
	/**
	 * @return un objeto Triple de prueba
	 */
	private Triple getTriple1() {
		Triple t = new Triple();
		t.setDependency("det");
		t.setHead("car");
		t.setDependent("the");
		return t;
	}
	
	/**
	 * @return un objeto Triple de prueba
	 */
	private Triple getTriple2() {
		Triple t = new Triple();
		t.setDependency("nsubj");
		t.setHead("likes");
		t.setDependent("car");
		return t;
	}
}
