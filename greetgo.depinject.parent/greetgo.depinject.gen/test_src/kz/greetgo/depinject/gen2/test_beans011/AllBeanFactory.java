package kz.greetgo.depinject.gen2.test_beans011;

import kz.greetgo.depinject.core.Bean;

@Bean
public class AllBeanFactory {

  @Bean
  public BeanA3_beanFactory createBeanA3_beanFactory() {
    return new BeanA3_beanFactory();
  }
}