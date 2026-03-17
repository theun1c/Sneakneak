# UI Implemented

## Статус этапа

Текущий проект зафиксирован после UI-этапа:
- завершён базовый Compose foundation;
- собран auth UI flow;
- собран main shell;
- собраны основные main screens;
- часть экранов уже имеет `UiState / UiEvent / UiEffect` и ViewModel;
- backend/Supabase в рамках этого этапа не подключён.

## Готовые экраны

### Auth
- `Splash`
- `Sign In`
- `Register Account`
- `Forgot Password`
- `Verification / OTP`
- `Create New Password`

### Main
- `Home`
- `Catalog`
- `Favorite`
- `Profile`
- `Edit Profile`
- `Loyalty Card`
- drawer / side menu
- bottom navigation

### Placeholder routes
- `Notifications`
- `Cart`
- `Orders`
- `Settings`

Эти маршруты заведены в shell, но пока остаются экранными заглушками.

## Что покрыто по UI states

### Реализованные состояния
- auth loading state на submit-сценариях;
- auth dialog error state;
- `Forgot Password` success dialog;
- `OTP` timer / resend / error state;
- registration button enabled/disabled state по макету;
- `Favorite` empty/content state;
- mock session gate между `Splash`, auth и main graph;
- logout возврат на `Sign In` через mock session.

### Частично покрытые состояния
- `Home`, `Catalog`, `Profile`, `Edit Profile`, `Loyalty Card` уже имеют screen-level state и ViewModel skeleton;
- main data states пока в основном работают на локальном mock state;
- loading/error/empty для server-driven main screens пока не доведены до полной domain/data интеграции.

## Reusable components

### Form / auth
- `AppTextField`
- `PasswordTextField`
- `PrimaryButton`
- `InfoDialog`
- `SuccessDialog`
- `BackButton`
- `AuthScreenScaffold`
- `ConsentRow`
- `OtpInputRow`

### Main UI
- `ProductCard`
- `SearchBar`
- `CategoryChip`
- `BottomNavBar`
- `MainShellScaffold`
- `MainTopBar`
- `MainDrawer`
- `ProfileHeaderBlock`
- `ProfileBarcodeCard`
- `ProfileFieldList`
- `LoyaltyBarcodeBlock`

## Используемые assets

### Design overlays / local assets
- `design/Splash.png`
- `design/Sign in.png`
- `design/Register Account.png`
- `design/Forgot Password.png`
- `design/Verification.png`
- `design/Create New Password.png`
- `design/Home.png`
- `design/Catalog.png`
- `design/Favorite.png`
- `design/Profile.png`
- `design/Edit Profile.png`
- `design/Loyalty Card.png`
- `design/Side Menu.png`
- `design/Stock.png`
- `design/Card Item.png`

## Реализованная навигация

### Auth routes
- `splash`
- `auth/signin`
- `auth/signup`
- `auth/forgot`
- `auth/otp?email={email}`
- `auth/new-password?email={email}`

### Main routes
- `main/home`
- `main/catalog`
- `main/favorite`
- `main/profile`
- `main/edit-profile`
- `main/loyalty-card`
- `main/logout`
- `main/notifications`
- `main/cart`
- `main/orders`
- `main/settings`

## Что сейчас работает на mock state

- auth session и auth flow через `FakeAuthRepository`;
- recovery OTP через фиксированный тестовый код;
- home/catalog/favorite через `MockCatalogState`;
- profile/edit profile через локальный screen state;
- loyalty card через placeholder barcode block;
- часть навигационных переходов через `mockNavigate` / `mockReplace`.

## Что визуально уже соответствует макетам

- auth typography / spacing / dialog flow;
- register button disabled/enabled state;
- home promo/product presentation;
- bottom bar и side drawer;
- profile/edit profile structure;
- loyalty card layout.

## Что ещё требует полировки

- выравнивание UI и бизнес-логики в main screens;
- реальный barcode payload вместо placeholder графики;
- profile avatar flow;
- server-driven empty/error/loading states для `Home`, `Catalog`, `Profile`, `Favorite`;
- устранение временных mock singleton/state-holder решений.
