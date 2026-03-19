package com.example.sneakneak.data.profile.mapper

import com.example.sneakneak.data.profile.dto.ProfileInsertDto
import com.example.sneakneak.data.profile.dto.ProfileRowDto
import com.example.sneakneak.data.profile.dto.ProfileUpdateDto
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.model.UserProfileDraft

// Преобразования между DTO (`profiles`) и domain-моделями profile feature.
fun ProfileRowDto.toDomain(email: String?): UserProfile {
    return UserProfile(
        userId = userId,
        email = email,
        firstname = firstname.orEmpty(),
        lastname = lastname.orEmpty(),
        address = address.orEmpty(),
        phone = phone.orEmpty(),
        photo = photo,
    )
}

fun UserProfileDraft.toInsertDto(userId: String): ProfileInsertDto {
    return ProfileInsertDto(
        userId = userId,
        photo = photo.nullIfBlank(),
        firstname = firstname.nullIfBlank(),
        lastname = lastname.nullIfBlank(),
        address = address.nullIfBlank(),
        phone = phone.nullIfBlank(),
    )
}

fun UserProfileDraft.toUpdateDto(): ProfileUpdateDto {
    return ProfileUpdateDto(
        photo = photo.nullIfBlank(),
        firstname = firstname.nullIfBlank(),
        lastname = lastname.nullIfBlank(),
        address = address.nullIfBlank(),
        phone = phone.nullIfBlank(),
    )
}

private fun String?.nullIfBlank(): String? {
    // Для update/insert отправляем null вместо пустой строки, чтобы не засорять БД фиктивными значениями.
    return this?.trim()?.takeIf { it.isNotEmpty() }
}
