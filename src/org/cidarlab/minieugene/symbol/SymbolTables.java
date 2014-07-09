/*
 * Copyright (c) 2014, Boston University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 *    
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *    
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cidarlab.minieugene.symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.cidarlab.minieugene.act.ACT;
import org.cidarlab.minieugene.dom.Component;
import org.cidarlab.minieugene.predicates.Predicate;
import org.cidarlab.minieugene.predicates.interaction.Interaction;


/**
 * 
 * @author Ernst Oberortner
 */
public class SymbolTables {

	/*
	 * set of parts
	 * 
	 * we offer a predefined set of parts that
	 * the user can extend on-demand
	 * 
	 * predefined_symbols := {p, r, g, t}
	 * 
	 * p ... Promoter
	 * r ... RBS
	 * g ... Gene
	 * t ... Terminator
	 */
	private Map<Integer, Component> symbols;
	private Set<Predicate> predicates;
	private Set<Interaction> interactions;
	
	/*
	 * the Abstract Composition Tree
	 */
	private ACT act;
	
	/*
	 * N ... the length of the device
	 *       -1 ... random length
	 *       >0 ... length 
	 */
	public SymbolTables() {
		/*
		 * the symbols HashMap keeps track of all defined 
		 * symbols
		 * the predefined ones are: p, r, g, t
		 */
		this.symbols = new HashMap<Integer, Component>();
	
		this.predicates = new HashSet<Predicate>();
		
		this.interactions = new HashSet<Interaction>();
		
		/*
		 * Abstract Composition Tree
		 */
		this.act = new ACT();
		
	}
	
	/* 
	 * put a symbol into the symbol tables
	 */
	public int put(String s) {
		return this.put(new Component(s));
	}
	
	public int put(Component s) {
		if(!containsId(s.getId())) {
			symbols.put(s.getId(), s);
		}
		return s.getId();
	}
	
	public void put(Predicate p) {
		this.predicates.add(p);
	}
		
	public Set<Predicate> getPredicates() {
		return this.predicates;
	}
	
	public boolean containsId(int i) {
		return this.symbols.containsKey(i);
	}
	
	public boolean contains(String s) {
		return this.symbols.containsKey(new Component(s).getId());
	}
	
	public Component get(int i) {
		return this.symbols.get(i);
	}
	
	public int[] getIds() {
		Integer[] ids = new Integer[this.symbols.keySet().size()];
		this.symbols.keySet().toArray(ids);
		return ArrayUtils.toPrimitive(ids);
	}
	
	public Component[] getSymbols() {
		Component[] s = new Component[this.symbols.keySet().size()];
		return this.symbols.values().toArray(s);
	}
	
	public int getId(String s) {
		
		/*
		 * get b's id from the symbol
		 */

		if(this.symbols.containsValue(new Component(s))) {
			if(this.contains(s)) {
				for(Integer i : this.symbols.keySet()) {
					Component symbol = this.symbols.get(i);
					if(symbol.getName().equalsIgnoreCase(s)) {
						return i.intValue();
					}
				}
			}
		}

//		System.out.println("NEW SYMBOL -> "+new Symbol(s).toString());
		
		/*
		 * if the symbol does not exist, 
		 * then add it to the symbol tables
		 */
		return this.put(new Component(s));
	}
	
	public void print() {
		System.out.println("**** SYMBOLS ****");
		for(int i : this.symbols.keySet()) {
			System.out.println(i+" -> "+this.symbols.get(i));
		}
		System.out.println("**** PREDICATES ****");
		Iterator<Predicate> it = this.predicates.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
	}
	
	/*
	 * methods to change the directionality of all symbols 
	 * or of a specific symbol
	 */
	public void allReverse() {
		for(int i : this.symbols.keySet()) {
			this.symbols.get(i).setForward(false);
		}
	}
	public void reverse(int a) {
		this.symbols.get(a).setForward(false);
	}
	public void allForward() {
		for(int i : this.symbols.keySet()) {
			this.symbols.get(i).setForward(true);
		}
	}
	public void forward(int a) {
		this.symbols.get(a).setForward(true);
	}

	
	/*
	 * methods to store information on regulatory interactions
	 */
	public void putInteraction(Interaction ip) {	
		if(!this.interactions.contains(ip)) {
			this.interactions.add(ip);
		}		
	}
	
	/**
	 * 
	 * @return the set of regulatory interactions
	 */
	public Set<Interaction> getInteractions() {
		return this.interactions;
	}
	

	/**
	 * 
	 * @return the Abstract Composition Tree
	 */
	public ACT getACT() {
		return this.act;
	}
	
	/*
	 * clear ... to clear all hashtables
	 */
	public void clear() {
		if(null != this.interactions) {
			this.interactions.clear();
		}
		
		if(null != this.predicates) {
			this.predicates.clear();
		}
		
		if(null != this.symbols) {
			this.symbols.clear();
		}
	}
}
