package org.josfranmc.collocatio.db;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase ConnectionPool
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ConnectionPoolTest {

	/**
	 * Si se pasa null al constructor se debe conectar a la base de datos por defecto (col_default)
	 */
	@Test
	public void testConnectionPoolParameterConstructorNull() {
		ConnectionPool cp = new ConnectionPool(null);
		Connection c = cp.getConnection();
		DatabaseMetaData md;
		try {
			md = c.getMetaData();
			assertTrue("No se ha conectado a base de datos por defecto (col_default)", md.getURL().contains("col_default"));
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Si se indica un nombre de base de datos que no existe, entonces la conexión devuelta debe ser null
	 */
	@SuppressWarnings("unused")
	@Test
	public void testConnectionPoolParameterConstructorNotExists() {
		ConnectionPool cp = new ConnectionPool("sdfsd");
		Connection c = cp.getConnection();
		assertNull("La conexión no es null", c);
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
}
