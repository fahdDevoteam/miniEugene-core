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

package org.cidarlab.minieugene.examples;

import java.net.URI;

import org.cidarlab.minieugene.MiniEugene;
import org.cidarlab.minieugene.exception.MiniEugeneException;
import org.cidarlab.minieugene.util.SolutionExporter;

/**
 * In this example we demonstrate how to utilize miniEugene's 
 * SolutionExporter in order to visualize the solutions using Pigeon (pigeoncad.org),
 * export the solutions to SBOL (sbolstandard.org), and to generate a 
 * Eugene header file (eugenecad.org). 
 * <p>
 * In general, the utilization of miniEugene is divided into the following steps:<br/>
 * 1. instantiate MiniEugene<br/>
 * 2. specify an String array, each row containing a miniEugene rule<br/>
 * 3. let miniEugene solve the rules (using MiniEugene's solve() method)<br/>
 * 4. process the solutions (maybe by using the SolutionExporter)<br/>
 *    and/or have a look into the statistics of the solving process<br/>
 *
 * @author Ernst Oberortner
 *
 */
public class SolutionExportExample {

	public static void main(String[] args) {
		/*
		 * STEP 1:
		 * Instantiating miniEugene
		 */
		MiniEugene me = new MiniEugene();
		
		/*
		 * STEP 2:
		 * specify some rules and put them 
		 * into a String array (String[])
		 */
		String[] rules = {
				"CONTAINS a", "CONTAINS b", "CONTAINS c", 
				"CONTAINS d", "CONTAINS e", "CONTAINS f", 
				"CONTAINS g", "CONTAINS h", "CONTAINS i",
				"CONTAINS j", "CONTAINS k", "CONTAINS l", 
				"a BEFORE b", "b BEFORE c", "c BEFORE d",
				"d BEFORE e", "e BEFORE f", "f BEFORE g",
				"g BEFORE h", "h BEFORE i", "i BEFORE j",
				"j BEFORE k", "k BEFORE l"};
		try {
			
			/* STEP 3:
			 * Ask miniEugene to solve the constraints
			 * for a design length of 12 and
			 * we'd like to have 20 solutions
			 */
			me.solve(rules, 12, 20);

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		/* STEP 4:
		 * we instantiate miniEugene's SolutionExporter
		 */
		SolutionExporter se = new SolutionExporter(
				me.getSolutions(), me.getInteractions());
		
		/*
		 * we visualize the solutions using Pigeon
		 */
		try {
			URI pigeonURI = se.toPigeon();
			System.out.println(pigeonURI.toString());
//			WeyekinPoster.launchPage(pigeonURI);
		} catch(MiniEugeneException ee) {
			ee.printStackTrace();
		}

		/*
		 * we export the solutions to SBOL
		 */
		try {
			se.toSBOL("./examples/solutions.sbol");
		} catch(MiniEugeneException ee) {
			ee.printStackTrace();
		}

		/*
		 * we export the solutions to Eugene
		 */
		try {
			se.toEugene("./examples/solutions.eug");
		} catch (MiniEugeneException e) {
			e.printStackTrace();
		}
	}

}
