package com.example.sneakneak.data.profile

import com.example.sneakneak.data.profile.dto.ProfileInsertDto
import com.example.sneakneak.data.profile.dto.ProfileRowDto
import com.example.sneakneak.data.profile.remote.ProfileRemoteDataSource

// Служебный data-компонент: гарантирует наличие записи в `public.profiles` для текущего auth user.
// Используется из auth и profile репозиториев, чтобы экраны могли работать без ручных миграций в UI.
class ProfileBootstrapper(
    private val remote: ProfileRemoteDataSource,
) {
    suspend fun createIfMissing(
        userId: String,
        emailHint: String?,
        nameHint: String?,
    ): ProfileRowDto {
        val existing = remote.getByUserId(userId)
        if (existing != null) return existing

        // Имя строим из sign-up nameHint, иначе из email local-part (компромисс для MVP).
        val name = toNameParts(
            nameHint = nameHint,
            emailHint = emailHint,
        )
        remote.insert(
            ProfileInsertDto(
                userId = userId,
                firstname = name.firstName,
                lastname = name.lastName,
                address = null,
                phone = null,
                photo = null,
            ),
        )
        return remote.getByUserId(userId)
            ?: throw IllegalStateException("Profile was not created")
    }

    private fun toNameParts(
        nameHint: String?,
        emailHint: String?,
    ): NameParts {
        val normalizedName = nameHint?.trim().orEmpty()
        if (normalizedName.isNotEmpty()) {
            val words = normalizedName
                .split("\\s+".toRegex())
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            if (words.isNotEmpty()) {
                return NameParts(
                    firstName = words.first(),
                    lastName = words.drop(1).joinToString(" ").ifBlank { null },
                )
            }
        }

        val emailBase = emailHint
            ?.substringBefore("@")
            ?.trim()
            .orEmpty()
            .ifBlank { "User" }
        return NameParts(firstName = emailBase, lastName = null)
    }
}

private data class NameParts(
    val firstName: String?,
    val lastName: String?,
)
