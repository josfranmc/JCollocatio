package org.josfranmc.collocatio.triples;

public class TripleData {

	private Triple triple;
	
	private TripleEvents events;
	
	private long totalHeadWord;
	
	private long totalDependentWord;
	
	TripleData(Triple triple, TripleEvents events, long totalHeadWord, long totalDependentWord) {
		this.triple = triple;
		this.events = events;
		this.totalHeadWord = totalHeadWord;
		this.totalDependentWord = totalDependentWord;
	}

	public Triple getTriple() {
		return triple;
	}

	public TripleEvents getEvents() {
		return events;
	}

	public long getTotalHeadWord() {
		return totalHeadWord;
	}

	public long getTotalDependentWord() {
		return totalDependentWord;
	}
}
