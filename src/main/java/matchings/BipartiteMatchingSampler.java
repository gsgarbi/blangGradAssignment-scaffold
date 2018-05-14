package matchings;

import java.util.List;

import bayonet.distributions.Multinomial;
import bayonet.distributions.Random;
import bayonet.math.SpecialFunctions;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;

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
	  // Example: 1 4 -1 -1 2 -> 2 3 -1 0 -1 (-1: unlinked)
	  BipartiteMatching currentMatching = matching;
	  double currentDensity = logDensity();
	  
	  matching.sampleUniform(rand);
	  double proposedDensity = logDensity(); //

	  double alpha = Math.min(1, Math.exp(proposedDensity)/Math.exp(currentDensity));

	  
	  if (! rand.nextBernoulli(alpha)) {
		  matching = currentMatching;
		  }
	  
	 
    
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
