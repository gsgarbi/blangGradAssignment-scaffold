package matchings;

import java.util.Collections;
import java.util.List;

import bayonet.distributions.Multinomial;
import bayonet.distributions.Random;
import bayonet.math.SpecialFunctions;
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
	  
	  //get the current log density
	  double logDensityBefore = logDensity();
	  
	  // choose randomly one vertex in the first component and a free vertex in component 2
	  int i = Generators.discreteUniform(rand, 0, matching.componentSize());
	  int j = -7;  // initialize it. initial value won't be used.
	  if (matching.free2().isEmpty())
		  ;
	  else {
	  int k = Generators.discreteUniform(rand, 0, matching.free2().size());
	  j = matching.free2().get(k);
	  }
	  
	  //if that vertex is not connected, decide to make a connection operation
	  boolean connectOp = matching.free1().contains(i);
	  
	  // if I dont decide to make a connection operation, disconnect 
	  // and get q's if a connection was not made

		  double logQAfterGivenBefore = - Math.log(matching.componentSize());		  
		  matching.getConnections().set(i, BipartiteMatching.FREE);
		  double logQBeforeGivenAfter = - Math.log(matching.componentSize()* 1/matching.free2().size());
	  
	  // else, choose to connect to one of the free vertexes of the component 2
	  if (connectOp) {
		  // q's if a connection was made
		  logQBeforeGivenAfter = - Math.log(matching.componentSize());
		  logQAfterGivenBefore = - Math.log(matching.componentSize() * 1/matching.free2().size());

		  matching.getConnections().set(i,j);
	  }
	  
	  //get our new logDensity
	  double logDensityAfter = logDensity();
	  
	  // get acceptance probability
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
