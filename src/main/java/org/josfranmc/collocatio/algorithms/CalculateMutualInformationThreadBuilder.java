package org.josfranmc.collocatio.algorithms;

import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleData;
import org.josfranmc.collocatio.triples.TripleEvents;
import org.josfranmc.collocatio.triples.TriplesCollection;

/**
 * Creates and sets up a <code>ParserThread</code> object. Five properties may be setting:
 * <ul>
 * <li>parser model</li>
 * <li>tagger model</li>
 * <li><code>TriplesCollection</code> object where to store the triples obtained</li>
 * <li>file to parser</li>
 * <li>maximun length of the sentences to parse</li>
 * </ul>
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see ParserThread
 * @see TriplesCollection
 */
public class CalculateMutualInformationThreadBuilder {

	private TripleData tripleData;
	
	private TripleEvents events;
	
	private Long totalHeadWord;
	
	private Long totalDependentWord;
	
	private Double adjustedFrequency;
	
	private Long totalDependencies;

	private TriplesCollection triplesCollection;
	
	private String dependency;
	
	
	/**
	 * Default constructor.
	 */
	CalculateMutualInformationThreadBuilder() {
		
	}
	
	/**
	 * Sets the tagger model to be used.
	 * @param tagger the path of the tagger model
	 * @return a reference to the <code>ParserThreadBuilder</code> object that call this method
	 */
	
    public CalculateMutualInformationThreadBuilder setTripleData(TripleData tripleData) {
		this.tripleData = tripleData;
		return this;
	}
//
//    public CalculateMutualInformationThreadBuilder setTriple(TripleData tripleData) {
//		this.tripleData = tripleData;
//		return this;
//	}
//
//	public CalculateMutualInformationThreadBuilder setEvents(TripleEvents events) {
//		this.events = events;
//		return this;
//	}
//
//	public CalculateMutualInformationThreadBuilder setTotalHeadWord(long totalHeadWord) {
//		this.totalHeadWord = totalHeadWord;
//		return this;
//	}
//
//	public CalculateMutualInformationThreadBuilder setTotalDependentWord(long totalDependentWord) {
//		this.totalDependentWord = totalDependentWord;
//		return this;
//	}

	public CalculateMutualInformationThreadBuilder setAdjustedFrequency(double adjustedFrequency) {
		this.adjustedFrequency = adjustedFrequency;
		return this;
	}

	public CalculateMutualInformationThreadBuilder setTotalDependencies(long totalDependencies) {
		this.totalDependencies = totalDependencies;
		return this;
	}

	public CalculateMutualInformationThreadBuilder setTriplesCollection(TriplesCollection triples) {
		this.triplesCollection = triples;
		return this;
	}
	
	public CalculateMutualInformationThreadBuilder setDependency(String dependency) {
		this.dependency = dependency;
		return this;
	}
	
	/**
     * Creates and sets up a <code>ParserThread</code> object with the parameters previously set.<br>
     * If there is any wrong parameter an <code>IllegalArgumentException</code> exception is thrown.
     * @return  a <code>ParserThread</code> object correctly configured
     * @see ParserThread
     * @throws IllegalArgumentException
     */
    public CalculateMutualInformationThread build() {
    	if (tripleData == null) {
            throw new IllegalArgumentException("Triple is required");
    	}
//    	if (events == null) {
//            throw new IllegalArgumentException("events is required");
//    	}
//    	if (totalHeadWord == null) {
//            throw new IllegalArgumentException("totalHeadWord name is required");
//    	}
//    	if (totalDependentWord == null) {
//            throw new IllegalArgumentException("totalDependentWord is required");
//    	}
    	if (totalDependencies == null) {
            throw new IllegalArgumentException("totalDependencies is required");
    	}
    	if (adjustedFrequency == null) {
            throw new IllegalArgumentException("adjustedFrequency is required");
    	}
    	CalculateMutualInformationThread cmit = new CalculateMutualInformationThread();    	
    	cmit.setTriple(tripleData.getTriple());
    	cmit.setEvents(tripleData.getEvents());
    	cmit.setTotalHeadWord(tripleData.getTotalHeadWord());
    	cmit.setTotalDependentWord(tripleData.getTotalDependentWord());
    	cmit.setTotalDependencies(totalDependencies);
    	cmit.setAdjustedFrequency(adjustedFrequency);
    	
    	cmit.setDependency(dependency);
    	cmit.setTriplesCollection(triplesCollection);
        return cmit;
    }
}