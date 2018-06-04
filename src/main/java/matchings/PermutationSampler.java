package matchings;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	  UnorderedPair<Integer, Integer> pair = Generators.distinctPair(rand, permutation.componentSize());
	    double logDensityBefore = logDensity();
	    Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
	    double logDensityAfter = logDensity();
	    double acceptPr = Math.min(1.0, Math.exp(logDensityAfter - logDensityBefore)); 
	    if (Generators.bernoulli(rand, acceptPr))
	      ;
	    else
	      Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
	    
	    PrintWriter writer;
		try {
			writer = new PrintWriter("samples.csv", "UTF-8");
		    writer.println("The first line");
		    writer.println("The second line");
		    writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  
		
	}

  
  
 
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
