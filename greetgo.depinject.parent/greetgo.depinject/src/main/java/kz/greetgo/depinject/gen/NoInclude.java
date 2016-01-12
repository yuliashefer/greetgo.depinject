package kz.greetgo.depinject.gen;

public class NoInclude extends RuntimeException {
  
  public final Class<?> mustContainingClass;
  
  public NoInclude(Class<?> mustContainingClass) {
    super(" in " + mustContainingClass.getSimpleName());
    this.mustContainingClass = mustContainingClass;
  }
}
