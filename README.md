# Sneakneak

`Sneakneak` — Android-приложение магазина кроссовок и спортивных товаров.
Проект сделан на `Kotlin + Jetpack Compose` с backend на `Supabase` и архитектурой `domain / data / presentation`.

## Основная идея

Пользователь может зарегистрироваться и войти, просматривать каталог, добавлять товары в избранное, редактировать профиль, загружать аватар и открыть карту лояльности со штрихкодом.

## Что реализовано

- Auth flow: `Splash`, `Sign In`, `Sign Up`, `Forgot Password`, `OTP`, `Create New Password`
- Main flow: `Home`, `Catalog`, `Favorite`, `Profile`, `Edit Profile`, `Loyalty Card`
- Реальные данные из Supabase для auth/profile/catalog/favorites
- Загрузка изображений товаров из `Storage` bucket `products`
- Загрузка аватара в `Storage` bucket `avatars`
- UI на Compose с переиспользуемыми компонентами и навигацией

## Демо-видео

Видео работы приложения: [видео](assets/video/project.mp4)


## Макет приложения

[Ссылка](https://www.figma.com/design/t7yNd853ZuGihgKLNexZSr/UP-01.03?node-id=1-2&t=7U5UF6Zit4S5xnSy-1) на макет

## Примечание по запуску

Для работы с Supabase нужно задать `SUPABASE_URL` и `SUPABASE_ANON_KEY` в `local.properties` (или через `gradle.properties`/env).
