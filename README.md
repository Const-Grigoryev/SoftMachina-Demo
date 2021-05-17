SoftMachina Demo
================

> Тестовое задание для [СофтМашины](https://soft-machine.ru/).

Тестовое задание
----------------

  1. Разработать "заглушку" на *springboot* с использованием *WebFlux* (project reactor) - *java/kotlin*.

  2. *gradle/maven* - на выбор.

  3. (de-)serialization библиотека - на выбор.

  4. Данные хранить просто в памяти.

  5. Dockerfile.  

### Rest API:

  * **GET** `/users` возвращает список всех пользователей в формате

```json
    [
        {"username": "значение", "password": "значение"},
        {"username": "значение2", "password": "значение2"}
    ]
```
    
Опционально может быть задан query параметр `userNameMask` - в виде регулярного выражения, и тогда нужно вернуть 
только пользователей с username соответствующим regex.
Eсли пользователи не найдены - отдать пустой список `[]`.

  * **POST** `/user`  принимает body в формате

```json
    {"username": "значение", "password": "значение"}
```

и сохраняет пользователя в список пользователей (если значения `username` и `password` соответствуют регулярному выражению - см. Конфигурационные параметры).
Если пароль пустой или не указан - `500`. Если пользователь с таким username уже существует - отдает ошибку с `500` кодом.

  * **POST** /updatePassword - принимает body в формате

```json
    {"username": "значение", "oldpassword": "старый пароль", "password": "значение"}
```

Если пользователь существует и его текущий пароль равен значению поля `oldpassword` и если новый пароль соотвестует регулярному выражению, то обновляет его пароль
и отдает `200 ОК`. Если пользователь с таким `userName` не существует - ошибка `500`. Tсли значение поля `oldpassword` не совпадает с текущим паролем
пользователя - `500`.

### Примечания к методам:

Все ошибки должны быть залогированы c описанием. Опционально в случае ошибки вернуть ответ вида

```json
    {"message": "детальное описание ошибки"}
```                            

### Конфигурационные параметры:

  1. задержка (в милисекундах) на каждый из трех методов - по умолчанию 0. (Использовать "реактивную" задержку, без блокировки потока - НЕ `Thread.sleep`)
 
  2. Regex для `userName` - по умолчанию `.*`
 
  3. Regex для `password` - по умлочанию `.*`

### Примечения к конфигураци:

  1. При старте приложения - конфигурация задается файлом

  2. Поддержка изменения конфигурационных параметров в рантайме через внутренний API `/api/config/{path}` 

Покрыть все методы (в том числе конфигурационные, с проверкой что изменения действительно влияют на поведение) интеграционными автотестами (на уровне реальных
http запросов) с использованием *Rest-assured*. Опционально *allure* отчеты. Опционально простенький frontend для изменения конфигурации.