
### Лист изменений

#### Изменения 2.0.0 -> 2.0.1

 - Сделана проверка, чтобы BeanGetter был обязательно public;
   > Если BeanGetter не public, то будет генерироваться ошибка BeanGetterIsNotPublic.
   > Это сделано, потому что программисты постоянно забывают, что BeanGetter должен быть public,
   > и потом хватают ошибку NullPointerException и долго не понимают, в чём дело. Теперь сразу же генерируется ошибка,
   > из-за которой сразу становиться понятно, что не так.
 - Добавлена аннотация @LetBeNonePublic на случай, чтобы всё-таки можно было добавить BeanGetter с непубличным доступом;
