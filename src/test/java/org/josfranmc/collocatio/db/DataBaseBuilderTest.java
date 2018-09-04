package org.josfranmc.collocatio.db;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase DataBaseBuilder
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class DataBaseBuilderTest {

	/**
	 * Si no se ha indicado un nombre para la nueva base de datos se debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNameNewDB() {
		DataBaseBuilder dbb = new DataBaseBuilder();
		dbb.createNewDB();	
		dbb = new DataBaseBuilder(null, null);
		dbb.createNewDB();	
	}	

	/**
	 * Si ya existe una base de datos con el nombre indicado se debe lanzar IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateNewDB() {
		DataBaseBuilder dbb = new DataBaseBuilder("col_default", "Base de datos por defecto");
		dbb.createNewDB();	
	}
	
	/**
	 * Si el nuevo nombre de la base de datos supera los 14 caracteres se debe lanzar StringIndexOutOfBoundsException
	 */
	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testLengthNameNewDB() {
		DataBaseBuilder dbb = new DataBaseBuilder("nombrequesuperalalongitudmaxima", "Base de datos por defecto");
		dbb.createNewDB();	
	}
	
	/**
	 * Si la descripción de la base de datos supera los 50 caracteres se debe lanzar StringIndexOutOfBoundsException
	 */
	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testLengthDescriptionNewDB() {
		DataBaseBuilder dbb = new DataBaseBuilder("ejemplo", "Base de datos cuya descripción supera los cincuenta caracteres.");
		dbb.createNewDB();	
	}	
}
