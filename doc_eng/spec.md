### References

 - [Very quick start (using TestNG)](fast_start.md)
 - [Quick start (main function, or war file)](quick_start.md)
 - [Concept](concept.md)
 - [Specification]
   - [Creation of beans](#bean-creation)
     - [Creation of bean using bean class](#bean-creation-using-bean-class)
     - [Creation of bean using bean method](#bean-creation-using-bean-method)
     - [Creation of bean using bean factory](#bean-creation-using-bean-factory)
   - [Including beans to bean containers](#include-beans-to-bean-containers)
     - [Annotation `@BeanScanner`](#beanscanner-annotation)
     - [Annotation `@ScanPackage`](#scanpackage-annotation)
     - [Annotation `@Include`](#include-annotation)
   - [Bean containers](#bean-containers)
   - [Bean replacers](#replacers)

## Specification

Вначале необходимо почитать концепцию - там даются базовые принцыпы depinject. Также можно воспользоваться быстрым или
очень быстрым стартом, для получения общих представлений о библиотеке.

### Возможности

  - Инициализация бинов по мере необходимости (а не по мере зависимости);
  - Кодогенерация вместо рефлексии, чтобы работала оптимизация;
  - Три способа создания бинов;
  - Два способа подмены бинов (некий аналог аспектного программирования);
  
### Bean Creation

Бин - это объект, к которому можно получить доступ из бин-класса, посредством интерфейса `BeanGetter`.

Существует только три способа создания бинов:
  - посредством бин-класса;
  - посредством бин-метода;
  - посредством бин-фабрики;

#### Bean Creation using Bean Class

Бин-класс - это класс помеченный аннотацией `@Bean`. У бин-класса должен быть конструктор по умолчанию, чтобы
библиотека смогла создать инстанцию этого бина. Система создаёт инстанцию этого класса, используя конструктор
по умолчанию.

#### Bean Creation using Bean Method

Бин-метод - это публичный метод некого бина, помеченный аннотацией `@Bean`. Объект, возвращаемый этим методом
автоматически становиться бином. Так можно создавать бины без контруктора по умолчанию.

#### Bean Creation using Bean Factory

Аннотацией `@Bean` можно пометить интерфейс или абстрактный класс. В этом случае система не знает как создавать
такой бин, и ему нужна помощь. Эту помощь ему может предоставить бин-фабрика

Бин-фабрика - это бин, который реализует интерфейс `BeanFactory`, который в себе содержит один метод:

```java
public interface BeanFactory {
  Object createBean(Class<?> beanClass) throws Exception;
}
```

Этот метод служит для создания бинов. Ему передаётся интерфейс или абстрактный класс, помеченный `@Bean`-ом, и то, что
этот метод вернёт становиться бином. Бин-фабрика указывается в аннотации `@BeanConfig`.

Также бин-фабрику можно указать в самом интерфейсе или абстрактном классе в аннотации `@FactoredBy`.

Бин-фабрика, указанная в `@BeanConfig-е` распространяется на все бины, которые относятся к этому бин-конфигу, как по
пути аннотации `@Include` так и по пути аннотации `@BeanScanner`. Следуя по `@Include` могут встретиться внутренние
бин-фабрики,

> внутренние бин-фабрики приоритетнее более общих.

Также, бин-фабрика, определённая аннотацией `@FatoredBy`, более приоритетна чем бин-фабрика,
определённая бин-конфигом.

Если бин-фабрика не определена, но аннотация `@Bean` встретилась у интерфейса или абстрактного класса, то генерируется
ошибка сборки.

Например можно создавать бины интерфейсы следующим образом:

```java
@BeanConfig(defaultFactoryClass = ExampleFactory.class)
public class BeanConfigExample {}

@Bean // фабрика тоже бин
public class ExampleFactory implements BeanFactory {
  @Override
  public Object createBean(Class<?> beanClass) throws Exception {
    if (beanClass == Interface1.class) return () -> "Hello World!";
    if (beanClass == Interface2.class) return () -> 42;
    if (beanClass == InterfaceAsd.class) throw new RuntimeException("Эта ошибка никогда не вылетит");
            
    throw new RuntimeException("I do not known how to create " + beanClass);
  }
}

@Bean
public class ExampleFactoryAsd implements BeanFactory {
  @Override
  public Object createBean(Class<?> beanClass) throws Exception {
    if (beanClass == InterfaceAsd.class) return () -> "asd";
    
    throw new RuntimeException("I do not known how to create " + beanClass);
  }
}

@Bean
public interface Interface1 {
  String message();
}

@Bean
public interface Interface2 {
  int value();
}

@Bean
@FactoredBy(ExampleFactoryAsd.class)
public interface InterfaceAsd {
  String asd();
}

//Теперь сможем подключать интерфейсы:
@Bean
public class UsingInterfaces {
  public BeanGetter<Integerface1> f1;
  public BeanGetter<Integerface2> f2;
  public BeanGetter<IntegerfaceAsd> asd;
  
  public void printMessages() {
    System.out.println("f1 message is " + f1.get().message());
    System.out.println("f2 value   is " + f2.get().value());
    System.out.println("asd        is " + f2.get().asd());
  }
}

```

### Include Beans to Bean Containers

Подключение бина к бин-контэйнеру делается в два этапа:
  1. Подключение бина к бин-конфигу,
  2. Подключение бин-конфига к бин-контэйнеру напрямую или через другой бин-конфиг.

Подключение бина к бин-конфигу делается с помощью [аннотации `@BeanScanner`](#beanscanner-annotation).

Подключение бин-конфига к бин-контэйнеру или другому бин-конфигу делается
с помощью [аннотации `@Include`](#include-annotation).

Так же можно подключать бины к бин-конфигу с помощью [аннотации `@ScanPackage`](#scanpackage-annotation).
Использовать аннотацию `@ScanPackage` не рекомендуется, так как, при этом, усложняется рефакторинг кода.
Аннотация `@ScanPackage` была введена для того, чтобы можно было подключать бины, полученные при кодогенерации.

#### BeanScanner Annotation

Аннотация `@BeanScanner` служит для подключения местных бинов к бин-конфигу

> > Местные бины - это бины, которые находятся в этом пакете и во всех его подпакетах
>
> > По простому: подпакет - это другой пакет, внутри данного пакета.
> 
> Или математически точно:
> 
>  > Подпакетом к данному пакету, в терминах библиотеки depinject, называется другой пакет, имя которого начинается
>  > с имени данного пакета.
>
> Ну и от сюда получается, что
>
> > Подпакет подпакета тоже подпакет
>
> Например у нас имеется два пакета:

    kz.greetgo.main - пакет №1
    kz.greetgo.main.register.impl - пакет №2

> Так вот, пакет №2 является подпакетом к пакету №1

Аннотация `@BeanScanner`, будучи поставленная у бин-конфига (рядом с аннотацией `@BeanConfig`), обязует систему
подключить к этому бин-конфигу все бины, которые находятся в этом пакете и во всех его подпакетах.

#### ScanPackage Annotation

Данная аннотация позволяет подключить не местные бины, т.е. бины, которые находятся в другом пакете, или в других
пакетах. Эта аннотация указывается у бин-конфига (радом с аннотацией `@BeanConfig`). Также в этой аннотации можно
указать пути к одному или нескольким пакетам. Пути могут быть относительные и абсолютные.

Абсолютный путь к пакету - это и есть имя пакета.

Относительный путь начинается с точки или с крышки (^).

Путь начинающийся с точки указывает на пакет имя которого получается из конкатенации текущего пакета и пути.

Например, если в пакете `kz.greetgo.main` расположить бин-конфиг с аннотацией:

    @ScanPackage(".register.impl")

то к этому бин-конфигу будут подключены бины из пакета `kz.greetgo.main.register.impl` и из всех его подпакетов.

Также путь может начинаться с одной или нескольких крышек (^). Одна крышка означать родительский пакет,
две - дедушкин, и так далее. После крышки можно ставить точку, а можно не ставить.

Например, если в пакете `kz.greetgo.main.report.impl` расположить бин-конфиг с аннотацией:

    @ScanPackage("^^.register.impl")

или

    @ScanPackage("^^register.impl")

то к этому бин-конфигу будут подключены бины из пакета `kz.greetgo.main.register.impl` и из всех его подпакетов.

Пользоваться аннотацией `@ScanPackage` крайне не рекомендуется потому что она затрудняет рефакторинг кода. В следующих
версиях библиотеки эта аннотация возможно будет удалена.

#### Include Annotation

Бины подключаются к бин-конфигу, а бин-конфиг подключается к бин-фабрике с помощью аннотации `@Include`, в которой
указывается подключаемый бин-конфиг (один или несколько). Также к бин-конфигу можно подключить другие бин-конфиги.

Можно создать бин-конфиг без бинов вообще. Он будет полезен тем, что к нему подключены другие бин-конфиги. Это
своего рода бин-конфиг агрегатор, какой-то подсистемы внутри общей системы. И, подключая этот бин-конфиг-агрегатор,
подключается вся подсистема.

Например, есть бин-конфиги с бинами:

```java
package kz.greetgo.hello;
@BeanConfig
@BeanScanner
public class BeanConfigHello {}
```
```java
package kz.greetgo.hello;
@Bean
public class Hello {}
```
```java
package kz.greetgo.world;
@BeanConfig
@BeanScanner
public class BeanConfigWorld {}
```
```java
package kz.greetgo.world;
@Bean
public class Hello {}
```
И бин-конфиг-агрегатор:
```java
package kz.greetgo.another;
@BeanConfig
@Include({
  kz.greetgo.hello.BeanConfigHello.class,
  kz.greetgo.world.BeanConfigWorld.class,
})
public class BeanConfigHelloWorld {}
```
`BeanConfigHelloWorld` не содержит своих бинов, но он содержит бины из `BeanConfigHello` и `BeanConfigWorld` за счёт
аннотации `@Include`. Тепер, если мы подключим к бин-контэйнеру `BeanConfigHelloWorld`, то бин-контэйнеру будет
доступны оба бина: `Hello` и `World`. Вот примерно так:
```java
package kz.greetgo.another_another;

@Include(kz.greetgo.another.BeanConfigHelloWorld.class)
public interface HelloWorldContainer implements BeanContainer {
  Hello getHello();
  World getWorld();
}
```

#### Bean Containers

Бин-контэйнер - это интерфейс расширяющий интерфейс `@BeanContainer`. Бин-контэйнер должен содержать один или несколько
методов. Методы в бин-контэйнере не должны содержать аргументов. Иначе будет ошибка сборки.

Тип возвращаемого значения метода бин-контэйнера должен однозначно определять один и только обин бин из того набора
бинов, которые подключены к этому бин-контэйнеру. Иначе будет ошибка сборки.

Инстанция бин-контэйнера создаётся методом `Depinject.newInstance`. На вход этого метода, передаётся класс
бин-контэйнера, а на выходе инстанция этого класса.

Класс, реализующий бин-контэйнер, должен создаваться специальным генератором, компилироваться и включаться в class path.
Для генерации реализации бин-контэйнеров используется библиотека `greetgo.depinject.gen`, и класс `DepinjectUtil`.

#### Replacers

Библиотека предоставляет возможность подмены бинов, с помощью подменьщиков бинов (бин-подменщиков).
Для этого существует специальный интерфейс:

```java
public interface BeanReplacer {
  Object replaceBean(Object originalBean, Class<?> returnClass);
}
```

С одним методом: `replaceBean`. Этот метод вызывается для подмены бинов. Первым аргуметом в этот метод передаётся
оригинальный бин, а вторым аргументов передаётся класс из `BeanGetter`-а. Метод должен вернуть объект, который
`instanceOf returnClass`. Если такой объект создать нельза или не нужно, то можно вернуть `originalBean`.

Для создания бин-подменьщика необходимо чтобы бин реализовывал этот интерфейс. У бин-контэйнера может быть несколько
бин-подменщиков.

Для одного бина тоже может быть несколько бин-подменьщиков. В этом случае он их применяет в порядке величины
приоритетности, указанной аннотацией `@ReplacePriority` в бине-подменьщине. Если этой аннотации нет, то считается,
что эта величина приоритетности такого бина-подменьщика равна нулю. Для одинаковых величин приоритетности
бины-подменьщики выстраиваются в алфавитном порядке по имени класса бина-подменьщика.

Бин-подменьщики применяются не для всех бинов, а только для тех, которые указанны аннотациями:

  - `@ReplaceInstanceOf` - в этой аннотации указывается интерфейс или класс, который должен расширять или реализовывать
                           бин, чтобы к нему применился бин-подменьщик;
  - `@ReplaceWithAnn` - в этой аннотации указывается другая аннотация, которой должен быть помечен бин, чтобы к нему
                        применился данный бин-подменьщик.

Эти аннотации указываются у бина-подменьщика.