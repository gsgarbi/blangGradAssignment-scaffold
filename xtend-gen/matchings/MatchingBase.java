package matchings;

/**
 * Shared functionality and representation used by Permutation and
 * BipartiteMatching.
 */
/* @Data
 */public abstract class MatchingBase /* implements TidilySerializable  */{
  /**
   * Assume the vertices are indexed 0, 1, ..., N in the first bipartite component, and
   * also 0, 1, 2, .., N in the second bipartite component.
   * 
   * For vertex i in the first component, connections.get(i)
   * give the index in the second bipartite component.
   */
  private final /* List<Integer> */Object connections;
  
  /**
   * Initialize to the identity permutation.
   */
  public MatchingBase(final int componentSize) {
    throw new Error("Unresolved compilation problems:"
      + "\n..< cannot be resolved."
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\ntoList cannot be resolved");
  }
  
  /**
   * The number of vertices in each of the two bipartite components.
   */
  public int componentSize() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\nsize cannot be resolved");
  }
  
  public /* String */Object gioPrint() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\ntoString cannot be resolved");
  }
  
  public /* String */Object toString() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\ntoString cannot be resolved");
  }
  
  /**
   * Used to output samples into a tidy format.
   */
  public void serialize(final /* Context */Object context) {
    throw new Error("Unresolved compilation problems:"
      + "\n..< cannot be resolved."
      + "\nThe field MatchingBase.connections refers to the missing type List"
      + "\nrecurse cannot be resolved"
      + "\nget cannot be resolved");
  }
}
