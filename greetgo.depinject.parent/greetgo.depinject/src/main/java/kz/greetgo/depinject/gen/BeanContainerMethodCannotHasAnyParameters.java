package kz.greetgo.depinject.gen;

import java.lang.reflect.Method;

public class BeanContainerMethodCannotHasAnyParameters extends RuntimeException {
  
  public final Method method;
  
  public BeanContainerMethodCannotHasAnyParameters(Method method) {
    super(method.toString());
    this.method = method;
  }
}
