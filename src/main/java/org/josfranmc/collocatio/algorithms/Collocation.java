package org.josfranmc.collocatio.algorithms;

import java.io.Serializable;

/**
 * It represents a linguistic triple according to universal dependencies format.<p>
 * A triple is formed by a type of dependency and two words  that are related to each other by that dependency.
 * The first word is called <i>head</i> (or <i>governor</i>) and the second one is called <i>dependent</i>.<p>
 * Two triples are equals if they have the same dependency and the same <i>head</i> and <i>dependant</i> words.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class Collocation {

	/**
	 * Head (governor) word of the triple
	 */
	private String head;
	
	/**
	 * Dependent word of the triple
	 */
	private String dependent;

	
	/**
	 * Mutual information value for this triple
	 */
	private double mutualInformation;


	/**
	 * Default constructor.
	 */
	public Collocation() {
		this.mutualInformation = 0;
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
		if (!(obj instanceof Collocation))
			return false;
		Collocation other = (Collocation) obj;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + head + ", " + dependent + ")";
	}
}
