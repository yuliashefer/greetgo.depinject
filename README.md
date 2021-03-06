# greetgo.depinject

[English language here](README.eng.md)

Реализация паттерна Dependency Injection на базе кодогенерации от компании greetgo!.

### Ссылки

 - [Лист изменений](doc/change_list.md)

 - [Очень быстрый старт (через TestNG)](doc/fast_start.md)
 - [Быстрый старт (main-функция или war-файл)](doc/quick_start.md)
 - [Концепция](doc/concept.md)
 - [Спецификация](doc/spec.md)
 

# Особенности

 - НЕТ зависимостей;
 - Минималистичный размер;
   > То, что уходит в продакшн, содержит только один класс с одним маленьким статическим методом, а всё
     остальное интерфейсы и аннотации
 - Определение топологии бинов (скорость этой операции = O(n*n), где n - количество бинов), происходит на этапе
   компиляции, а не при запуске;
   > Это позволяет сделать запуск системы всегда быстрым, и не зависимым от количества бинов в системе
 - Инициализация бинов происходит в очерёдности использования, а не в очерёдности зависимостей, поэтому,
   если бины не используются, то они и не создаются
   > Например БИН_1 содержит ссылку на БИН_2. Дак вот, при инициализации БИН_1, БИН_2 инициироваться НЕ будет. БИН_2
     будет инициирован только при непосредственном обращении к нему. Это делает скорость запуска системы
     независимой от количества бинов.
 - Рефлексия в бин-контэйнерах не используется, следовательно всё грамотно оптимизируется!

# Недостатки

  - обращение к бину можно делать только через метод `get()`. К сожалению напрямую (как в Spring-овском @Autowired)
    не получиться, потому что бин надо инициировать при первом обращении к нему, а не при формировании зависимости.
    
    Обращаться можно примерно так:
  
```java
  class SomeBean {
    public BeanGetter<SomeAnotherBean> someAnotherBean;
    public void someMethod() {
        someAnotherBean.get().helloWorld();
    }
  }
```
  - Поля `BeanGetter` должны быть обязательно `public`! (Потому что рефлексия в продакшне не используется);
  - Усложнённая сборка (нужно генерировать код бин-контэйнеров, компилировать его и добавлять в дистрибутив);
  - На данный момент поддерживается только TestNG. (Здорово, если кто-то запилит greetgo.depinject.junit) ;
