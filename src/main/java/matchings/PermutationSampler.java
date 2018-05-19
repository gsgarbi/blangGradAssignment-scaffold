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

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class PermutationSampler implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable Permutation permutation;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;

  @Override
  public void execute(Random rand) {
	  // by Gio
	  
	  double logDensityBefore = logDensity();
	  double[] Qij = getQ();
	  
	  
	 // Make a move
	  int idx_ij = Generators.categorical(rand, Qij);
	  UnorderedPair<Integer, Integer> pair = possibleMoves().get(idx_ij);
	  Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
	  
  
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
     Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
   

  }
  
  
  
  private ArrayList<UnorderedPair<Integer, Integer>> possibleMoves() {
	  ArrayList<UnorderedPair<Integer, Integer>> nb = new ArrayList<UnorderedPair<Integer, Integer>>();
	  for (int i = 0; i < permutation.componentSize(); i++) {
		  for (int j = i+1; j < permutation.componentSize(); j++) {
			  UnorderedPair<Integer, Integer> pair = new UnorderedPair<Integer, Integer>(i, j);

			  nb.add(pair);
		  }
	  }
	  return nb;
  }
  
  private double[] getQ() {
	  int size = permutation.componentSize();

	  ArrayList<UnorderedPair<Integer, Integer>> nb = possibleMoves();
	 
	  ArrayList<Double> q = new ArrayList<Double>(size*(size-1)/2);
	  Double sum = 0.0;  
   for (UnorderedPair<Integer, Integer> p: nb) {
	   Collections.swap(permutation.getConnections(), p.getFirst(), p.getSecond());
	   double prob = Math.exp(0.5*logDensity());
	   Collections.swap(permutation.getConnections(), p.getFirst(), p.getSecond());
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
