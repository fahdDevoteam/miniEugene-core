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

package org.cidarlab.minieugene.predicates.orientation;

import org.cidarlab.minieugene.constants.RuleOperator;
import org.cidarlab.minieugene.dom.Component;
import org.cidarlab.minieugene.dom.Identified;
import org.cidarlab.minieugene.exception.MiniEugeneException;
import org.cidarlab.minieugene.predicates.ConstraintOperand;
import org.cidarlab.minieugene.predicates.UnaryConstraint;
import org.cidarlab.minieugene.solver.jacop.Variables;
import org.jacop.constraints.And;
import org.jacop.constraints.IfThen;
import org.jacop.constraints.Not;
import org.jacop.constraints.Or;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Reified;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XneqC;
import org.jacop.core.BooleanVar;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

/*
 * ALTERNATE a
 *
 * i in {1, ..., N-1}
 * for all i: orientation(i) != orientation(i+1)
 * 
 */
public class AlternateOrientation 
	extends UnaryConstraint 
	implements OrientationConstraint {

	public AlternateOrientation(ConstraintOperand rhs) {
		super(rhs);
	}

	@Override
	public String getOperator() {
		return RuleOperator.ALTERNATE_ORIENTATION.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getOperator()).append(" ").append(this.getA());
		return sb.toString();
	}

	@Override
	public PrimitiveConstraint toJaCoP(Store store, IntVar[][] variables) 
				throws MiniEugeneException {
		
		if(null != this.getA()) {
			return alternate(store, variables, this.getA().getOperand());
		} else {
			return alternate(store, variables);
		}

	}
	
	/**
	 * to alternate the orientation of the component c
	 * 
	 * @param store
	 * @param variables
	 * @param c
	 * @return
	 */
	private PrimitiveConstraint alternate(Store store, IntVar[][] variables, Identified id) {
		
		/*
		 * here, we need memory of the orientation of c's last occurrence
		 */
		IntVar iVar = null;
		if(null == (iVar = (IntVar)store.findVariable(id.getName()+"-is-forward"))) {
			iVar = new IntVar(store, id.getName()+"-is-forward");
			iVar.addDom(0, 1);
		}
		
		
		int N = variables[Variables.ORIENTATION].length;
		
		BooleanVar[] isC = new BooleanVar[N];
		BooleanVar[] isForward = new BooleanVar[N];
		
		PrimitiveConstraint[] pc = new PrimitiveConstraint[N];
		for(int i=0; i<N; i++) {
			
			isC[i] = new BooleanVar(store, "is"+id.getName());
			isForward[i] = new BooleanVar(store, id.getName()+".isForward");
			
			/*
			 * IF c(i) /\ forward(i) THEN
			 * 		id.isForward(i) = TRUE
			 * ELSE
			 * 		id.isForward(i) = FALSE
			 */
			
			pc[i] = new Or(
						new IfThen(
								new XeqC(iVar, 0),
								new And( 
										new And(new XeqC(variables[Variables.PART][i], id.getId()), new  XeqC(variables[Variables.ORIENTATION][i], 1)),
										new XeqC(iVar, 1))),
						new IfThen(
								new XeqC(iVar, 1),
								new And( 
										new And(new XeqC(variables[Variables.PART][i], id.getId()), new  XeqC(variables[Variables.ORIENTATION][i], -1) ),
										new XeqC(iVar, 0)))
						);
		}
		
		
		return new And(pc);
	}
	
	/**
	 * to alternate all components of the design
	 * 
	 * @param store
	 * @param variables
	 * @return
	 */
	private PrimitiveConstraint alternate(Store store, IntVar[][] variables) {
		
		int N = variables[Variables.ORIENTATION].length;
		

		/*
		 * 
		 * IF orientation(i) == 1 THEN orientation(i+1) != 1 
		 * OR
		 * IF orientation(i) == -1 THEN orientation(i+1) != -1
		 * 
		 * 0 <= i < N
		 */
		PrimitiveConstraint pcForward[] = new PrimitiveConstraint[N];
		for(int i=0; i<N; i++) {
			if(i%2==0) {
				pcForward[i] = new XeqC(variables[Variables.ORIENTATION][i], 1);				
			} else {
				pcForward[i] = new XneqC(variables[Variables.ORIENTATION][i], 1);
			}
		}
		PrimitiveConstraint pcReverse[] = new PrimitiveConstraint[N];
		for(int i=0; i<N; i++) {
			if(i%2==0) {
				pcReverse[i] = new XeqC(variables[Variables.ORIENTATION][i], -1);				
			} else {
				pcReverse[i] = new XneqC(variables[Variables.ORIENTATION][i], -1);
			}
		}
		
		return new Or(new And(pcForward), new And(pcReverse));
	}

	@Override
	public PrimitiveConstraint toJaCoPNot(Store store, IntVar[][] variables)
			throws MiniEugeneException {
		return new Not((PrimitiveConstraint)this.toJaCoP(store, variables));
	}
	
}
