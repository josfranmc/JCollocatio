package org.josfranmc.collocatio.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.util.PropertiesFile;

/**
 * Permite acceder a una base de datos MySQL.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class MySQLConnection implements IDBConnection {

	private static final Logger log = Logger.getLogger(MySQLConnection.class);
	
	/**
	 * Ruta del fichero de propiedades en el classpath
	 */
	private final static String DB_PROPERTIES_FILE = "db/DBMySQL.properties";

	private final static String PROPERTY_DRIVER = "driver";
	private final static String PROPERTY_HOST = "host";
	private final static String PROPERTY_PORT = "port";
	private final static String PROPERTY_DBNAME = "dbname";
	private final static String PROPERTY_PARAMS = "params";
	private final static String PROPERTY_USER = "user";
	private final static String PROPERTY_PASSWORD = "password";
	
	/**
	 * Nombre del driver a utilizar para conectar con la base de datos
	 */
	private static String DB_DRIVER;
	
	/**
	 * URL de conexión a la base de datos
	 */
	private static String DB_URL;
	
	/**
	 * Nombre de la cuenta de usuario para conectar con la base de datos
	 */
	private static String DB_USER;
	
	/**
	 * Clave de la cuenta de usuario para conectar con la base de datos
	 */
	private static String DB_PASSWORD;
	
	/**
	 * Constructor principal. Lee el fichero de configuración y establece una conexión con los datos leidos a una base de datos MySQL.<br>
	 * Se se indica un nombre de base de datos la conexión se realizará sobre ella, en lugar de utilizar el valor por defecto del fichero de
	 * configuración.
	 * @param dataBase nombre de la base de datos con la que conectar
	 */
	MySQLConnection(String dataBase) {
		loadConfigConnection(dataBase);
		try {
			Class.forName(DB_DRIVER);
			//DriverManager.registerDriver(new Driver());
			log.info("Cargado driver " + DB_DRIVER);
		} catch (ClassNotFoundException e) {
			log.error("Error cargando driver " + DB_DRIVER);
			throw new RuntimeException("Error cargando driver", e);
		}	
	}
	
	/**
	 * Devuelve una conexión del pool de conexiones
	 */
	@Override
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Lee el fichero de propiedades que contiene los datos de configuración para la conexión a la base de datos.
	 * @param dataBase nombre de la base de datos con la que conectar
	 * @see PropertiesFile
	 */
	private void loadConfigConnection(String dataBase) {
		Properties prop = PropertiesFile.loadProperty(DB_PROPERTIES_FILE);
		DB_DRIVER = prop.getProperty(PROPERTY_DRIVER);
		String host = prop.getProperty(PROPERTY_HOST);
		String port = prop.getProperty(PROPERTY_PORT);
		String dbname = (dataBase == null) ? prop.getProperty(PROPERTY_DBNAME) : dataBase;
		String params = prop.getProperty(PROPERTY_PARAMS);
		DB_USER = prop.getProperty(PROPERTY_USER);
		DB_PASSWORD = prop.getProperty(PROPERTY_PASSWORD);
		DB_URL = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?" + params;
		log.info("Cargando datos de fichero " + DB_PROPERTIES_FILE);
		log.info("   host = " + host);
		log.info("   port = " + port);
		log.info("   dbname = " + dbname);
		log.info("   params = " + params);
		log.info("   user = " + DB_USER);
	}
}
