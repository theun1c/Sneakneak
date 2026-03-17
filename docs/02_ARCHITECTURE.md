# 02. Техническая архитектура

## 1. Архитектурный стиль

Рекомендуемый стиль: **Clean Architecture + feature-first packages + MVVM/MVI-подобная подача UI-состояния**.

Это означает:

- `domain` хранит бизнес-правила, сущности, интерфейсы репозиториев и use case;
- `data` реализует доступ к Supabase Auth, PostgREST и Storage;
- `presentation` содержит Compose UI, ViewModel, экранные состояния и навигацию.

## 2. Правила зависимости

```text
presentation -> domain
data -> domain
domain -> nothing
```

Допускается корневая папка `di/` для связывания зависимостей. Она не должна превращаться в место бизнес-логики.

## 3. Структура пакетов

Рекомендуемая структура:

```text
app/src/main/java/com/example/up/
  data/
    auth/
      datasource/
      dto/
      mapper/
      repository/
    profile/
    promotions/
    products/
    favorites/
    media/
  domain/
    auth/
      model/
      repository/
      usecase/
      validator/
    profile/
    products/
    favorites/
    common/
  presentation/
    navigation/
    theme/
    components/
    auth/
      signin/
      signup/
      forgot/
      otp/
      newpassword/
    home/
    catalog/
    favorite/
    profile/
    loyalty/
  di/
  core/
    result/
    dispatchers/
    error/
    ui/
```

## 4. Domain layer

## 4.1 Ответственность

Слой `domain` должен содержать:

- чистые модели предметной области;
- интерфейсы репозиториев;
- use case;
- валидаторы;
- при необходимости value objects.

## 4.2 Пример набора use case

### Auth
- `SignUpWithEmailUseCase`
- `SignInWithEmailUseCase`
- `SignOutUseCase`
- `SendRecoveryCodeUseCase`
- `VerifyRecoveryCodeUseCase`
- `UpdatePasswordUseCase`
- `ObserveSessionUseCase`
- `GetCurrentUserIdUseCase`

### Profile
- `GetProfileUseCase`
- `CreateOrGetProfileUseCase`
- `UpdateProfileUseCase`
- `UploadProfilePhotoUseCase`

### Products / Home
- `GetPromotionsUseCase`
- `GetCatalogProductsUseCase`
- `GetBestSellerProductsUseCase`
- `GetCategoriesUseCase`

### Favorite
- `GetFavoritesUseCase`
- `ToggleFavoriteUseCase`
- `IsFavoriteUseCase`

## 5. Data layer

## 5.1 Ответственность

Слой `data` отвечает за:

- вызовы Supabase SDK;
- DTO и их десериализацию;
- маппинг DTO -> domain model;
- обработку ошибок transport/backend уровня;
- реализацию интерфейсов репозиториев.

## 5.2 Remote data sources

Рекомендуется выделить отдельные remote data source:

- `AuthRemoteDataSource`
- `ProfileRemoteDataSource`
- `ProductsRemoteDataSource`
- `FavoritesRemoteDataSource`
- `StorageRemoteDataSource`
- `PromotionsRemoteDataSource`

## 5.3 Репозитории

Репозитории должны скрывать детали Supabase SDK от `domain`.

Примеры:

- `AuthRepositoryImpl`
- `ProfileRepositoryImpl`
- `ProductsRepositoryImpl`
- `FavoritesRepositoryImpl`
- `MediaRepositoryImpl`

## 6. Presentation layer

## 6.1 Состояние экрана

Для каждого экрана использовать три основных сущности:

- `UiState` — полное состояние экрана;
- `UiAction`/`UiEvent` — входящие действия пользователя;
- `UiEffect` — одноразовые побочные эффекты.

### Пример

```kotlin
data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dialogMessage: String? = null,
)
```

## 6.2 Навигация

Навигацию строить через два графа:

- `AuthNavGraph`
- `MainNavGraph`

Контейнерный `AppNavHost` переключает граф в зависимости от статуса сессии.

## 6.3 Повторно используемые UI-компоненты

Обязательно вынести в `presentation/components`:

- `AppTextField`
- `PasswordTextField`
- `PrimaryButton`
- `ErrorDialog`
- `LoadingOverlay`
- `OtpInput`
- `ProductCard`
- `BottomBar`
- `SideMenuDrawer`

## 7. Обработка ошибок

Использовать единый слой отображаемых ошибок.

### Рекомендуемая схема

- низкоуровневая ошибка Supabase/IO -> `DataError`
- маппинг в `DomainError`
- UI получает готовый пользовательский текст или тип ошибки

Не показывать пользователю сырые внутренние тексты исключений, если это ухудшает UX.

## 8. Работа с корутинами

- Все сетевые запросы выполнять в `viewModelScope`.
- Репозитории и data source должны быть `suspend`/`Flow`.
- Для одноразовых операций использовать `suspend`.
- Для наблюдаемой сессии и реактивных потоков — `Flow`.

## 9. Инъекция зависимостей

DI-контейнер должен предоставлять:

- `SupabaseClient`
- data source
- repositories
- use case
- view model factories / Hilt bindings

Предпочтительно:

- `Singleton` для `SupabaseClient`
- отдельные модули: `NetworkModule`, `RepositoryModule`, `UseCaseModule`

## 10. Тема и дизайн-система

Compose theme должна быть централизована.

Нужно завести:

- `Color.kt`
- `Type.kt`
- `Theme.kt`
- при необходимости `Dimens.kt`/`Spacing.kt`

Визуальные константы не размазывать по экранам.

## 11. Работа с изображениями

### Загрузка из сети
- `Coil AsyncImage`
- placeholder/error image
- разумный content scale по макету

### Фото профиля
- выбор из галереи через системный picker;
- съемка камерой через контракт/CameraX;
- перед upload — получить URI/временный файл;
- upload выполнять в Storage;
- затем сохранить путь/URL в профиле.

## 12. Штрихкод

Для user id генерировать линейный штрихкод формата `Code128`.

Причины:

- поддерживает UUID/строковый id;
- визуально соответствует понятию «бар-код» лучше, чем QR;
- удобен для экранного отображения.

Генерация должна быть инкапсулирована в отдельном utility/use case, а не жить в composable.

## 13. Архитектурные тесты

Минимальный рекомендуемый набор:

- unit-тесты валидаторов email/password;
- unit-тесты use case toggle favorite;
- unit-тесты profile update mapper/validator;
- при возможности UI-тесты auth flow и catalog list rendering.

## 14. Правила расширения

Если позже добавятся `cart/orders`, они должны внедряться как новые feature-пакеты, не ломая текущие `auth/profile/products/favorites`.

Запрещено «встраивать» новую логику в существующие экраны без feature boundary.
