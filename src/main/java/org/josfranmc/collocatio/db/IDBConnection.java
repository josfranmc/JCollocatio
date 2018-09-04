package org.josfranmc.collocatio.db;

import java.sql.Connection;

/**
 * Interfaz que deben implementar las clases que quieran gestionar el acceso a una base de datos.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public interface IDBConnection {

	/**
	 * Debe devolver una conexión a una de base de datos.
	 * @return conexión a una base de datos en forma de objeto Connection
	 */
	public Connection getConnection();
}
