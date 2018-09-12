package org.josfranmc.collocatio.service;

import java.util.List;

import org.josfranmc.collocatio.service.dao.CollocatioDao;
import org.josfranmc.collocatio.service.dao.ICollocatioDao;
import org.josfranmc.collocatio.service.domain.Collocatio;

/**
 * Clase que ofrece los servicios necesarios para interactuar con la base de datos. Ejecuta consultas y modifica datos.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class JCollocatioService implements ICollocatioService {

	/**
	 * Encapsula las operaciones que pueden realizarse.
	 */
	private ICollocatioDao colDao;
	
	
	/**
	 * Constructor por defecto.
	 */
	public JCollocatioService() {
		colDao = new CollocatioDao();
	}
	
	/**
	 * Constructor. Inicializa una nueva instancia con el nombre de la base de datos con la que se quiere conectar para trabajar.
	 * @param dbName nombre de la base de datos
	 */
	public JCollocatioService(String dbName) {
		colDao = new CollocatioDao();
		colDao.setDataBase(dbName);
	}

	/**
	 * @param id identificador 
	 * @return una colocación según su identificador en la base de datos
	 */
	@Override
	public Collocatio findCollocationsById(long id) {
		return colDao.getElementById(id);
	}
	
	/**
	 * Obtiene las colocaciones que contienen las palabras especificadas.<p>
	 * Permite paginar la consulta, de forma que se puede consultar una determinada página y especificar el número de registros por página.<br>
	 * La primera página se numera como cero.<br>
	 * Si se especifica cero como tamaño de página se devuelven todos los registros existentes.
	 */
	@Override
	public List<Collocatio> findCollocationsByWords(List<String> words, int offset, int size) {
		int o = (offset <= 0) ? 0 : offset;
		int s = (size <= 0) ? 0 : size;
		return colDao.findCollocationsByWords(words, o, s);
	}

	/**
	 * Obtiene las colocaciones que empiezan por una palabra dada.
	 * Permite paginar la consulta, de forma que se puede consultar una determinada página y especificar el número de registros por página.<br>
	 * La primera página se numera como cero.<br>
	 * Si se especifica cero como tamaño de página se devuelven todos los registros existentes.
	 */
	@Override
	public List<Collocatio> findCollocationsStartWith(List<String> words, int offset, int size) {
		int o = (offset <= 0) ? 0 : offset;
		int s = (size <= 0) ? 0 : size;
		return colDao.findCollocationsStartWith(words, o, s);
	}

	/**
	 * Obtiene las colocaciones que terminan por una palabra dada.
	 * Permite paginar la consulta, de forma que se puede consultar una determinada página y especificar el número de registros por página.<br>
	 * La primera página se numera como cero.<br>
	 * Si se especifica cero como tamaño de página se devuelven todos los registros existentes.
	 */
	@Override
	public List<Collocatio> findCollocationsEndWith(List<String> words, int offset, int size) {
		int o = (offset <= 0) ? 0 : offset;
		int s = (size <= 0) ? 0 : size;
		return colDao.findCollocationsEndWith(words, o, s);
	}

	@Override
	public List<Collocatio> findAllCollocations() {
		return colDao.getAllElements();
	}
	
	/**
	 * Obtiene todas las colocaciones almacenadas paginando el resultado.
	 * Se puede consultar una determinada página y especificar el número de registros por página.<br>
	 * La primera página se numera como cero.<br>
	 * Si se especifica cero como tamaño de página se devuelven todos los registros existentes.
	 */
	@Override
	public List<Collocatio> findAllCollocationsPaged(int offset, int size) {
		int o = (offset <= 0) ? 0 : offset;
		int s = (size <= 0) ? 0 : size;
		return colDao.getAllElementsByPage(o, s);
	}
	
	/**
	 * Obtiene las colocaciones con el valor más alto de información mutua.<br>
	 * Si se indica cero como cantidad de registros a obtener se devuelven diez registros.
	 * @param size cantidad de registros a obtener
	 * @return lista de colocaciones
	 */
	@Override
	public List<Collocatio> findBestCollocationsByMutualInformation(int size) {
		return colDao.findBestCollocationsByMutualInformation(((size <= 0) ? 10 : size));
	}
	
	/**
	 * Añade una nueva colocación a la base de datos.
	 * @param collocatio colocación a insertar
	 * @return el identificador asiganado al nuevo registro guardado
	 * @see Collocatio
	 */
	@Override
	public long addCollocation(Collocatio collocatio) {
		return colDao.save(collocatio);
	}

	/**
	 * Elimina una colocación de una base de datos.
	 * @param id identificador del registro a eliminar
	 * @return número de registros afectados
	 */
	@Override
	public long deleteCollocation(Long id) {
		return colDao.deleteElement(id);
	}
	
	@Override
	public Collocatio createElement() {
		return new Collocatio();
	}
	
	/**
	 * Establece el nombre de la base de datos con la que se quiere conectar para trabajar.
	 * @param dbName nombre de la base de datos
	 */
	@Override
	public void setDataBase(String dbName) {
		colDao.setDataBase(dbName);
	}
}
