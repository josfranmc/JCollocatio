package org.josfranmc.collocatio.triples;

import java.io.Serializable;

/**
 * It represents a linguistic triple according to universal dependencies format.<p>
 * A triple is formed by a type of dependency and two words  that are related to each other by that dependency.
 * The first word is called <i>head</i> (or <i>governor</i>) and the second one is called <i>dependent</i>.<p>
 * Two triples are equals if they have the same dependency and the same <i>head</i> and <i>dependant</i> words.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class Triple implements Serializable {

	private static final long serialVersionUID = -176602567035892229L;
	
	/**
	 * Dependency type of the triple
	 */
	private String dependency;
	
	/**
	 * Head (governor) word of the triple
	 */
	private String head;
	
	/**
	 * Dependent word of the triple
	 */
	private String dependent;
	
	/**
	 * Number of words between head and dependent
	 */
	private int distance;
	
	/**
	 * Mutual information value for this triple
	 */
	private double mutualInformation;


	/**
	 * Default constructor.
	 */
	public Triple() {
		this.distance = 0;
		this.mutualInformation = 0;
	}
	
	/**
	 * Returns the dependency of the triple.
	 * @return the dependency of the triple
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * Sets the dependency of the triple.
	 * @param dependency dependency type
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	/**
	 * Returns the head (governor) word of the triple.
	 * @return the head (governor) word of the triple
	 */
	public String getHead() {
		return head;
	}

	/**
	 * Sets the head (governor) word of the triple.
	 * @param head word
	 */
	public void setHead(String head) {
		this.head = head;
	}

	/**
	 * Returns the dependent word of the triple.
	 * @return the dependent word of the triple
	 */
	public String getDependent() {
		return dependent;
	}

	/**
	 * Sets the dependent word of the triple.
	 * @param dependent word
	 */
	public void setDependent(String dependent) {
		this.dependent = dependent;
	}
	
	/**
	 * Returns the number of words between head and dependant.
	 * @return the number of words between head and dependant
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Sets the number of words between head and dependant.
	 * @param dependent number of words
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * Returns the mutual information value of the triple.
	 * @return the mutual information value of the triple
	 */
	public double getMutualInformation() {
		return mutualInformation;
	}

	/**
	 * Sets the mutual information value of the triple.
	 * @param mutualInformation mutual information value
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
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((dependent == null) ? 0 : dependent.hashCode());
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
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (dependent == null) {
			if (other.dependent != null)
				return false;
		} else if (!dependent.equals(other.dependent))
			return false;
		return true;
	}

	
	/* (non-Javadoc)
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + dependency + "(" + head + ", " + dependent + ")" + "]";
	}
}
