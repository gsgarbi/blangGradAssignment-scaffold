package matchings;

import matchings.MatchingBase;

/**
 * We use the same representation as Permutation.xtend, but now
 * allow the links stored in the field "List<Integer> connections"
 * to contain a special value, FREE = -1, which means the vertex
 * is not connected to anything.
 */
/* @Samplers(/* name is null */)
@Data
 */public class BipartiteMatching extends MatchingBase {
  public final static int FREE /* Skipped initializer because of errors */;
  
  /* @DesignatedConstructor
   */public BipartiteMatching(/* @ConstructorArg("size")  */final int componentSize) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field Multinomial is undefined"
      + "\nexpNormalize cannot be resolved");
  }
  
  /**
   * The list of vertices that are free (unlinked) in the first connected
   * component.
   */
  public /* List<Integer> */Object free1() {
    throw new Error("Unresolved compilation problems:"
      + "\nArrayList cannot be resolved to a type."
      + "\nArrayList cannot be resolved."
      + "\n..< cannot be resolved."
      + "\nThe field connections is not visible"
      + "\nInteger cannot be resolved to a type."
      + "\nThe field MatchingBase.connections refers to the missing type Object"
      + "\nget cannot be resolved"
      + "\n== cannot be resolved"
      + "\nadd cannot be resolved");
  }
  
  /**
   * The list of vertices that are free (unlinked) in the second connected
   * component (in an arbitrary but fixed order).
   */
  public /* List<Integer> */Object free2() {
    throw new Error("Unresolved compilation problems:"
      + "\nSet cannot be resolved to a type."
      + "\n..< cannot be resolved."
      + "\nArrayList cannot be resolved."
      + "\nThe field connections is not visible"
      + "\nInteger cannot be resolved to a type."
      + "\nThe field MatchingBase.connections refers to the missing type Object"
      + "\ntoSet cannot be resolved"
      + "\nremoveAll cannot be resolved");
  }
  
  public int numConnected() {
    throw new Error("Unresolved compilation problems:"
      + "\n== cannot be resolved."
      + "\n+= cannot be resolved."
      + "\nThe field connections is not visible"
      + "\nThe field MatchingBase.connections refers to the missing type Object"
      + "\nFREE cannot be resolved"
      + "\n! cannot be resolved");
  }
  
  public void sampleUniform(final /* Random */Object random) {
    throw new Error("Unresolved compilation problems:"
      + "\nList cannot be resolved to a type."
      + "\nList cannot be resolved to a type."
      + "\nThe method sort(Object) is undefined"
      + "\n< cannot be resolved."
      + "\n++ cannot be resolved."
      + "\n< cannot be resolved."
      + "\n++ cannot be resolved."
      + "\nThe field connections is not visible"
      + "\nThe field connections is not visible"
      + "\nThe field connections is not visible"
      + "\nInteger cannot be resolved to a type."
      + "\nInteger cannot be resolved to a type."
      + "\nThe field MatchingBase.connections refers to the missing type Object"
      + "\nThe field MatchingBase.connections refers to the missing type Object"
      + "\nThe field MatchingBase.connections refers to the missing type Object"
      + "\ncategorical cannot be resolved"
      + "\npermutation cannot be resolved"
      + "\npermutation cannot be resolved"
      + "\nset cannot be resolved"
      + "\nFREE cannot be resolved"
      + "\nset cannot be resolved"
      + "\nget cannot be resolved"
      + "\nget cannot be resolved");
  }
  
  private final double logNormalization;
  
  private final double[] sizeProbabilities;
  
  private double[] normalizations(final int nObject) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method newDoubleArrayOfSize(Object) is undefined"
      + "\n+ cannot be resolved."
      + "\n<= cannot be resolved."
      + "\nThe method set(int, Object) is undefined for the type double[]"
      + "\n* cannot be resolved."
      + "\nThe method logFactorial(int) is undefined"
      + "\nThe method logFactorial(int) is undefined"
      + "\n* cannot be resolved."
      + "\nThe method logFactorial(Object) is undefined"
      + "\n- cannot be resolved."
      + "\n++ cannot be resolved."
      + "\n- cannot be resolved"
      + "\n- cannot be resolved");
  }
  
  public static /* List<Integer> */Object permutation(final /* Random */Object rand, final int n, final int k) {
    throw new Error("Unresolved compilation problems:"
      + "\nList cannot be resolved to a type."
      + "\n..< cannot be resolved."
      + "\nThe method shuffle(List, Random) is undefined"
      + "\nInteger cannot be resolved to a type."
      + "\ntoList cannot be resolved"
      + "\nsubList cannot be resolved");
  }
}
