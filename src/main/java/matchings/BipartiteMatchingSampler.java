package matchings;

import java.util.List;

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
public class BipartiteMatchingSamplerBLBadAlg implements Sampler {
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
	  int numConnected;
	  
	  //get the current log density
	  double logDensityBefore = logDensity();
  
	  // choose randomly one vertex in the first component and a free vertex in component 2
	  int i = Generators.discreteUniform(rand, 0, matching.componentSize());
	  int j = 0; 
	  if (matching.free2().isEmpty())
		  ;
	  else {
	  int k = Generators.discreteUniform(rand, 0, matching.free2().size());
	  j = matching.free2().get(k);
	  }
	  
	  //if that vertex is not connected, decide to make a connection operation
	  boolean connectOp = matching.free1().contains(i);
	  
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

		  // Propose a matching by creating and edge between i and j
		  matching.getConnections().set(i,j);
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
  }
