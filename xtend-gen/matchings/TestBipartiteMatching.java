package matchings;

import matchings.BipartiteMatching;
import matchings.UniformBipartiteMatching;

public class TestBipartiteMatching {
  private final static int size = 5;
  
  private final static UniformBipartiteMatching unifMatchingModel = new UniformBipartiteMatching.Builder().setMatching(new BipartiteMatching(TestBipartiteMatching.size)).build();
  
  private final static /* DiscreteMCTest */Object test /* Skipped initializer because of errors */;
  
  /* @Test
   */public void invariance() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field TestBipartiteMatching.test refers to the missing type DiscreteMCTest"
      + "\ncheckInvariance cannot be resolved");
  }
  
  /* @Test
   */public void irreducibility() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field TestBipartiteMatching.test refers to the missing type DiscreteMCTest"
      + "\ncheckIrreducibility cannot be resolved");
  }
}
