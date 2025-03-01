# Магазинчик

Некоммерческий мини-проект для понимания того что я умею, что могу изменить и что могу начать развивать.
Если увидите это и решите посмотреть мой код прошу оставить обратную связь с замечаниями и предложениями по улучшению кода.

## Замечания которые могу сейчас выделить сам

- Приходится вводить id вместо выбора продукта или магазина из списка
- Регистрация и авторизация максимально простая, нет никаких проверок и информирования

## Ссылки

- GitHub-проект с UI-тестами на Selenium: https://github.com/AAndreyAndreevich/store-tests.git

## Контакты

- Telegram: https://t.me/andrej_andreevich
- GitHub: https://github.com/AAndreyAndreevich
- Gmail: andrey.zolotarev7610@gmail.com

## Доп настройка

Для работы так же необходим application.properties, для безопасности данных я убрал его из общего пула файлов.
В нем присутствуют следующие переменные:
- spring.application.name=store
- server.port=8080
- spring.datasource.driver-class-name=org.postgresql.Driver
- spring.jpa.show-sql=true
- spring.jpa.hibernate.ddl-auto=update
- spring.datasource.url=ссылка на базу данных
- spring.datasource.username=логин
- spring.datasource.password=пароль