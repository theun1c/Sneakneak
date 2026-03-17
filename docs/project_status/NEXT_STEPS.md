# Next Steps

## Следующий этап

Следующий этап проекта должен быть посвящён не новой вёрстке, а стабилизации логики и постепенной замене mock-слоя на domain/data + реальную интеграцию.

## Рекомендуемый порядок

1. `auth logic`
2. `validators`
3. `fake/domain stabilization`
4. `real repositories`
5. `Supabase integration`
6. `polish / QA`

## Что именно делать дальше

### 1. Auth logic
- довести auth ViewModel и use case до финального контракта;
- перевести временный manual coroutine scope на устойчивый production-подход;
- сохранить текущие screen contracts и UI behavior.

### 2. Validators
- централизовать все обязательные валидаторы в `domain`;
- покрыть их unit-тестами;
- убрать дублирование правил по экранным слоям.

### 3. Fake/domain stabilization
- вывести `Profile`, `Catalog`, `Home`, `Favorite`, `Loyalty` из presentation-level mock state;
- завести интерфейсы репозиториев и use cases;
- сохранить текущий UI и навигацию без переписывания composable.

### 4. Real repositories
- подготовить data-layer implementations;
- скрыть transport/DTO details от UI;
- заменить `MockCatalogState` и screen-local mock state на repository-backed flows.

### 5. Supabase integration
- подключить real auth;
- подключить `profiles`, `products`, `categories`, `actions`, `favourite`;
- подключить storage flow для avatar/product/promotion media;
- сохранить уже собранные loading/error/success contracts.

### 6. Polish / QA
- пройти `docs/05_QA_CHECKLIST.md`;
- проверить пустые/ошибочные/loading состояния;
- проверить соответствие реальным данным и сценариям logout/recovery/profile/favorites;
- провести финальную визуальную полировку.

## Какие части уже готовы к data integration

- auth navigation contract;
- auth screen state contracts;
- `ProductCard` и product list composition;
- main shell;
- profile screen structure;
- loyalty card route and host screen;
- theme/design system;
- asset mapping и reusable components.

## Какие mock parts нужно заменять первыми

### Первая очередь
- `MockCatalogState`
- profile screen static data
- loyalty placeholder payload
- mock navigation helpers, если session routing станет fully data-driven

### Вторая очередь
- `FakeAuthRepository`
- manual composition root
- placeholder screens в shell

## Риски перед подключением backend

- main screens ещё не полностью соответствуют целевым loading/error/empty data states;
- profile/avatar flow пока не готов к media integration;
- loyalty screen пока не получает barcode из user id;
- часть текущих main ViewModel пока служит только экранным каркасом;
- Gradle/SDK окружение нужно стабилизировать отдельно, чтобы нормально гонять проверки.

## Зависимости следующего этапа

- нужен согласованный `domain` contract для profile/products/favorites;
- нужен единый подход к data errors и UI mapping;
- нужен composition root/DI подход для масштабирования beyond auth;
- после этого можно безопасно подключать Supabase без переделки UI-слоя.
