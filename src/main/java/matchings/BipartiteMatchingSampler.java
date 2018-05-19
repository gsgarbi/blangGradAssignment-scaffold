package matchings;


import java.util.*;

import org.apache.log4j.lf5.viewer.LogBrokerMonitor;

import com.mxgraph.shape.mxActorShape;

//time 91s, 73s, 68s, 83s

//import bayonet.distributions.Multinomial;
import bayonet.distributions.Random;
import bayonet.math.SpecialFunctions;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;
//import briefj.collections.UnorderedPair;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class BipartiteMatchingSampler implements Sampler {
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
	  
	  double normF, //normalizing factor for locally balanced
	  qAfterGivenBefore, 
	  qBeforeGivenAfter, 
	  logQAfterGivenBefore, 
	  logQBeforeGivenAfter;
	  int numConnected,
	  size = matching.componentSize();
	  ArrayList<Double> probVector;
	  
	  //get the current log density
	  double logDensityBefore = logDensity();
	  
	  ArrayList <BipartiteMatching> nextStates; 
	  for (int i = 0; i < size; i++) {
		  if (matching.free1().contains(i)) {
//			  #delete one edge
			  // add new matching to nextStates
//			  #get logDensity for the new matching
			  // add logDensityAfter to list of prob
			  // undo the operation
			  ;	  }

		  else {
			  for (int j: matching.free2()) {
//				  #add one edge
				  // add new matching to nextStates
//				  #get logDensityAfter for the new matching
				  // add logDensity to list of prob
				  // get 
				  // undo the operation
			  }
			  
			  Random.nextCategorical(rand, probVector)
			  // alpah = 
	 
	  
	  if (connectOp) {
		  // get logQ's if a connection was made
		  qAfterGivenBefore = Math.sqrt(1/(matching.componentSize() * matching.free2().size()));
		  numConnected = matching.componentSize() - matching.free1().size();
		  normF = Math.exp(SpecialFunctions.logBinomial(numConnected, 2)) + 
				  matching.free1().size();
		  logQAfterGivenBefore = Math.log (qAfterGivenBefore/normF);
		  
		  qBeforeGivenAfter = Math.sqrt(1/ (matching.componentSize() * matching.free2().size()));
		  numConnected = matching.componentSize() - matching.free1().size();
		  normF = Math.exp(SpecialFunctions.logBinomial(numConnected, 2)) + 
				  matching.free1().size();
		  logQBeforeGivenAfter = Math.log(qBeforeGivenAfter/normF);

		  // create part of probability vector

	  }
	  
	  else {
		  // get q's if a connection was not made
		  qAfterGivenBefore = Math.sqrt((1/matching.componentSize()));
		  numConnected = matching.componentSize() - matching.free1().size();
		  normF = Math.exp(SpecialFunctions.logBinomial(numConnected, 2)) + 
				  matching.free1().size();
		  logQAfterGivenBefore = Math.log (qAfterGivenBefore/normF);
		  
		  // if we don't make a connection operation, disconnect 
		  matching.getConnections().set(i, BipartiteMatching.FREE);
		  
		  qBeforeGivenAfter = Math.sqrt(1/ (matching.componentSize() * matching.free2().size()));
		  numConnected = matching.componentSize() - matching.free1().size();
		  normF = Math.exp(SpecialFunctions.logBinomial(numConnected, 2)) + 
				  matching.free1().size();
		  logQBeforeGivenAfter = Math.log(qBeforeGivenAfter/normF);
	  }
	  	  
	  //get our new logDensity
	  double logDensityAfter = logDensity();
	  
	  // acceptance probability
	  double acceptPr = Math.min(1.0, Math.exp(logDensityAfter + logQBeforeGivenAfter - 
			  logDensityBefore - logQAfterGivenBefore));
	  
	  if (Generators.bernoulli(rand, acceptPr)){
	  // move to next state
		  ;
		  }
	  //else, go back
	  else {
		  if (connectOp) {
			  // disconnect
			  matching.getConnections().set(i, BipartiteMatching.FREE);
		  }
		  
		  // reconnect
		  else {
			  matching.getConnections().set(i, j);
			  }
		  }
	  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
    }
  private double nchoosek(int n, int k) {
		return Math.rint(Math.exp(SpecialFunctions.logBinomial(n, k)));
	}
  
  private BipartiteMatching nextMatching(BipartiteMatching m) {;}

				  
			  
			  
			  
			  
		
		  
  }

