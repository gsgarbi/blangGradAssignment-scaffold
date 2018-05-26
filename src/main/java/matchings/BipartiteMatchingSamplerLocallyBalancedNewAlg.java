package matchings;

import java.util.ArrayList;
//import java.util.Collection;
import java.util.Collections;
import java.util.List;

//import org.apache.commons.codec.net.QCodec;

import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;
import briefj.collections.UnorderedPair;

import matchings.Pair;



/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class BipartiteMatchingSamplerLocallyBalancedNewAlg implements Sampler {
	/**
	 * This field will be populated automatically with the 
	 * permutation being sampled. 
	 */
	@SampledVariable BipartiteMatching matching;
	/**
	 * This will contain all the elements of the prior or likelihood 
	 * (collectively, factors), that depend on the permutation being 
	 * resampled. 
	 */
	@ConnectedFactor List<LogScaleFactor> numericFactors;
	@Override
	public void execute(Random rand) {

		// Trying to implement ideas from Informed proposals for local MCMC in discrete spaces
		System.out.println("--------- File: BipartiteMatchingSamplerLocallyBalanced-newAlg ---------");
		
		
		// Part1: Gather info about sigma_i
		List<Integer> connectionsBefore = matching.getConnections();
		System.out.println("connectionsBefore: " + matching.gioPrint());
		
		double logDensityBefore = logDensity();
		ArrayList<Pair> possibleMoves_ij = possibleMoves();
		double[] Qij = getQ(possibleMoves_ij);

		System.out.println("Possible moves ij: " + possibleMoves_ij);
		

		// Part2: Make a move
		int idx_ij= Generators.categorical(rand, Qij);
		System.out.println("categorically chosen position: " + idx_ij);
		
		Pair chosenMove = possibleMoves_ij.get(idx_ij);
		System.out.println("chosen move: " + chosenMove);
		
		int iBefore = chosenMove.getFirst();
		int jBefore = matching.getConnections().get(iBefore);
		Pair pairBefore = new Pair (iBefore, jBefore);
		
		System.out.println("MOVE");
		matching.getConnections().set(chosenMove.getFirst(), chosenMove.getSecond());
		
		
		// Part3: Gather info about sigma_j
		System.out.println("connectionsAfter: " + matching.gioPrint());
		
		double logDensityAfter = logDensity();
		ArrayList<Pair> possibleMoves_ji = possibleMoves();
		double[] Qji = getQ(possibleMoves_ji);
		System.out.println("Possible moves ji: " + possibleMoves_ji);
		

		// Part4.1: Find how to undo changes
		Pair undoMove = new Pair(-10,-10);
		if (chosenMove.getSecond() == -1) {
			undoMove = pairBefore;
		}
		else {
			undoMove = new Pair (iBefore, -1);
		}
		
		// Part4.2: Find Qji to recover previous state
		int idx_ji = -10; // idx_ji serves to recover Qji
		for (Pair m: possibleMoves_ji) {
			if ( (m.getFirst() == undoMove.getFirst()) & (m.getSecond() == undoMove.getSecond())){	
				idx_ji = possibleMoves_ji.indexOf(m);
			}
		}
		
		
		// Part5: Define alpha (probability of accepting proposal)
		double acceptPr = Math.min(1.0, Math.exp(logDensityAfter - logDensityBefore + Math.log(Qji[idx_ji]) - Math.log(Qij[idx_ij])));
		
		
		// Part6: Undo changes if test does not pass
		if (Generators.bernoulli(rand, acceptPr))
			;
		else
			matching.getConnections().set(undoMove.getFirst(), undoMove.getSecond());
			System.out.println("back, original: " + matching.gioPrint() + connectionsBefore);
		}

	
	// Part7: Functions
	private ArrayList<Pair> possibleMoves() {
		// input: 
		// output: all possible next states
		//         ArrayList<Pair> (vector of chosenMoves of integers)
		
		System.out.println("Start possibleMoves function for " + matching.gioPrint());
		

		ArrayList<Pair> posMoves = new ArrayList<Pair>();
		for (int i = 0; i < matching.componentSize(); i++) {
			if (!(matching.free1().contains(i))) { //check if vertex at position i in comp1 is not free
				Pair chosenMove = new Pair(i, -1); //the possible move is to delete the connection
				posMoves.add(chosenMove);
			}
			else {
				for (int j: matching.free2()) { //the possible moves are to create a connection to one of the free vertexes in comp2
					Pair chosenMove = new Pair(i, j); 
					posMoves.add(chosenMove);
				}
			}
		}
		return posMoves;
	}
	

	private double[] getQ(ArrayList<Pair> nb) {
		// input:
		// output: normalized proposed distribution

		ArrayList<Double> q = new ArrayList<Double>();
		Double sum = 0.0;  
		for (Pair p: nb) {
			int jBefore = matching.getConnections().get(p.getFirst());
			matching.getConnections().set(p.getFirst(), p.getSecond());
			double prob = Math.exp(0.5*logDensity())/nb.size();
			matching.getConnections().set(p.getFirst(), jBefore);
			q.add(prob);
			sum = sum + prob;
		}

		double[] Q = new double[q.size()];
		for (int i = 0; i < q.size(); i++) {
			Q[i] = q.get(i) / sum;
		}
		return Q;
	}


	private double logDensity() {
		double sum = 0.0;
		for (LogScaleFactor f : numericFactors)
			sum += f.logDensity();
		return sum;
	}
}
