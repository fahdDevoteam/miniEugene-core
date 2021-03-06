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

package org.cidarlab.minieugene.solver.jacop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cidarlab.minieugene.dom.Component;

/**
 * The SolutionPoolManager is intended to avoid running 
 * out of memory because of a large number of solutions.
 * 
 * The SolutionPoolManager is a Singleton and manages a pool of solutions 
 * (i.e. Component[]).
 * During runtime, we always keep only a fixed number of solutions in-memory.
 * The limit of the solutions is configurable (initialPoolSize).
 * 
 * If the limit of solutions is reached, we store the solutions 
 * into Sparrow.
 *  
 * The idea was taken from:
 * J. Shirazi, "Java Performance Tuning", 
 * Chapter 4: "Object Creation"
 * http://oreilly.com/catalog/javapt/chapter/ch04.html#_Toc452891296
 * 
 * @author Ernst Oberortner
 *
 */
public class SolutionPoolManager {

	private ArrayList<Component[]> pool;
	private boolean[] inUse;
	private int poolSize;
	
	private long solution_counter;
	private int N;
	
	//
	private List<Component[]> solutions;
	
	protected SolutionPoolManager(int poolSize, int N) {

		this.poolSize = poolSize;
		this.N = N;
		// add the start up, we create poolSize solution (i.e. Component[] arrays)	
		
		/*
		 * for lazy initialization we init all list elements 
		 * of the pool with NULL
		 */
		pool = new ArrayList<Component[]>(
				Collections.nCopies(poolSize, (Component[])null));
		inUse = new boolean[poolSize];
		
		for(int i=0; i<poolSize; i++) {
			inUse[i] = false;
		}

		
		solution_counter = 0;
	}
	
	
	public Component[] getFreeSpot() {
		for(int i=0; i<this.poolSize; i++) {
			if(!inUse[i]) {
				
				/*
				 * LAZY INITIALIZATION
				 */
				if(null == pool.get(i)) {
					pool.set(i, new Component[N]);
				}
				
				inUse[i] = true;
				return pool.get(i);
			}
		}

		this.free();
		
		return this.getFreeSpot();
	}
	
	public void putSolution(Component[] solution) {
		for(int i=0; i<this.poolSize; i++) {
			if(!inUse[i]) {
				pool.set(i, solution);
				inUse[i] = true;

				solution_counter ++;
				
				return;
			}
		}
		
//		System.out.println("[putSolution] we're full!");
		this.free();

		this.putSolution(solution);
	}
	
	private void free() {
		/*
		 * it's time to serialize!
		 */
		if(null == this.solutions) {
			this.solutions = new ArrayList<Component[]>();
		}
		this.solutions.addAll(this.pool);
		
//		System.out.println("*** it's time to serialize ***");
		for(int i=0; i<this.poolSize; i++) {
//			System.out.println(Arrays.toString(pool.get(i)));
			
			inUse[i] = false;
		}
		
//		pool.clear();
//		pool = new ArrayList<Component[]>(
//				Collections.nCopies(poolSize, (Component[])null));
	}
	
	public List<Component[]> getSolutions() {
		/*
		 * 
		 */
		return this.solutions;
	}
	
	public long getCurrentNumberOfSolutions() {
		return this.solution_counter;
	}
}
