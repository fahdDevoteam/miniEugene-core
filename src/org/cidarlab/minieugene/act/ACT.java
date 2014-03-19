package org.cidarlab.minieugene.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cidarlab.minieugene.dom.Component;
import org.cidarlab.minieugene.exception.ACTException;
import org.cidarlab.minieugene.exception.EugeneException;
import org.cidarlab.minieugene.predicates.LogicalOr;
import org.cidarlab.minieugene.predicates.Predicate;
import org.cidarlab.minieugene.predicates.counting.BinaryContains;
import org.cidarlab.minieugene.predicates.counting.Contains;
import org.cidarlab.minieugene.predicates.counting.CountingPredicate;

public class ACT {

	/**
	 * the DAG
	 * 
	 * key   ... node
	 * value ... edges to the node's successors 
	 */
	private Map<Component, ArrayList<Component>> dg;
	
	/**
	 * cycles contains a list of components 
	 * that cause a cycle
	 */
	private List<Component> cycles;
	
	public ACT() {
		//this.root = null;
		this.dg = new HashMap<Component, ArrayList<Component>>();
	}
	
	public void constructACT(List<Predicate> predicates) 
			throws EugeneException {
		
		// first, we build a list of all
		// binary contains constraints
		List<CountingPredicate> lst = new ArrayList<CountingPredicate>();
		for(Predicate p : predicates) {
			if(p instanceof BinaryContains || p instanceof Contains) {
				lst.add((CountingPredicate)p);
			} else if(p instanceof LogicalOr) {
				for(Predicate subp : ((LogicalOr)p).getPredicates()) {
					if(subp instanceof BinaryContains || p instanceof Contains) {
						lst.add((CountingPredicate)subp);
					}
				}
			}
		}	

		// build the tree
		try {
			this.buildTree(lst);
			
			// sort the tree
			try {
				this.sortTree();
			} catch(ACTException e) {
				this.cycles = e.getCycles();
				throw new EugeneException("Cyclic Design!");
			}
			
		} catch(EugeneException ee) {
			throw new EugeneException(ee.getMessage());
		}
		
	}
	
	private void buildTree(List<CountingPredicate> lst) 
			throws EugeneException {
		
		/*
		 * first, we build the DG
		 * based on the binary and unary CONTAINS predicates
		 */
		for(CountingPredicate cp : lst) {
			
			if(cp instanceof BinaryContains) {
				BinaryContains bc = (BinaryContains)cp;

				ArrayList<Component> childs = new ArrayList<Component>();
				if(this.dg.containsKey(bc.getA())) {
					childs = this.dg.get(bc.getA());
					childs.add(bc.getB());
				} else {
					childs.add(bc.getB());				
				}
				
				this.dg.put(bc.getA(), childs);
				if(!this.dg.containsKey(bc.getB())) {
					this.dg.put(bc.getB(), new ArrayList<Component>());
				}
			} else if(cp instanceof Contains) {
				Contains c = (Contains)cp;
				if(!this.dg.containsKey(c.getA())) {
					this.dg.put(c.getA(), new ArrayList<Component>());
				}
			}
		}
		

	}
	
	public void sortTree() 
			throws ACTException {
		TSort.tSort(this.dg);
	}
	
	public void printTree() {
		for(Component c : this.dg.keySet()) {
			System.out.print(c.getName()+" -> {");
			for(int i=0; i<this.dg.get(c).size(); i++) {
				System.out.print(this.dg.get(c).get(i).getName()+", ");
			}
			System.out.println("}");
		}
	}
	
	/**
	 * 
	 * @return true if the ACT is acyclic
	 *         false otherwise
	 */
	public boolean isCyclic() {
		return this.cycles==null || this.cycles.isEmpty();
	}
	
	private static final String NEWLINE = System.getProperty("line.separator");	
	public String toGraphViz() 
			throws EugeneException {
		
		// print the Tree
		StringBuilder sb = new StringBuilder();
		sb.append("digraph DESIGN {").append(NEWLINE);
		
		// first, let's check if there are any circles
		// in order to red color the involved nodes
		if(null != this.cycles) {
			sb.append("node [color=red];").append(NEWLINE);
			for(Component c : this.cycles) {
				sb.append(c.getName()).append(";").append(NEWLINE);
			}
			sb.append("edge [color=red];").append(NEWLINE);
					
		}
		
		Iterator<Component> it = this.dg.keySet().iterator();
		while(it.hasNext()) {
			Component source = it.next();
			if(!this.dg.get(source).isEmpty()) {
				for(Component child : this.dg.get(source)) {
					sb.append(source.getName()).append(" -> ");
					sb.append(child.getName()).append(";").append(NEWLINE);
				}
			} else {
				sb.append(source.getName()).append(";").append(NEWLINE);
			}
		}
		
		sb.append("}");

		return sb.toString();
	}
	
}