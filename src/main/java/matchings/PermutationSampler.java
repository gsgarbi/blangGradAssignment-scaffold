package matchings;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.internal.formatter.FormatJavadocBlock;
import org.jgraph.graph.ConnectionSet;

import bayonet.math.SpecialFunctions;

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
	  Permutation currentPerm = permutation;
	  double currentDensity = logDensity();
	  
	  permutation.sampleUniform(rand);
	  double proposedDensity = - SpecialFunctions.logFactorial(permutation.componentSize()); 

	  double alpha = Math.min(1, Math.exp(proposedDensity)/Math.exp(currentDensity));

	  
	  if (! rand.nextBernoulli(alpha)) {
		  permutation = currentPerm;
		  }
	  
		
	}

  
  
 
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
