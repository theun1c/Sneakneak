# UI Architecture Explained

## Общая схема

Текущий UI-слой построен вокруг Compose + screen-level ViewModel + `UiState / UiEvent / UiEffect`.

Используемые уровни сейчас выглядят так:

```text
MainActivity
  -> AppTheme
  -> AppNavHost
      -> auth screens
      -> main shell

presentation/ui
  -> composable screens
  -> reusable components
  -> navigation

domain
  -> auth models / validators / use cases

data
  -> FakeAuthRepository

di
  -> AppContainer
```

Для auth уже введён минимальный `domain/data` каркас. Для main screens UI ещё частично живёт на presentation-level mock state.

## Как устроены screens

Каждый экран строится по одному из двух уровней:

### 1. Route composable
Отвечает за:
- получение ViewModel;
- подписку на `UiEffect`;
- навигационные callback;
- передачу `state` и `onEvent` в screen composable.

### 2. Screen composable
Отвечает за:
- layout;
- вызов reusable composable;
- отображение `UiState`;
- отправку `UiEvent`.

Бизнес-решения по возможности выносятся из composable в ViewModel / domain.

## Как организован state

### Auth
Для auth-экранов state организован полноценно:
- `UiState` хранит поля ввода, loading, dialogs, screen flags;
- `UiEvent` описывает действия пользователя;
- `UiEffect` отвечает за навигацию и одноразовые переходы;
- ViewModel вызывает use cases.

### Main screens
Для `Home`, `Catalog`, `Favorite`, `Profile`, `Edit Profile`, `Loyalty Card` экранные модели уже существуют, но степень зрелости разная:
- в части экранов state пока локально заполняется mock данными;
- в части экранов ViewModel пока является skeleton без repository-backed загрузки;
- shell и навигационная структура уже готовы и не требуют переизобретения.

## Навигация

Навигация разделена на:
- auth flow в `Navigation.kt`;
- main flow в `MainNavGraph.kt`.

Текущее стартовое поведение:
- `Splash` читает mock session;
- если сессии нет, пользователь отправляется в auth;
- если mock session есть, открывается main graph.

Для UI-этапа используются helper-функции:
- `mockNavigate`
- `mockReplace`

Это временный слой удобства, который будет заменён на навигацию от реальных данных/сессии.

## Reusable composables

Основные правила текущего UI-слоя:
- визуальные токены вынесены в `ui/theme`;
- form controls переиспользуются между auth и profile;
- product presentation переиспользуется между `Home`, `Catalog`, `Favorite`;
- shell-компоненты централизуют top bar, drawer и bottom nav.

Это снижает риск расхождения экранов при следующем этапе интеграции логики.

## Как устроен mock flow

### Auth mock flow

Источник данных:
- `FakeAuthRepository`

Через него уже моделируются:
- регистрация;
- вход;
- logout;
- recovery request;
- OTP verification;
- password reset;
- наблюдение mock session.

Это позволяет гонять экранный поток без сети.

### Main mock flow

Источник данных:
- `MockCatalogState`

Через него пока моделируются:
- категории;
- товары;
- избранное.

Это временное presentation-level решение. На следующем этапе оно должно быть заменено на use case + repository contracts.

## Где позже подключать логику

### Auth
Подмена уже подготовлена:
- заменить `FakeAuthRepository` на реальную data implementation;
- сохранить текущие use case и screen contracts.

### Profile
Нужно ввести:
- `ProfileRepository`;
- profile use cases;
- backend-bound ViewModel loading/save state.

### Catalog / Home / Favorite
Нужно ввести:
- `ProductsRepository`;
- `FavoritesRepository`;
- screen state из domain/data вместо `MockCatalogState`.

### Loyalty
Нужно ввести:
- user id source;
- barcode payload/use case;
- позже генератор Code128.

## Почему текущий UI подготовлен к следующему этапу

- навигационные маршруты уже разведены;
- базовые экраны существуют и визуально согласованы;
- reusable components собраны;
- auth уже показывает, как можно подключать domain/data без переписывания composable;
- TODO-маркеры в коде отмечают точки будущей интеграции;
- документация по статусу этапа теперь хранится рядом с проектом.
