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

package org.cidarlab.minieugene.predicates;

import org.cidarlab.minieugene.dom.Component;

/**
 * Binary predicates have two operands 
 * left-hand-side (LHS) and right-hand-side (RHS) 
 * 
 * Examples of binary predicates are
 * AFTER, BEFORE, NEXTTO, MATCH, WITH, THEN
 * 
 * @author Ernst Oberortner
 */
/* 
 */
public abstract class BinaryPredicate 
	extends UnaryPredicate {
	
	private Component b;
	private int num;
	
	/**
	 * 
	 * @param lhs
	 * @param rhs
	 */
	public BinaryPredicate(Component a, Component b) {
		super(a);
		this.b = b;
		this.num = -1;
	}
	
	public BinaryPredicate(Component a, int num) {
		super(a);
		this.b = null;
		this.num = num;
	}
	
	/**
	 * 
	 * @return
	 */
	public Component getB() {
		return this.b;
	}	
	
	/**
	 * 
	 * @return
	 */
	public int getNum() {
		return this.num;
	}
}
