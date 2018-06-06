package matchings;

import matchings.MatchingBase;

/**
 * A permutation or equivalently, a bipartite perfect
 * matching.
 * 
 * The annotation "@Samplers" links the data type with the appropriate sampler.
 * 
 * The annotation "@Data" is not related to data in the statistical
 * sense but rather read as 'data class', meaning that .equals, .hashcode
 * are automatically implemented, as well as other nice defaults
 * (see the xtend documentation for details).
 */
/* @Samplers(/* name is null */)
@Data
 */public class Permutation extends MatchingBase {
  /**
   * Sample an independent uniform permutation in place.
   */
  public void sampleUniform(final /* Random */Object random) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method sort(List) is undefined"
      + "\nThe method shuffle(List, Random) is undefined"
      + "\nThe field connections is not visible"
      + "\nThe field connections is not visible"
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\nThe field MatchingBase.connections refers to the missing type List");
  }
  
  public void sampleNew(final int[] l) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method replaceAll(List, int, int) is undefined"
      + "\nThe field connections is not visible"
      + "\nThe field MatchingBase.connections refers to the missing type List");
  }
  
  public /* List<Integer> */Object swapConnections(final int i, final int j) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method swap(List, int, int) is undefined"
      + "\nThe field connections is not visible"
      + "\nThe field connections is not visible"
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\nThe field MatchingBase.connections refers to the missing type List");
  }
}
