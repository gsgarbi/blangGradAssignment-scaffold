package matchings;

import java.util.List;

import bayonet.distributions.Multinomial;
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


	  // current log pi density
	  double logPiX = logDensity();

	  // uniformly pick a vertex and check if it is matched	  
	  double logQXY, logQYX;
	  int i = rand.nextInt(matching.componentSize());
	  int j = 0;
	  List<Integer> free1 = matching.free1();
	  List<Integer> free2 = matching.free2();
	  
	  boolean isFree = free1.contains(i);
	  if (isFree) {
		  // add edge
		  j = free2.get(rand.nextInt(free2.size()));
		  matching.getConnections().set(i, j);
		  logQYX = - Math.log((matching.componentSize() * free2.size()));
		  logQXY = - Math.log(matching.componentSize());
	  } else {
		  // remove edge
		  j = matching.getConnections().get(i);
		  matching.getConnections().set(i, BipartiteMatching.FREE);
	  	  logQYX = - Math.log((matching.componentSize()));
		  logQXY = - Math.log((matching.componentSize() * free2.size()));
	  }
	  		
	  // proposed log pi density
	  double logPiY = logDensity();

	  // calculate acceptance rate and accept or reject.
	  double alpha = Math.min(1, Math.exp(logPiY - logPiX + logQXY - logQYX));
//	  System.out.println(alpha);

	  boolean accept = Generators.bernoulli(rand, alpha);
	  
	  if (accept) {
		  ; // we already made the move.
	  } else {
		  if (isFree){
			  // remove proposed edge
			  matching.getConnections().set(i, BipartiteMatching.FREE);
		  } else {
			  // rebuild current edge
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