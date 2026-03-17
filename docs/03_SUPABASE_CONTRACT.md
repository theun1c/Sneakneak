# 03. Контракт с Supabase, схема данных и интеграция

## 1. Канонический backend

Целевой backend проекта — **Supabase**.

Экспорт PocketBase используется только как историческая подсказка по полям и сценариям. Реализация клиента должна опираться на Supabase Auth, PostgreSQL и Supabase Storage.

## 2. Используемые подсистемы Supabase

- **Auth** — регистрация, логин, logout, recovery flow
- **PostgREST / Data API** — чтение и обновление таблиц
- **Storage** — фото профиля, фото товаров, фото акций
- **Row Level Security** — изоляция пользовательских данных

## 3. Целевые таблицы MVP

### 3.1 `public.categories`

Использование:
- справочник категорий каталога

Поля:
- `id uuid primary key`
- `title text not null`

### 3.2 `public.products`

Использование:
- каталог товаров
- блоки товаров на `Home`
- карточки избранного

Поля из SQL:
- `id uuid primary key`
- `title text not null`
- `category_id uuid`
- `cost float8 not null`
- `description text not null`
- `is_best_seller bool default false`

**Обязательная доработка схемы:**

```sql
alter table public.products
add column if not exists photo text;
```

`photo` хранит путь в Supabase Storage или готовый URL.

### 3.3 `public.actions`

Использование:
- акции/баннеры на `Home`

Поля:
- `id uuid primary key`
- `created_at timestamptz not null default now()`
- `photo text null`

### 3.4 `public.profiles`

Использование:
- профиль текущего пользователя
- edit profile
- фото профиля

Поля:
- `id uuid primary key`
- `created_at timestamptz not null default now()`
- `user_id uuid not null references auth.users(id)`
- `photo text null`
- `firstname text null`
- `lastname text null`
- `address text null`
- `phone text null`

**Обязательная доработка схемы:**

```sql
create unique index if not exists profiles_user_id_uidx
on public.profiles(user_id);
```

У одного пользователя должна быть не более одной записи профиля.

### 3.5 `public.favourite`

Использование:
- избранное пользователя

Поля:
- `id uuid primary key`
- `product_id uuid references public.products(id)`
- `user_id uuid references auth.users(id)`

**Обязательная доработка схемы:**

```sql
create unique index if not exists favourite_user_product_uidx
on public.favourite(user_id, product_id);
```

Это позволит безопасно реализовать toggle без дублей.

## 4. Таблицы вне текущего MVP

Следующие таблицы оставляем в базе, но не используем как обязательные фичи текущего релиза:

- `cart`
- `orders`
- `orders_items`
- `payments`
- `deliveries`
- `order_status`
- `notifications`

## 5. Storage buckets

Рекомендуемые bucket'ы:

### 5.1 `avatars`
- хранение фото профиля
- путь: `users/{userId}/avatar.jpg`

### 5.2 `products`
- хранение изображений товаров
- путь: `products/{productId}/main.jpg`

### 5.3 `promotions`
- хранение промо-баннеров
- путь: `actions/{actionId}/banner.jpg`

## 6. RLS-политики

### 6.1 Profiles

```sql
alter table public.profiles enable row level security;

create policy if not exists profiles_select_own
on public.profiles
for select
to authenticated
using (user_id = auth.uid());

create policy if not exists profiles_insert_own
on public.profiles
for insert
to authenticated
with check (user_id = auth.uid());

create policy if not exists profiles_update_own
on public.profiles
for update
to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());
```

### 6.2 Favourite

```sql
alter table public.favourite enable row level security;

create policy if not exists favourite_select_own
on public.favourite
for select
to authenticated
using (user_id = auth.uid());

create policy if not exists favourite_insert_own
on public.favourite
for insert
to authenticated
with check (user_id = auth.uid());

create policy if not exists favourite_delete_own
on public.favourite
for delete
to authenticated
using (user_id = auth.uid());
```

### 6.3 Public catalog data

```sql
alter table public.products enable row level security;
alter table public.categories enable row level security;
alter table public.actions enable row level security;

create policy if not exists products_read_all
on public.products
for select
to authenticated, anon
using (true);

create policy if not exists categories_read_all
on public.categories
for select
to authenticated, anon
using (true);

create policy if not exists actions_read_all
on public.actions
for select
to authenticated, anon
using (true);
```

## 7. Auth flow

## 7.1 Регистрация

Клиент вызывает регистрацию по email и паролю.

Требование задания: после успешной регистрации перейти на экран `Sign In`.

Поэтому проектное решение такое:

1. использовать Supabase email/password sign-up;
2. для учебного проекта рекомендуется **отключить обязательное Confirm email** в настройках проекта Supabase;
3. если Supabase автоматически создает сессию после sign-up, выполнить явный `signOut()` и вернуть пользователя на `Sign In`, чтобы UX совпал с заданием.

## 7.2 Логин

Использовать email/password auth.

После успеха переходить в main-graph.

## 7.3 Logout

Logout должен:

1. вызвать серверную деавторизацию через Supabase Auth;
2. очистить локальную сессию;
3. очистить связанные UI-state при необходимости;
4. перевести пользователя на `Sign In`.

## 7.4 Recovery / OTP flow

Целевой пользовательский поток по заданию:

```text
Forgot Password -> OTP Verification -> Create New Password -> Sign In
```

Чтобы он стыковался с Supabase, использовать следующую модель:

1. `Forgot Password` отправляет recovery email через Supabase.
2. Email template для reset/recovery должен быть настроен на отправку **OTP**, а не только ссылки.
3. `Verification` отправляет email + token (OTP) на верификацию recovery-потока.
4. После успешной верификации пользователь получает временно валидную recovery/session context.
5. `Create New Password` вызывает изменение пароля.
6. После успеха — logout/clean state и переход на `Sign In`.

