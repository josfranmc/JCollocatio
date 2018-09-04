package org.josfranmc.collocatio.db;

import static org.junit.Assert.assertNotNull;

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
		ConnectionFactory cf = ConnectionFactory.getInstance();
		assertNotNull("No se ha obtenido conexión a db por defecto", cf.getConnection());
	}
	
	/**
	 * Comprueba que se obtiene una conexión a una base de datos concreta
	 */
	@Test
	public void testConnectionFactoryOtherConnection() {
		ConnectionFactory cf = ConnectionFactory.getInstance("col_default");
		assertNotNull("No se ha obtenido conexión a db indicada", cf.getConnection());
	}
}
