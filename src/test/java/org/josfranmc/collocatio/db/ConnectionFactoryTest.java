package org.josfranmc.collocatio.db;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase ConnectionFactory
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ConnectionFactoryTest {

	/**
	 * Comprueba que se obtiene una conexión a la base de datos por defecto (col_default)
	 */
	@Test
	public void testConnectionFactoryDefaultConnection() {
		ConnectionFactory.getInstance();
		assertTrue("No se ha obtenido conexión a db por defecto", ConnectionFactory.isInstance(ConnectionFactory.DEFAULT_DB));
	}
	
	/**
	 * Comprueba que se obtiene una conexión a una base de datos concreta
	 */
	@Test
	public void testConnectionFactoryOtherConnection() {
		ConnectionFactory.getInstance("col_default");
		assertTrue("No se ha obtenido conexión a db indicada", ConnectionFactory.isInstance("col_default"));
	}
}
