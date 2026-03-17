# Mock Logic Foundation Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Перевести приложение со статичных экранов на рабочую внутреннюю логику на тестовых данных без реального backend.

**Architecture:** Ввести чистые слои `domain`, `data`, `presentation`, где `domain` содержит модели, валидаторы и use case, `data` реализует fake repositories in-memory, а `presentation` только отображает `UiState` и отправляет `UiEvent`. Навигация и одноразовые действия идут через `UiEffect`, loading/error/success живут в state.

**Tech Stack:** Kotlin, Jetpack Compose, Android ViewModel, Coroutines/Flow, JUnit4, ручной composition root без сети и без Supabase.

---

## Chunk 1: Auth Mock Flow

### Task 1: Domain contract and validators

**Files:**
- Create: `app/src/main/java/com/example/sneakneak/domain/auth/model/AuthModels.kt`
- Create: `app/src/main/java/com/example/sneakneak/domain/auth/repository/AuthRepository.kt`
- Create: `app/src/main/java/com/example/sneakneak/domain/auth/validator/AuthValidators.kt`
- Create: `app/src/test/java/com/example/sneakneak/domain/auth/AuthValidatorsTest.kt`

- [ ] **Step 1: Write failing tests for email, password, confirm password and otp validators**
- [ ] **Step 2: Run validator tests and verify RED**
- [ ] **Step 3: Add domain validator objects and error models**
- [ ] **Step 4: Run validator tests and verify GREEN**

### Task 2: Fake auth repository and use cases

**Files:**
- Create: `app/src/main/java/com/example/sneakneak/data/auth/FakeAuthRepository.kt`
- Create: `app/src/main/java/com/example/sneakneak/domain/auth/usecase/AuthUseCases.kt`
- Create: `app/src/test/java/com/example/sneakneak/data/auth/FakeAuthRepositoryTest.kt`

- [ ] **Step 1: Write failing tests for sign up, sign in, recovery, otp verification and password update on in-memory data**
- [ ] **Step 2: Run repository tests and verify RED**
- [ ] **Step 3: Implement fake repository and minimal use case wrappers**
- [ ] **Step 4: Run repository tests and verify GREEN**

### Task 3: Auth presentation integration

**Files:**
- Modify: `app/src/main/java/com/example/sneakneak/ui/auth/signin/SignInScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/auth/signup/SignUpScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/auth/forgot/ForgotPasswordScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/auth/otp/OtpVerificationScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/auth/newpassword/NewPasswordScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/navigation/Navigation.kt`
- Create: `app/src/main/java/com/example/sneakneak/di/AppContainer.kt`
- Test: `app/src/test/java/com/example/sneakneak/ui/auth/AuthFlowViewModelTest.kt`

- [ ] **Step 1: Write failing ViewModel tests for auth flows on fake data**
- [ ] **Step 2: Run auth ViewModel tests and verify RED**
- [ ] **Step 3: Inject use cases into auth ViewModel and move all state transitions into ViewModel**
- [ ] **Step 4: Run auth ViewModel tests and verify GREEN**

## Chunk 2: Profile Mock Flow

### Task 4: Profile fake repository and edit flow

**Files:**
- Create: `app/src/main/java/com/example/sneakneak/domain/profile/...`
- Create: `app/src/main/java/com/example/sneakneak/data/profile/...`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/profile/ProfileScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/profile/EditProfileScreen.kt`
- Test: `app/src/test/java/com/example/sneakneak/ui/main/profile/...`

- [ ] **Step 1: Add failing tests for load/edit/save profile**
- [ ] **Step 2: Implement fake profile flow**
- [ ] **Step 3: Integrate screens without visual changes**
- [ ] **Step 4: Re-run tests**

## Chunk 3: Catalog, Home, Favorite

### Task 5: Product and favorite mock flows

**Files:**
- Create: `app/src/main/java/com/example/sneakneak/domain/products/...`
- Create: `app/src/main/java/com/example/sneakneak/domain/favorites/...`
- Create: `app/src/main/java/com/example/sneakneak/data/products/...`
- Create: `app/src/main/java/com/example/sneakneak/data/favorites/...`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/home/HomeScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/catalog/CatalogScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/favorite/FavoriteScreen.kt`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/common/ProductContent.kt`

- [ ] **Step 1: Add failing tests for categories, best sellers and toggle favorite**
- [ ] **Step 2: Implement fake repositories and use cases**
- [ ] **Step 3: Bind screens to ViewModel state**
- [ ] **Step 4: Re-run tests**

## Chunk 4: Loyalty

### Task 6: Loyalty mock data

**Files:**
- Create: `app/src/main/java/com/example/sneakneak/domain/loyalty/...`
- Create: `app/src/main/java/com/example/sneakneak/data/loyalty/...`
- Modify: `app/src/main/java/com/example/sneakneak/ui/main/loyalty/LoyaltyCardScreen.kt`

- [ ] **Step 1: Add failing tests for loyalty card payload**
- [ ] **Step 2: Implement fake data source/use case**
- [ ] **Step 3: Bind screen to ViewModel**
- [ ] **Step 4: Re-run tests**

## Chunk 5: Verification and cleanup

### Task 7: Final verification

**Files:**
- Modify: `app/src/test/java/...`
- Modify: `app/src/main/java/...`

- [ ] **Step 1: Run focused unit tests by feature**
- [ ] **Step 2: Fix regressions**
- [ ] **Step 3: Run final verification set**
- [ ] **Step 4: Summarize remaining environment blockers**
