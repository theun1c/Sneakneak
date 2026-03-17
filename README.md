# Sneakneak

Android-приложение магазина обуви/спортивных товаров на Kotlin + Jetpack Compose.

## Текущее состояние

UI-этап проекта зафиксирован:
- собран базовый Compose foundation;
- реализованы auth screens;
- реализован main shell;
- собраны основные main screens;
- часть экранов уже работает на тестовых данных и mock state;
- реальный Supabase/backend в рамках текущего состояния ещё не подключён.

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
- только после этого реальная интеграция с Supabase.

## Где смотреть документацию

- общий статус UI-этапа: [docs/project_status/UI_IMPLEMENTED.md](docs/project_status/UI_IMPLEMENTED.md)
- объяснение текущей UI-архитектуры: [docs/project_status/UI_ARCHITECTURE_EXPLAINED.md](docs/project_status/UI_ARCHITECTURE_EXPLAINED.md)
- changelog UI-этапа: [docs/project_status/CHANGELOG_UI_STAGE.md](docs/project_status/CHANGELOG_UI_STAGE.md)
- следующий этап: [docs/project_status/NEXT_STEPS.md](docs/project_status/NEXT_STEPS.md)
- базовые проектные документы: `docs/01_*` ... `docs/06_*`
