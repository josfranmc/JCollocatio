package org.josfranmc.collocatio.service.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una colocación encontrada, encapsulando los datos que la definen.<p>
 * Una colocación se representa como una tripleta compuesta por tres elementos: dos palabras y el tipo de dependencia que las relaciona.<br>
 * Junto a estos datos, se recoge el valor de información mutua calculado para la tripleta y la lista de libros en la que ha sido encontrada. 
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class Collocatio {

	/**
	 * Identificador
	 */
	private Long ID;
	
	/**
	 * Tipo de dependencia que define la tripleta
	 */
	private String dependencia;
	
	/**
	 * Palabra 1 de la tripleta
	 */
	private String palabra1;
	
	/**
	 * Palabra 2 de la tripleta
	 */
	private String palabra2;
	
	/**
	 * Valor de información mutua de la trileta
	 */
	private double infomutua;
	
	private List<String> books;
	
	public Collocatio() {
		books = new ArrayList<String>();
	}

	/**
	 * @return el iD de la colocación
	 */
	public Long getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(Long iD) {
		ID = iD;
	}

	/**
	 * @return el tipo de dependencia de la tripleta
	 */
	public String getDependencia() {
		return dependencia;
	}

	/**
	 * @param dependencia tipo de dependencia
	 */
	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	/**
	 * @return palabra 1 de la tripleta
	 */
	public String getPalabra1() {
		return palabra1;
	}

	/**
	 * @param palabra1 the palabra1 to set
	 */
	public void setPalabra1(String palabra1) {
		this.palabra1 = palabra1;
	}

	/**
	 * @return palabra 2 de la tripleta
	 */
	public String getPalabra2() {
		return palabra2;
	}

	/**
	 * @param palabra2 the palabra2 to set
	 */
	public void setPalabra2(String palabra2) {
		this.palabra2 = palabra2;
	}

	/**
	 * @return el valor de información mutua
	 */
	public double getInfomutua() {
		return infomutua;
	}

	/**
	 * @param infomutua el valor de información mutua
	 */
	public void setInfomutua(double infomutua) {
		this.infomutua = infomutua;
	}
	
	/**
	 * @return la lista de libros en los que se ha encontrado la tripleta que define la colocación
	 */
	public List<String> getBooks() {
		return books;
	}

	/**
	 * @param libros lista de libros en los que se ha encontrado la tripleta que define la colocación
	 */
	public void setBooks(List<String> books) {
		this.books = books;
	}

	/**
	 * Añade un libro a la lista de libros en los que se ha encontrado la tripleta que define la colocación
	 * @param books libro en el que se ha encontrado la tripleta
	 */
	public void setBook(String lib) {
		this.books.add(lib);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dependencia == null) ? 0 : dependencia.hashCode());
		result = prime * result + ((palabra1 == null) ? 0 : palabra1.hashCode());
		result = prime * result + ((palabra2 == null) ? 0 : palabra2.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Collocatio))
			return false;
		Collocatio other = (Collocatio) obj;
		if (dependencia == null) {
			if (other.dependencia != null)
				return false;
		} else if (!dependencia.equals(other.dependencia))
			return false;
		if (palabra1 == null) {
			if (other.palabra1 != null)
				return false;
		} else if (!palabra1.equals(other.palabra1))
			return false;
		if (palabra2 == null) {
			if (other.palabra2 != null)
				return false;
		} else if (!palabra2.equals(other.palabra2))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return dependencia + "(" + palabra2 + ", " + palabra1 + ")";
	}
}
