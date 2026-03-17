# 04. План реализации

## Этап 0. Bootstrap

Цель: подготовить базовый каркас приложения.

### Задачи
- создать проект Android с minSdk 30;
- подключить Compose и Material 3;
- подключить Navigation Compose;
- подключить DI;
- подключить Supabase Kotlin client;
- настроить `SUPABASE_URL` и `SUPABASE_ANON_KEY` через локальные свойства;
- подготовить базовую theme-систему.

### Результат этапа
- приложение запускается;
- есть пустой `AppNavHost`;
- SupabaseClient создается из конфигурации;
- build проходит без заглушек инфраструктуры.

## Этап 1. Базовая архитектура

### Задачи
- завести слои `domain/data/presentation`;
- создать base result/error model;
- создать интерфейсы репозиториев;
- реализовать DI-модули;
- создать base UI-компоненты.

### Результат этапа
- проект имеет согласованную структуру;
- можно реализовывать фичи по вертикали без дальнейшего архитектурного рефакторинга.

## Этап 2. Auth flow

### Задачи
- `Register Account`
- `Sign In`
- `Forgot Password`
- `Verification`
- `Create New Password`
- общая email/password валидация в `domain`
- обработка загрузки, диалогов и навигации

### Результат этапа
- полный auth flow работает на Supabase end-to-end.

## Этап 3. Main shell

### Задачи
- main navigation;
- bottom bar;
- side menu;
- logout flow;
- session gate между auth и main graph.

### Результат этапа
- после логина пользователь попадает в main-часть приложения;
- logout возвращает на `Sign In`.

## Этап 4. Home и Catalog

### Задачи
- загрузка `actions`;
- загрузка `products`;
- загрузка `categories`;
- reusable `ProductCard`;
- экран `Home`;
- экран `Catalog`.

### Результат этапа
- каталог и акции читаются с сервера;
- карточки товаров переиспользуются.

## Этап 5. Favorite

### Задачи
- таблица `favourite`;
- toggle favorite из `ProductCard` и catalog/home;
- экран `Favorite`;
- empty-state.

### Результат этапа
- избранное персонифицировано и синхронизируется с сервером.

## Этап 6. Profile и Loyalty Card

### Задачи
- чтение профиля;
- создание профиля при первом входе, если записи нет;
- `Edit Profile`;
- загрузка фото профиля;
- камера/галерея;
- генерация штрихкода;
- экран `Loyalty Card`.

### Результат этапа
- профиль редактируется;
- фото сохраняется в Storage;
- штрихкод строится из user id.

## Этап 7. Hardening и QA

### Задачи
- unit-тесты валидаторов и ключевых use case;
- проверка пустых/ошибочных состояний;
- проверка поворот/восстановление состояния критичных экранов;
- ручная приемка по `05_QA_CHECKLIST.md`.

### Результат этапа
- приложение стабильно проходит сценарии задания.

## Порядок коммитов

Рекомендуемый ритм:

1. `bootstrap`
2. `architecture-core`
3. `auth-flow`
4. `main-shell`
5. `home-catalog`
6. `favorites`
7. `profile-loyalty`
8. `qa-fixes`

## Чего не делать в плане

- не начинать с profile/upload, пока нет auth/session;
- не делать catalog до базового repository/mapper pattern;
- не смешивать одновременно полный UI polish и backend integration на самых ранних шагах.
