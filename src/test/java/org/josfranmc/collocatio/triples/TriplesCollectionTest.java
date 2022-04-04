package org.josfranmc.collocatio.triples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	public void createTriplesCollectionTest() {
		TriplesCollection tc = new TriplesCollection();
		assertNotNull("Colección principal es null", tc.getTriples());
		assertNotNull("Colección de dependencia es null", tc.getTriplesByDependency("det"));
		assertNotNull("Conjunto de dependencias es null", tc.getDependencies());
		assertEquals("TotalTriples no es cero", 0, tc.getTotalTriples());
	}
	
	/**
	 * Comprueba que se guardan correctamente objetos Triple
	 */
	@Test
	public void saveTest() {
		TriplesCollection tc = new TriplesCollection();
		Triple triple1 = getTriple1();
		
		tc.save(triple1, "testfile1.txt");

		Map<Triple, TripleEvents> triplesByDependency = tc.getTriplesByDependency(triple1.getDependency());
		TripleEvents event = triplesByDependency.get(triple1);

		assertTrue("Identificador de libro erróneo", event.getFiles().contains("testfile1.txt"));
		assertEquals("Valor de totalEvents no es 1", 1, event.getTotalEvents());
		assertEquals("Atributo totalTriples no es 1", 1, tc.getTotalTriples());
		assertEquals("Size no es 1", 1, tc.getTriples().size());
		
		Triple triple2 = getTriple2();
		tc.save(triple2, "testfile2.txt");

		triplesByDependency = tc.getTriplesByDependency(triple2.getDependency());
		event = triplesByDependency.get(triple2);
		
		assertTrue("Identificador de libro erróneo", event.getFiles().contains("testfile2.txt"));
		assertEquals("Valor de totalEvents no es 1", 1, event.getTotalEvents());
		assertEquals("Atributo totalTriples no es 2", 2, tc.getTotalTriples());
		assertEquals("Size no es 2", 2, tc.getTriples().size());
		
		Triple triple3 = getTriple2();
		tc.save(triple3, "testfile1.txt");

		triplesByDependency = tc.getTriplesByDependency(triple3.getDependency());
		event = triplesByDependency.get(triple3);
		
		assertTrue("Identificador de libro erróneo", event.getFiles().contains("testfile2.txt"));
		assertEquals("Valor de totalEvents no es 2", 2, event.getTotalEvents());
		assertEquals("Atributo totalTriples no es 3", 3, tc.getTotalTriples());
		assertEquals("Size no es 3", 2, tc.getTriples().size());
		
		Triple triple4 = getTriple4();
		tc.save(triple4, "testfile2.txt");

		triplesByDependency = tc.getTriplesByDependency(triple4.getDependency());
		event = triplesByDependency.get(triple4);
		
		tc.save(getTriple5(), "testfile2.txt");
		
		assertTrue("Identificador de libro erróneo", event.getFiles().contains("testfile2.txt"));
		assertEquals("Valor de totalEvents no es 1", 1, event.getTotalEvents());
		assertEquals("Atributo totalTriples no es 5", 5, tc.getTotalTriples());
		assertEquals("Size no es 2", 2, tc.getTriples().size());
		assertEquals("Total triples dependencia det no es 2", 2, tc.getTotalTriplesByDependency("det"));
		assertEquals("Total triples dependencia nsubj no es 3", 3, tc.getTotalTriplesByDependency("nsubj"));
		assertEquals("Total triples dependencia test no es 0", 0, tc.getTotalTriplesByDependency("test"));
		
		assertEquals("Total palabra the no es 0", 0, tc.getTotalHeadWord("det", "the"));
		assertEquals("Total palabra dog no es 0", 0, tc.getTotalDependentWord("nsubj", "dog"));
		assertEquals("Total palabra house no es 1", 1, tc.getTotalHeadWord("det", "house"));
		assertEquals("Total palabra the no es 2", 2, tc.getTotalDependentWord("det", "the"));
		assertEquals("Total palabra likes no es 3", 3, tc.getTotalHeadWord("nsubj", "likes"));
		assertEquals("Total palabra sugar no es 1", 1, tc.getTotalDependentWord("nsubj", "sugar"));
		assertEquals("Total palabra car no es 2", 2, tc.getTotalDependentWord("nsubj", "car"));
		
		Iterator<TripleData> it = tc.iterator();
		
		while (it.hasNext()) {
			TripleData triple = it.next();
			
			System.out.println(triple.getTriple().toString());
			
		}
		
		TripleData triple = it.next();
		System.out.println(triple.getTriple().toString());
	}
	
	@Test
	public void saveListTest() {
		List<Triple> triples = new ArrayList<>();
		Triple triple1 = getTriple1();
		triples.add(triple1);
		triples.add(getTriple2());
		triples.add(getTriple2());
		triples.add(getTriple4());
		triples.add(getTriple5());
		
		TriplesCollection tc = new TriplesCollection();
		tc.save(triples, "filetest.txt");
		
		Map<Triple, TripleEvents> triplesByDependency = tc.getTriplesByDependency(triple1.getDependency());
		TripleEvents event = triplesByDependency.get(triple1);
		
		assertTrue("Identificador de libro erróneo", event.getFiles().contains("filetest.txt"));
		assertEquals("Valor de totalEvents no es 1", 1, event.getTotalEvents());
		assertEquals("Atributo totalTriples no es 5", 5, tc.getTotalTriples());
		assertEquals("Size no es 2", 2, tc.getTriples().size());
		assertEquals("Total triples dependencia det no es 2", 2, tc.getTotalTriplesByDependency("det"));
		assertEquals("Total triples dependencia nsubj no es 3", 3, tc.getTotalTriplesByDependency("nsubj"));
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
	
	/**
	 * @return un objeto Triple de prueba
	 */
	private Triple getTriple4() {
		Triple t = new Triple();
		t.setDependency("det");
		t.setHead("house");
		t.setDependent("the");
		return t;
	}
	
	/**
	 * @return un objeto Triple de prueba
	 */
	private Triple getTriple5() {
		Triple t = new Triple();
		t.setDependency("nsubj");
		t.setHead("likes");
		t.setDependent("sugar");
		return t;
	}
}
