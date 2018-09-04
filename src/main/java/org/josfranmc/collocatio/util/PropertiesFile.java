package org.josfranmc.collocatio.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Gestiona la carga de un fichero de propiedades.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class PropertiesFile {
	
	/**
	 * Carga un fichero de propiedades y las devuelve en forma de un objeto Properties
	 * @param propertiesURL ruta del fichero a cargar
	 * @return las propiedades leidas del fichero
	 */
	public static Properties loadProperty(String propertiesURL){
		Properties properties = null;
		try {
			properties = new Properties();
			InputStream inputStream = PropertiesFile.class.getClassLoader().getResourceAsStream(propertiesURL);
			properties.load(inputStream);
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}
		return properties;
	}
}
