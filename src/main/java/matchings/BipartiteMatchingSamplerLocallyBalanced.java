package matchings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.net.QCodec;

import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;
import briefj.collections.UnorderedPair;

//import briefj.collections.UnorderedPair;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class BipartiteMatchingSamplerLocallyBalanced implements Sampler {
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
		double logDensityBefore = logDensity();
		double[] Qij = getQ();

		// Make a move
		int idx_ij = Generators.categorical(rand, Qij);
		UnorderedPair<Integer, Integer> pair = possibleMoves().get(idx_ij);
		Collections.swap(matching.getConnections(), pair.getFirst(), pair.getSecond());

		double logDensityAfter = logDensity();
		double[] Qji = getQ();

		int idx_ji = -1;
		for (int i = 0; i < Qji.length; i++) {
			if (possibleMoves().get(i).equals(pair))
				idx_ji = i;
		}

		double acceptPr = Math.min(1.0, Math.exp(logDensityAfter - logDensityBefore + Math.log(Qji[idx_ji]) - Math.log(Qij[idx_ij])));

		if (Generators.bernoulli(rand, acceptPr))
			;
		else
			Collections.swap(matching.getConnections(), pair.getFirst(), pair.getSecond());
	}


	private ArrayList<UnorderedPair<Integer, Integer>> possibleMoves() {
		// input: 
		// output: all possible next states
		//         ArrayList<UnorderedPair<Integer, Integer>> (vector of pairs of integers)

		ArrayList<UnorderedPair<Integer, Integer>> nb = new ArrayList<UnorderedPair<Integer, Integer>>();
		for (int i = 0; i < matching.componentSize(); i++) {
			if (matching.free1().contains(i)) {
				UnorderedPair<Integer, Integer> pair = new UnorderedPair<Integer, Integer>(i, -1);
				nb.add(pair);
			}
			else {
				for (int j: matching.free2()) {
					UnorderedPair<Integer, Integer> pair = new UnorderedPair<Integer, Integer>(i, j);
					nb.add(pair);
				}
			}
		}
		return nb;
	}


	private double[] getQ() {
		// input:
		// output: normalized proposed distribution

		ArrayList<UnorderedPair<Integer, Integer>> nb = possibleMoves();

		ArrayList<Double> q = new ArrayList<Double>();
		Double sum = 0.0;  
		for (UnorderedPair<Integer, Integer> p: nb) {
			int before = matching.getConnections().get(p.getFirst());
			matching.getConnections().set(p.getFirst(), p.getSecond());
			double prob = Math.exp(0.5*logDensity());
			matching.getConnections().set(p.getFirst(), before);
			q.add(prob/nb.size());
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
