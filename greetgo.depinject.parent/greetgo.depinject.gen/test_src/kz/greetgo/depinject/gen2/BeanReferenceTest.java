package kz.greetgo.depinject.gen2;

import kz.greetgo.depinject.gen.errors.IllegalBeanGetterArgumentType;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static kz.greetgo.depinject.gen2.TestUtil.getReturnType;
import static org.fest.assertions.api.Assertions.assertThat;

public class BeanReferenceTest {

  class SomeClass {
  }

  interface ForSimple {
    @SuppressWarnings("unused")
    SomeClass method();
  }

  @Test
  public void constructor_simple() throws Exception {

    Type type = getReturnType(ForSimple.class, "method");

    BeanReference beanReference = new BeanReference(type, "place 1");

    assertThat(beanReference.targetClass.getName()).isEqualTo(SomeClass.class.getName());
    assertThat(beanReference.isList).isFalse();
    assertThat(beanReference.place).isEqualTo("place 1");

  }


  interface ForList {
    @SuppressWarnings("unused")
    List<SomeClass> method();
  }

  @Test
  public void constructor_list() throws Exception {

    Type type = getReturnType(ForList.class, "method");

    BeanReference beanReference = new BeanReference(type, "place 2");

    assertThat(beanReference.targetClass.getName()).isEqualTo(SomeClass.class.getName());
    assertThat(beanReference.isList).isTrue();
    assertThat(beanReference.place).isEqualTo("place 2");
  }

  interface ForLeftType {
    @SuppressWarnings("unused")
    List<List<SomeClass>> method();
  }

  @Test(expectedExceptions = IllegalBeanGetterArgumentType.class)
  public void constructor_leftType() throws Exception {

    Type type = getReturnType(ForLeftType.class, "method");

    new BeanReference(type, "place 3");

  }

  interface A1_RefInterface {
  }

  class A2_BeanClass implements A1_RefInterface {
  }

  class A3_LeftClass {
  }

  @Test
  public void fillTargetCreationsFrom() throws Exception {

    BeanReference beanReference = new BeanReference(A1_RefInterface.class, "");

    BeanCreation refInterface = new BeanCreationWithDefaultConstructor(A1_RefInterface.class, true);
    BeanCreation beanClass = new BeanCreationWithDefaultConstructor(A2_BeanClass.class, true);
    BeanCreation leftClass = new BeanCreationWithDefaultConstructor(A3_LeftClass.class, true);

    //
    //
    beanReference.fillTargetCreationsFrom(Arrays.asList(beanClass, refInterface, leftClass));
    //
    //

    assertThat(beanReference.targetCreations).hasSize(2);
    assertThat(beanReference.targetCreations.get(0).beanClass.getName()).isEqualTo(A1_RefInterface.class.getName());
    assertThat(beanReference.targetCreations.get(1).beanClass.getName()).isEqualTo(A2_BeanClass.class.getName());
  }
}