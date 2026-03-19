# Sneakneak

Android-приложение магазина обуви/спортивных товаров на Kotlin + Jetpack Compose.

## Текущее состояние

UI-этап проекта зафиксирован:
- собран базовый Compose foundation;
- реализованы auth screens;
- реализован main shell;
- собраны основные main screens;
- auth foundation подключён к Supabase (с fallback на fake implementation);
- часть main screen-данных всё ещё работает на тестовых данных и mock state.

Это означает, что проект сейчас находится между этапами `UI complete` и `logic/data integration`.

## Что уже есть

- `Splash`, `Sign In`, `Register Account`, `Forgot Password`, `OTP`, `Create New Password`
- `Home`, `Catalog`, `Favorite`, `Profile`, `Edit Profile`, `Loyalty Card`
- bottom navigation и side menu
- reusable Compose components
- centralized theme/design system
- mock auth flow через `FakeAuthRepository`
- mock catalog/favorite state для UI review

## Что дальше

Следующий этап:
- стабилизация логики экранов;
- расширение `domain/data` beyond auth;
- замена mock state на repository/use case contracts;
- расширение Supabase-интеграции с auth на `profile`, `catalog`, `favorite`.

## Supabase setup (Auth foundation)

Для включения real Supabase auth:

1. Откройте существующий `local.properties` (не удаляя `sdk.dir`).
2. Добавьте строки:
   - `SUPABASE_URL`
   - `SUPABASE_ANON_KEY`

`local.properties` уже исключён из Git, поэтому ключи не попадут в репозиторий.
Альтернатива: `~/.gradle/gradle.properties` или переменные окружения с теми же именами.

Если значения не заданы, приложение автоматически использует `FakeAuthRepository` как безопасный fallback для UI/dev.

## Build prerequisites

Проект использует `AGP 8.13.2`, для сборки требуется JDK 17.

Если у вас выставлен JDK 11 или JDK 25, Gradle может падать на этапе инициализации. Убедитесь, что в Android Studio/`JAVA_HOME` выбран JDK 17.

## Где смотреть документацию

- общий статус UI-этапа: [docs/project_status/UI_IMPLEMENTED.md](docs/project_status/UI_IMPLEMENTED.md)
- объяснение текущей UI-архитектуры: [docs/project_status/UI_ARCHITECTURE_EXPLAINED.md](docs/project_status/UI_ARCHITECTURE_EXPLAINED.md)
- changelog UI-этапа: [docs/project_status/CHANGELOG_UI_STAGE.md](docs/project_status/CHANGELOG_UI_STAGE.md)
- следующий этап: [docs/project_status/NEXT_STEPS.md](docs/project_status/NEXT_STEPS.md)
- базовые проектные документы: `docs/01_*` ... `docs/06_*`
