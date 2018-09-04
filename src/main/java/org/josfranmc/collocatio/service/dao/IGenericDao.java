package org.josfranmc.collocatio.service.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Interfaz que expone las operaciones básicas que pueden realizarse con las tablas de la base de datos.
 * Utiliza tipos genéricos de forma que pueda reutilizarse para acceder a diferentes tablas.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public interface IGenericDao<T,ID extends Serializable> {

	T createElement(); 
	
	long save(T entity);
	
	T getElementById(ID id);
	
	void deleteElement(ID id);
	
	List<T> getAllElements();
}