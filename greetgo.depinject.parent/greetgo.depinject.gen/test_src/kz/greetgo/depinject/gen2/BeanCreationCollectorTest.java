package kz.greetgo.depinject.gen2;

import kz.greetgo.depinject.core.BeanContainer;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.depinject.gen.errors.FactoryMethodCannotHaveAnyArguments;
import kz.greetgo.depinject.gen.errors.NoBeanConfig;
import kz.greetgo.depinject.gen.errors.NoBeanContainer;
import kz.greetgo.depinject.gen.errors.NoInclude;
import kz.greetgo.depinject.gen2.test_beans001.BeanConfig001;
import kz.greetgo.depinject.gen2.test_beans002.BeanConfig002;
import kz.greetgo.depinject.gen2.test_beans003.Bean1;
import kz.greetgo.depinject.gen2.test_beans003.Bean2;
import kz.greetgo.depinject.gen2.test_beans003.BeanConfig003;
import kz.greetgo.depinject.gen2.test_beans003.BeanFactory;
import kz.greetgo.depinject.gen2.test_beans004.BeanConfig004;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class BeanCreationCollectorTest {

  public interface BeanContainerWithoutNoBeanContainer {
  }

  @Test(expectedExceptions = NoBeanContainer.class)
  public void collectFrom_NoBeanContainer() throws Exception {
    //
    //
    BeanCreationCollector.collectFrom(BeanContainerWithoutNoBeanContainer.class);
    //
    //
  }

  public interface BeanContainerWithoutInclude extends BeanContainer {
  }

  @Test(expectedExceptions = NoInclude.class)
  public void collectFrom_NoInclude() throws Exception {
    //
    //
    BeanCreationCollector.collectFrom(BeanContainerWithoutInclude.class);
    //
    //
  }

  class BeanConfigWithoutBeanConfig {
  }

  @Include(BeanConfigWithoutBeanConfig.class)
  public interface ForNoBeanConfigError extends BeanContainer {
  }

  @Test(expectedExceptions = NoBeanConfig.class)
  public void collectFrom_NoBeanConfig() throws Exception {
    //
    //
    BeanCreationCollector.collectFrom(ForNoBeanConfigError.class);
    //
    //
  }

  @Include(BeanConfig001.class)
  public interface HasBeanWithDefaultConstructor extends BeanContainer {
  }

  private static Map<String, BeanCreation> toMapSimple(List<BeanCreation> list) {
    Map<String, BeanCreation> ret = new HashMap<>();
    list.forEach(bc -> ret.put(bc.beanClass.getSimpleName(), bc));
    return ret;
  }

  @Test
  public void collectFrom_BeanWithDefaultConstructor() throws Exception {
    //
    //
    List<BeanCreation> list = BeanCreationCollector.collectFrom(HasBeanWithDefaultConstructor.class);
    //
    //

    assertThat(list).hasSize(2);

    Map<String, BeanCreation> map = toMapSimple(list);

    assertThat(map.get("BeanWithDefaultConstructor1")).isInstanceOf(BeanCreationWithDefaultConstructor.class);
    assertThat(map.get("BeanWithDefaultConstructor2")).isInstanceOf(BeanCreationWithDefaultConstructor.class);

    assertThat(map.get("BeanWithDefaultConstructor1").singleton).isTrue();
    assertThat(map.get("BeanWithDefaultConstructor2").singleton).isFalse();
  }

  @Include(BeanConfig002.class)
  interface WithoutBeanScanner extends BeanContainer {
  }

  @Test
  public void collectFrom_withoutBeanScanner() throws Exception {
    //
    //
    List<BeanCreation> list = BeanCreationCollector.collectFrom(WithoutBeanScanner.class);
    //
    //

    assertThat(list).isEmpty();
  }

  @Include(BeanConfig003.class)
  interface FactoryMethodBeanContainer extends BeanContainer {
  }

  @Test
  public void collectFrom_factoryMethod() {
    //
    //
    List<BeanCreation> list = BeanCreationCollector.collectFrom(FactoryMethodBeanContainer.class);
    //
    //

    assertThat(list).hasSize(3);

    Map<String, BeanCreation> map = toMapSimple(list);

    assertThat(map.get(BeanFactory.class.getSimpleName())).isInstanceOf(BeanCreationWithDefaultConstructor.class);
    assertThat(map.get(Bean1.class.getSimpleName())).isInstanceOf(BeanCreationWithFactoryMethod.class);
    assertThat(map.get(Bean2.class.getSimpleName())).isInstanceOf(BeanCreationWithFactoryMethod.class);

    BeanCreationWithFactoryMethod bc = (BeanCreationWithFactoryMethod) map.get(Bean1.class.getSimpleName());

    assertThat(bc.factorySource.beanClass.getName()).isEqualTo(BeanFactory.class.getName());
    assertThat(bc.factoryMethod.getName()).isEqualTo("createBean1");

    assertThat(map.get(BeanFactory.class.getSimpleName()).singleton).isTrue();
    assertThat(map.get(Bean1.class.getSimpleName()).singleton).isTrue();
    assertThat(map.get(Bean2.class.getSimpleName()).singleton).isFalse();
  }

  @Include(BeanConfig004.class)
  interface BeanFactoryMethodCannotHasAnyArgumentsBeanContainer extends BeanContainer {
  }

  @Test(expectedExceptions = FactoryMethodCannotHaveAnyArguments.class)
  public void collectFrom_BeanFactoryMethodCannotHasAnyArguments() throws Exception {
    //
    //
    BeanCreationCollector.collectFrom(BeanFactoryMethodCannotHasAnyArgumentsBeanContainer.class);
    //
    //
  }
}