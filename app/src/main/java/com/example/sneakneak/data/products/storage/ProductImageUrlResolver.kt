package com.example.sneakneak.data.products.storage

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.hours

// Сервис data-слоя для построения URL изображений товаров из bucket `products`.
// Поддерживает public bucket и signed URL fallback, а также кеширует уже найденные пути.
class ProductImageUrlResolver(
    private val supabase: SupabaseClient,
) {
    private val cache = mutableMapOf<String, String?>()
    private var indexedPathsByProductId: Map<String, String>? = null
    private var bucketIsPublic: Boolean? = null

    suspend fun resolveProductPhotoUrl(
        productId: String,
        photo: String?,
    ): String? {
        // 1) Если в БД уже лежит прямой URL, используем его как есть.
        if (photo.isUrl()) return photo
        val cleanPhoto = photo?.trim().orEmpty()

        // 2) Пытаемся разрешить `photo` как путь внутри bucket.
        if (cleanPhoto.isNotBlank()) {
            resolveToUrl(cleanPhoto)?.let { return it }
            resolveToUrl(cleanPhoto.removePrefix("/"))?.let { return it }
        }

        // 3) Ищем по предварительно индексированным путям (помогает при нестандартных layout в storage).
        val indexedPath = getIndexedPaths()[productId]
        if (!indexedPath.isNullOrBlank()) {
            resolveToUrl(indexedPath)?.let { return it }
        }

        // 4) Последний шаг: эвристический набор кандидатов `<id>.png`, `products/<id>.png` и т.д.
        val candidates = buildPathCandidates(productId = productId, photo = photo)
        for (path in candidates) {
            resolveToUrl(path)?.let { return it }
        }
        return null
    }

    private suspend fun resolveToUrl(path: String): String? {
        val normalized = path.trim().removePrefix("/")
        if (normalized.isBlank()) return null

        val key = "$PRODUCTS_BUCKET:$normalized"
        if (cache.containsKey(key)) {
            val cached = cache[key]
            if (!cached.isNullOrBlank()) return cached
            return null
        }

        val bucket = supabase.storage.from(PRODUCTS_BUCKET)
        val resolved = if (isBucketPublic()) {
            runCatching { bucket.publicUrl(normalized) }.getOrNull()
        } else {
            runCatching {
                bucket.createSignedUrl(path = normalized, expiresIn = 24.hours)
            }.getOrElse {
                runCatching { bucket.publicUrl(normalized) }.getOrNull()
            }
        }

        cache[key] = resolved
        return resolved
    }

    private suspend fun getIndexedPaths(): Map<String, String> {
        indexedPathsByProductId?.let { return it }

        val bucket = supabase.storage.from(PRODUCTS_BUCKET)
        val indexed = try {
            val paths = linkedSetOf<String>()

            suspend fun appendFrom(prefix: String) {
                val entries = bucket.list(prefix = prefix) {
                    limit = 1_000
                }
                entries.forEach { entry ->
                    if (entry.id != null) {
                        val fullPath = if (prefix.isBlank()) entry.name else "$prefix/${entry.name}"
                        paths.add(fullPath)
                    }
                }
            }

            appendFrom(prefix = "")
            appendFrom(prefix = "products")

            val rootEntries = bucket.list(prefix = "") {
                limit = 1_000
            }
            val folderNames = rootEntries
                .filter { it.id == null }
                .map { it.name.removeSuffix("/") }
                .filter { it.isNotBlank() }

            folderNames.forEach { folder ->
                runCatching { appendFrom(prefix = folder) }
            }

            // Индексируем файлы по UUID из имени, чтобы связать storage объект с product.id.
            val uuidRegex = UUID_REGEX.toRegex()
            buildMap {
                paths.forEach { path ->
                    val productId = uuidRegex.find(path)?.value ?: return@forEach
                    // keep the first discovered path to avoid random replacements
                    putIfAbsent(productId, path)
                }
            }
        } catch (_: Throwable) {
            emptyMap()
        }

        indexedPathsByProductId = indexed
        return indexed
    }

    private suspend fun isBucketPublic(): Boolean {
        bucketIsPublic?.let { return it }
        val value = runCatching {
            supabase.storage.retrieveBucketById(PRODUCTS_BUCKET)?.public
        }.getOrNull() == true
        bucketIsPublic = value
        return value
    }

    private fun buildPathCandidates(
        productId: String,
        photo: String?,
    ): List<String> {
        val cleanPhoto = photo?.trim().orEmpty()
        val extensions = listOf("png", "jpg", "jpeg", "webp")
        return buildSet {
            if (cleanPhoto.isNotBlank()) {
                add(cleanPhoto)
                val normalized = cleanPhoto.removePrefix("/")
                add(normalized)
                if (!normalized.contains('.')) {
                    extensions.forEach { ext ->
                        add("$normalized.$ext")
                    }
                }
            }

            extensions.forEach { ext ->
                add("$productId.$ext")
                add("products/$productId.$ext")
                add("$productId/main.$ext")
                add("products/$productId/main.$ext")
            }
        }.toList()
    }

    private fun String?.isUrl(): Boolean {
        if (this.isNullOrBlank()) return false
        val value = trim().lowercase()
        return value.startsWith("http://") || value.startsWith("https://")
    }

    private companion object {
        const val PRODUCTS_BUCKET = "products"
        const val UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    }
}
