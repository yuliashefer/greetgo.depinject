### References

 - [Very quick start (using TestNG)]
 - [Quick start (main function, or war file)](quick_start.md)
 - [Concept](concept.md)
 - [Specification](spec.md)

## Very quick start (using TestNG)

### Prerequisites:

  - java 1.8+
  - gradle 3.5+ (https://gradle.org/)

### Enter

Использовать depinject на TestNG очень просто.
Для этого класс-тест должен наследоваться от специального абстрактного класса и содержать одну аннотацию.

И больше ничего не надо.

Правда, при этом, связывание бинов будет происходить при запуске, но для тестов это не критично.

(Использовать на JUnit, по идее, должно быть тоже очень просто, но это нужно ещё реализовать :) ) 

### Example project preparation

Давайте создадим gradle-проект. Чтобы создать проект, можно:

  - либо одним движением через bash создать проект - [здесь](fast_start.script.sh),
    и далее прыгаем [сюда](#run-tests), чтобы пропустить создание вручную;

  - Либо вручную далее по тексту:

Project file structure:

    depinject.fast_start/
      build.gradle
      src/main/java/depinject/fast_start/
        BeanConfigHelloWorld.java
        Hello.java
        World.java
        HelloWorld.java
      src/test/java/depinject/fast_start/
        HelloWorldTest.java


Content of file `build.gradle`:

```groovy
apply plugin: 'java'
apply plugin: 'maven'
apply plugin: 'idea'

repositories {
  jcenter() //Baruch, hi
}

dependencies {
  ext.depinjectVersion = '2.0.0'
  ext.testNgVersion = '6.5.1'

  compile "kz.greetgo.depinject:greetgo.depinject:$depinjectVersion"

  testCompile "kz.greetgo.depinject:greetgo.depinject.testng:$depinjectVersion"

  testCompile "org.testng:testng:$testNgVersion"
  testCompile 'org.easytesting:fest-assert-core:2.0M10'
}

test { useTestNG() }
```

Content of file `BeanConfigHelloWorld.java`:

```java
package depinject.fast_start;

import kz.greetgo.depinject.core.*;

@BeanConfig
@BeanScanner
public class BeanConfigHelloWorld {}

```

Content of file `Hello.java`:

```java
package depinject.fast_start;

import kz.greetgo.depinject.core.*;

@Bean
public class Hello {

  public String message() {
    return "Hello";
  }

}

```

Content of file `World.java`:

```java
package depinject.fast_start;

import kz.greetgo.depinject.core.*;

@Bean
public class World {

  public String message() {
    return "World";
  }

}
```

Content of file `HelloWorld.java`:

```java
package depinject.fast_start;

import kz.greetgo.depinject.core.*;

@Bean
public class HelloWorld {

  public BeanGetter<Hello> hello;
  public BeanGetter<World> world;

  public String message() {
    return hello.get().message() + " " + world.get().message() + "!!!";
  }

}
```

Content of file `HelloWorldTest.java`:

```java
package depinject.fast_start;

import kz.greetgo.depinject.core.*;
import kz.greetgo.depinject.testng.*;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

@ContainerConfig(BeanConfigHelloWorld.class)
public class HelloWorldTest extends AbstractDepinjectTestNg {

  public BeanGetter<HelloWorld> helloWorld;

  @Test
  public void helloWorldTest() {
    assertThat(helloWorld).isNotNull();
    String message = helloWorld.get().message();
    assertThat(message).isEqualTo("Hello World!!!");
    
    System.err.println();
    System.err.println();
    System.err.println(message);
    System.err.println();
    System.err.println();
  }

}
```

##### Run Tests

Теперь чтобы это всё запустить, заходим в папку `depinject.fast_start/` и запскаем комаду:

    gradle test

В конце должно вылететь:

    BUILD SUCCESSFUL

Это сообщение сигнализирует о том, что все тесты запустились и прошли успешно.
Все связи отлично соединились, и всё отработало как надо.

### Example project description

В файле `HelloWorldTest.java` содержиться тест, который подключает бин `HelloWorld` посредством библиотеки depinject
и использует его. К тому же если мы зайдём в файл `HelloWorld.java`, то увидим, что он использует классы `Hello`
и `World`, и доступы к ним он тоже получает с помощью depinject.

Как видите всё собирается и всё работает.

Так можно всё тестировать. Но если нам понадобиться war-файл или main-функция, то нужно будет создавать BeanContainer
и инициировать его. И потом ещё нужно будет правильно всё это дело откомпилировать и собрать. Как это сделать рассказано
в [быстром старте](quick_start.md).