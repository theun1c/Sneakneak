# Changelog UI Stage

## Что было сделано на UI-этапе

### Базовый каркас
- создан Compose-based Android UI host;
- собрана единая theme-система;
- подключена навигация на базе `Navigation Compose`;
- подготовлены reusable UI-компоненты для auth и main shell.

### Auth UI
- собраны экраны `Sign In`, `Register Account`, `Forgot Password`, `OTP`, `Create New Password`;
- добавлены диалоги ошибок и успеха;
- добавлено переключение видимости пароля;
- добавлен OTP input row и countdown state;
- реализовано состояние disabled/enabled для регистрации по макету.

### Main shell
- собраны bottom navigation и side menu;
- реализованы top bar variants;
- подключён logout переход на уровне mock session;
- настроены auth/main маршруты.

### Main screens UI
- собраны `Home`, `Catalog`, `Favorite`, `Profile`, `Edit Profile`, `Loyalty Card`;
- выделен reusable `ProductCard`;
- выделены profile-specific reusable blocks;
- выстроена общая visual shell для main flow.

### Mock logic, уже появившаяся до фиксации этапа
- введён `FakeAuthRepository`;
- введён минимальный `AppContainer`;
- auth flow переведён на тестовые данные;
- `Splash` начал работать как mock session gate;
- main screens частично остаются на presentation-level mock state.

## Какие пакеты/файлы были созданы или оформлены на этапе

### Основные UI пакеты
- `ui/theme`
- `ui/components`
- `ui/auth/*`
- `ui/main/*`
- `ui/navigation`

### Минимальный foundation для следующего этапа
- `domain/auth/*`
- `data/auth/*`
- `di/AppContainer.kt`

### Документация статуса
- `docs/project_status/UI_IMPLEMENTED.md`
- `docs/project_status/UI_ARCHITECTURE_EXPLAINED.md`
- `docs/project_status/CHANGELOG_UI_STAGE.md`
- `docs/project_status/NEXT_STEPS.md`

## Какие решения были приняты

- UI строится только на Jetpack Compose;
- навигация разделена на auth и main flows;
- screen state оформляется через `UiState / UiEvent / UiEffect`;
- пока backend не подключён, допускается mock/session/presentation state;
- reusable components выносятся до подключения реальных данных;
- logout и auth flow уже моделируются на тестовых данных, чтобы UI был не статичной картинкой.

## Временные решения и компромиссы

- вместо реального backend используется `FakeAuthRepository`;
- каталог и избранное пока завязаны на `MockCatalogState`;
- profile и loyalty пока не имеют полноценного repository-backed data source;
- placeholder routes присутствуют в shell, но не реализованы как MVP features;
- часть ViewModel main screens пока skeleton-only.

## Что изменено для ясности и передачи

- в ключевые файлы добавлены краткие поясняющие комментарии;
- в mock и integration points добавлены TODO/NOTE маркеры;
- README обновлён под текущее состояние этапа;
- статус UI-этапа вынесен в отдельный набор документов.
