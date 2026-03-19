package com.example.sneakneak.di

// Слой DI: главный composition root приложения.
// Все feature получают зависимости отсюда, а UI не знает о конкретных data-реализациях.

import com.example.sneakneak.data.auth.FakeAuthRepository
import com.example.sneakneak.data.auth.remote.SupabaseAuthRemoteDataSource
import com.example.sneakneak.data.auth.repository.SupabaseAuthRepository
import com.example.sneakneak.data.favorites.FakeFavoritesRepository
import com.example.sneakneak.data.favorites.remote.SupabaseFavoritesRemoteDataSource
import com.example.sneakneak.data.favorites.repository.SupabaseFavoritesRepository
import com.example.sneakneak.data.profile.FakeProfileRepository
import com.example.sneakneak.data.profile.ProfileBootstrapper
import com.example.sneakneak.data.profile.avatar.SupabaseAvatarRemoteDataSource
import com.example.sneakneak.data.profile.remote.SupabaseProfileRemoteDataSource
import com.example.sneakneak.data.profile.repository.SupabaseProfileRepository
import com.example.sneakneak.data.products.FakeCategoriesRepository
import com.example.sneakneak.data.products.FakeProductsRepository
import com.example.sneakneak.data.products.remote.SupabaseProductsRemoteDataSource
import com.example.sneakneak.data.products.repository.SupabaseCategoriesRepository
import com.example.sneakneak.data.products.repository.SupabaseProductsRepository
import com.example.sneakneak.data.products.storage.ProductImageUrlResolver
import com.example.sneakneak.domain.auth.repository.AuthRepository
import com.example.sneakneak.domain.auth.usecase.AuthUseCases
import com.example.sneakneak.domain.auth.usecase.GetCurrentUserEmailUseCase
import com.example.sneakneak.domain.auth.usecase.GetCurrentUserIdUseCase
import com.example.sneakneak.domain.auth.usecase.ObserveSessionUseCase
import com.example.sneakneak.domain.auth.usecase.SendRecoveryCodeUseCase
import com.example.sneakneak.domain.auth.usecase.SignInWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.SignOutUseCase
import com.example.sneakneak.domain.auth.usecase.SignUpWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.UpdatePasswordUseCase
import com.example.sneakneak.domain.auth.usecase.VerifyRecoveryCodeUseCase
import com.example.sneakneak.domain.favorites.repository.FavoritesRepository
import com.example.sneakneak.domain.favorites.usecase.AddFavoriteUseCase
import com.example.sneakneak.domain.favorites.usecase.FavoritesUseCases
import com.example.sneakneak.domain.favorites.usecase.GetMyFavoriteProductsUseCase
import com.example.sneakneak.domain.favorites.usecase.ObserveFavoriteIdsUseCase
import com.example.sneakneak.domain.favorites.usecase.RefreshFavoriteIdsUseCase
import com.example.sneakneak.domain.favorites.usecase.RemoveFavoriteUseCase
import com.example.sneakneak.domain.favorites.usecase.ToggleFavoriteUseCase
import com.example.sneakneak.domain.loyalty.usecase.GetLoyaltyCardInfoUseCase
import com.example.sneakneak.domain.loyalty.usecase.LoyaltyUseCases
import com.example.sneakneak.domain.profile.repository.ProfileRepository
import com.example.sneakneak.domain.profile.usecase.CreateOrGetMyProfileUseCase
import com.example.sneakneak.domain.profile.usecase.GetCurrentUserProfileUseCase
import com.example.sneakneak.domain.profile.usecase.GetMyProfileUseCase
import com.example.sneakneak.domain.profile.usecase.ProfileUseCases
import com.example.sneakneak.domain.profile.usecase.UpdateMyProfileUseCase
import com.example.sneakneak.domain.profile.usecase.UpdateMyAvatarUseCase
import com.example.sneakneak.domain.profile.usecase.UpsertMyProfileUseCase
import com.example.sneakneak.domain.products.repository.CategoriesRepository
import com.example.sneakneak.domain.products.repository.ProductsRepository
import com.example.sneakneak.domain.products.usecase.GetBestSellerProductsUseCase
import com.example.sneakneak.domain.products.usecase.GetCatalogProductsUseCase
import com.example.sneakneak.domain.products.usecase.GetCategoriesUseCase
import com.example.sneakneak.domain.products.usecase.GetPromotionsUseCase
import com.example.sneakneak.domain.products.usecase.ProductsUseCases

// Слой DI: ручной composition root для MVP.
// Здесь связываются use case -> repository -> remote data source, а UI получает только готовые use case.
object AppContainer {
    private val supabaseConfig by lazy { SupabaseConfigProvider.fromBuildConfig() }

    private val supabaseClient by lazy { SupabaseClientProvider.getOrCreate(supabaseConfig) }

    private val profileRemoteDataSource by lazy { SupabaseProfileRemoteDataSource(supabaseClient) }
    private val avatarRemoteDataSource by lazy { SupabaseAvatarRemoteDataSource(supabaseClient) }
    private val productsRemoteDataSource by lazy { SupabaseProductsRemoteDataSource(supabaseClient) }
    private val favoritesRemoteDataSource by lazy { SupabaseFavoritesRemoteDataSource(supabaseClient) }