## 8. Рекомендуемые интерфейсы репозиториев

### AuthRepository

```kotlin
interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun sendRecoveryCode(email: String): Result<Unit>
    suspend fun verifyRecoveryCode(email: String, code: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    fun observeSession(): Flow<Boolean>
    suspend fun getCurrentUserId(): String?
    suspend fun getCurrentUserEmail(): String?
}
```

### ProfileRepository

```kotlin
interface ProfileRepository {
    suspend fun getMyProfile(): Result<UserProfile>
    suspend fun upsertMyProfile(profile: UserProfileDraft): Result<UserProfile>
    suspend fun uploadAvatar(localUri: String): Result<String>
}
```

### ProductsRepository

```kotlin
interface ProductsRepository {
    suspend fun getCatalog(): Result<List<Product>>
    suspend fun getBestSellers(): Result<List<Product>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getPromotions(): Result<List<Promotion>>
}
```

### FavoritesRepository

```kotlin
interface FavoritesRepository {
    suspend fun getMyFavorites(): Result<List<Product>>
    suspend fun addToFavorites(productId: String): Result<Unit>
    suspend fun removeFromFavorites(productId: String): Result<Unit>
    suspend fun isFavorite(productId: String): Result<Boolean>
    suspend fun toggleFavorite(productId: String): Result<Boolean>
}
```

## 9. DTO / domain mapping

Канонические domain-модели:

```kotlin
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val cost: Double,
    val categoryId: String?,
    val categoryTitle: String?,
    val isBestSeller: Boolean,
    val photo: String?
)

data class Promotion(
    val id: String,
    val photo: String?
)

data class UserProfile(
    val userId: String,
    val email: String?,
    val firstname: String,
    val lastname: String,
    val address: String,
    val phone: String,
    val photo: String?
)
```

## 10. Query-стратегии

### 10.1 Catalog

- читать `products`
- по возможности тянуть вложенную `category` через relationship/join
- если nested query неудобна в выбранной реализации, делать два запроса: `categories` + `products`, затем собирать mapping на клиенте

### 10.2 Favorite

Базовая стратегия:

1. получить записи `favourite where user_id = currentUserId`
2. извлечь `product_id`
3. запросить `products in (...)`
4. соединить на клиенте

Если SDK-реализация nested relation удобна — допускается join-подход.

### 10.3 Profile

- читать `profiles where user_id = currentUserId single`
- если записи нет — создать пустой профиль и затем читать его как канонический объект

## 11. Storage flow для аватара

1. пользователь выбирает изображение из галереи или камеры;
2. клиент получает URI/файл;
3. upload в bucket `avatars`;
4. получает путь или публичный URL;
5. сохраняет это значение в `profiles.photo`;
6. обновляет profile state.

## 12. Названия и расхождения

### `favourite` vs `favorite`

- в базе использовать `favourite`, если схема уже развернута по SQL;
- в Kotlin-моделях и пакетах использовать слово `Favorite` для единообразия UI/домена;
- различие скрыть в data layer.

### `photo`

Использовать единое имя поля `photo` для:
- `profiles`
- `products`
- `actions`

Это уменьшает количество mapper-исключений.


## 13. Настройки Supabase Auth проекта

Перед интеграцией клиента проект Supabase должен быть приведен к следующему состоянию:

1. **Email/password auth включен**.
2. **Confirm email** для учебного проекта рекомендуется выключить, чтобы не вводить дополнительный экран подтверждения регистрации.
3. Для recovery email настроить шаблон письма так, чтобы пользователь получал **код/OTP**, а не только ссылку.
4. Если команда решит сохранить резервный deeplink-сценарий, дополнительно настроить Android redirect scheme и добавить его в redirect URLs проекта.
5. Для demo/учебного проекта можно использовать встроенную почтовую доставку Supabase, но для более стабильной эксплуатации предпочтительно подключить собственный SMTP.

## 14. Mapping на методы клиента Supabase Kotlin

Названия конкретных методов могут слегка отличаться в зависимости от версии `supabase-kt`, но ожидаемая семантика должна быть такой:

### Auth
- регистрация: `signUpWith(Email)`
- вход: `signInWith(Email)`
- выход: `signOut()`
- отправка recovery: `resetPasswordForEmail(email)`
- проверка recovery OTP: `verifyEmailOtp(...)` / эквивалентный verify method с типом `recovery`
- смена пароля после recovery: `updateUser { password = ... }`

### Data API
- чтение списка: `from("products").select(...)`
- чтение одной записи: `single()` / `decodeSingle()`
- вставка/создание профиля: `insert(...)`
- обновление профиля: `update(...){ filter ... }`
- upsert при необходимости: `upsert(...)`

### Storage
- bucket upload: `storage.from("avatars").upload(...)`
- аналогично для `products` и `promotions`

Если в выбранной версии SDK recovery-OTP API называется иначе, data layer обязан скрыть это за интерфейсом `AuthRepository`, не меняя contract domain слоя.

## 15. Обязательный SQL patch pack

```sql
alter table public.products
add column if not exists photo text;

create unique index if not exists profiles_user_id_uidx
on public.profiles(user_id);

create unique index if not exists favourite_user_product_uidx
on public.favourite(user_id, product_id);

alter table public.profiles enable row level security;
alter table public.favourite enable row level security;
alter table public.products enable row level security;
alter table public.categories enable row level security;
alter table public.actions enable row level security;
```

После этого применяются policy-блоки из раздела 6.
