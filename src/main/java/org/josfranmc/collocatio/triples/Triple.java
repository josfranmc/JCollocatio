package org.josfranmc.collocatio.triples;

import java.io.Serializable;

/**
 * Representa una tripleta lingüística según el formato de dependencias universales.<br>
 * Una tripleta está formada por un tipo de <i>dependencia</i> y por dos palabras que se relacionan por ese tipo de dependencia. Denominamos como <i>palabra 1</i>
 * a la palabra situada a la izquierda y como <i>palabra 2</i> a la situada a la derecha.<p>
 * Dos tripletas son iguales si tienen la misma <i>dependencia</i>, la misma <i>palabra 1</i> y la misma <i>palabra 2</i>.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class Triple implements Serializable {

	private static final long serialVersionUID = -176602567035892229L;
	
	/**
	 * Tipo de dependencia
	 */
	private String dependency;
	
	/**
	 * Palabra 1 de la tripleta
	 */
	private String word1;
	
	/**
	 * Palabra 2 de la tripleta
	 */
	private String word2;
	
	/**
	 * Valor de información mutua calculado para esta tripleta
	 */
	private double mutualInformation;


	public Triple() {

	}
	
	/**
	 * @return el tipo de dependencia de la tripleta
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * Establece el tipo de dependencia de la tripleta
	 * @param dependency tipo de dependencia
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	/**
	 * @return la palabra 1 de la tripleta
	 */
	public String getWord1() {
		return word1;
	}

	/**
	 * Establece la <i>palabra 1</i> de la tripleta
	 * @param word1 palabra a asignar
	 */
	public void setWord1(String word1) {
		this.word1 = word1;
	}

	/**
	 * @return la <i>palabra 2</i> de la tripleta
	 */
	public String getWord2() {
		return word2;
	}

	/**
	 * Establece la palabra 2 de la tripleta
	 * @param word2 palabra a asignar
	 */
	public void setWord2(String word2) {
		this.word2 = word2;
	}
	
	/**
	 * @return el valor de información mutua calculado para la tripleta
	 */
	public double getMutualInformation() {
		return mutualInformation;
	}

	/**
	 * Establece el valor de información mutua
	 * @param mutualInformation valor númerico de información mutua
	 */
	public void setMutualInformation(double mutualInformation) {
		this.mutualInformation = mutualInformation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dependency == null) ? 0 : dependency.hashCode());
		result = prime * result + ((word1 == null) ? 0 : word1.hashCode());
		result = prime * result + ((word2 == null) ? 0 : word2.hashCode());
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
		if (!(obj instanceof Triple))
			return false;
		Triple other = (Triple) obj;
		if (dependency == null) {
			if (other.dependency != null)
				return false;
		} else if (!dependency.equals(other.dependency))
			return false;
		if (word1 == null) {
			if (other.word1 != null)
				return false;
		} else if (!word1.equals(other.word1))
			return false;
		if (word2 == null) {
			if (other.word2 != null)
				return false;
		} else if (!word2.equals(other.word2))
			return false;
		return true;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return dependency + "(" + word1 + ", " + word2 + ")";
	}
}