    private val profileBootstrapper by lazy { ProfileBootstrapper(profileRemoteDataSource) }
    private val productImageResolver by lazy { ProductImageUrlResolver(supabaseClient) }

    private val authRepository: AuthRepository by lazy {
        if (!supabaseConfig.isConfigured) {
            // Если ключи не заданы, приложение остается работоспособным через fake-репозиторий.
            // TODO(SUPABASE): move to explicit build variant/env toggle once full DI is introduced.
            return@lazy FakeAuthRepository()
        }

        runCatching {
            val remote = SupabaseAuthRemoteDataSource(supabaseClient)
            SupabaseAuthRepository(
                remote = remote,
                profileBootstrapper = profileBootstrapper,
            )
        }.getOrElse {
            // Безопасный fallback: UI-flow не падает при проблемах окружения или сети.
            FakeAuthRepository()
        }
    }

    private val profileRepository: ProfileRepository by lazy {
        if (!supabaseConfig.isConfigured || authRepository !is SupabaseAuthRepository) {
            return@lazy FakeProfileRepository(authRepository)
        }

        runCatching {
            SupabaseProfileRepository(
                authRepository = authRepository,
                remote = profileRemoteDataSource,
                bootstrapper = profileBootstrapper,
                avatarRemote = avatarRemoteDataSource,
            )
        }.getOrElse {
            FakeProfileRepository(authRepository)
        }
    }

    private val productsRepository: ProductsRepository by lazy {
        if (!supabaseConfig.isConfigured) {
            return@lazy FakeProductsRepository()
        }

        runCatching {
            SupabaseProductsRepository(
                remote = productsRemoteDataSource,
                imageResolver = productImageResolver,
            )
        }.getOrElse {
            FakeProductsRepository()
        }
    }

    private val categoriesRepository: CategoriesRepository by lazy {
        if (!supabaseConfig.isConfigured) {
            return@lazy FakeCategoriesRepository()
        }

        runCatching {
            SupabaseCategoriesRepository(productsRemoteDataSource)
        }.getOrElse {
            FakeCategoriesRepository()
        }
    }

    private val favoritesRepository: FavoritesRepository by lazy {
        if (!supabaseConfig.isConfigured || authRepository !is SupabaseAuthRepository) {
            return@lazy FakeFavoritesRepository(productsRepository)
        }

        runCatching {
            SupabaseFavoritesRepository(
                authRepository = authRepository,
                remote = favoritesRemoteDataSource,
                productsRemote = productsRemoteDataSource,
                imageResolver = productImageResolver,
            )
        }.getOrElse {
            FakeFavoritesRepository(productsRepository)
        }
    }

    val authUseCases: AuthUseCases by lazy {
        // Единая точка, через которую presentation-слой работает с auth-сценариями.
        // TODO(DATA): migrate this container to Hilt/Koin modules after auth/profile/catalog foundations are ready.
        AuthUseCases(
            signInWithEmail = SignInWithEmailUseCase(authRepository),
            signUpWithEmail = SignUpWithEmailUseCase(authRepository),
            sendRecoveryCode = SendRecoveryCodeUseCase(authRepository),
            verifyRecoveryCode = VerifyRecoveryCodeUseCase(authRepository),
            updatePassword = UpdatePasswordUseCase(authRepository),
            observeSession = ObserveSessionUseCase(authRepository),
            signOut = SignOutUseCase(authRepository),
            getCurrentUserId = GetCurrentUserIdUseCase(authRepository),
            getCurrentUserEmail = GetCurrentUserEmailUseCase(authRepository),
        )
    }

    val profileUseCases: ProfileUseCases by lazy {
        ProfileUseCases(
            getMyProfile = GetMyProfileUseCase(profileRepository),
            createOrGetMyProfile = CreateOrGetMyProfileUseCase(profileRepository),
            getCurrentUserProfile = GetCurrentUserProfileUseCase(profileRepository),
            upsertMyProfile = UpsertMyProfileUseCase(profileRepository),
            updateMyProfile = UpdateMyProfileUseCase(profileRepository),
            updateMyAvatar = UpdateMyAvatarUseCase(profileRepository),
        )
    }

    val productsUseCases: ProductsUseCases by lazy {
        ProductsUseCases(
            getCatalogProducts = GetCatalogProductsUseCase(productsRepository),
            getBestSellerProducts = GetBestSellerProductsUseCase(productsRepository),
            getPromotions = GetPromotionsUseCase(productsRepository),
            getCategories = GetCategoriesUseCase(categoriesRepository),
        )
    }

    val favoritesUseCases: FavoritesUseCases by lazy {
        FavoritesUseCases(
            observeFavoriteIds = ObserveFavoriteIdsUseCase(favoritesRepository),
            refreshFavoriteIds = RefreshFavoriteIdsUseCase(favoritesRepository),
            getMyFavoriteProducts = GetMyFavoriteProductsUseCase(favoritesRepository),
            addFavorite = AddFavoriteUseCase(favoritesRepository),
            removeFavorite = RemoveFavoriteUseCase(favoritesRepository),
            toggleFavorite = ToggleFavoriteUseCase(favoritesRepository),
        )
    }

    val loyaltyUseCases: LoyaltyUseCases by lazy {
        LoyaltyUseCases(
            getLoyaltyCardInfo = GetLoyaltyCardInfoUseCase(
                authRepository = authRepository,
                profileRepository = profileRepository,
            ),
        )
    }
}
