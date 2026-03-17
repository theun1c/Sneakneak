package com.example.sneakneak.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppThemeTokensTest {

    @Test
    fun spacing_scale_is_incremental() {
        assertEquals(4f, AppSpacing.xs.value)
        assertEquals(8f, AppSpacing.sm.value)
        assertEquals(16f, AppSpacing.md.value)
        assertEquals(24f, AppSpacing.lg.value)
        assertEquals(32f, AppSpacing.xl.value)
    }

    @Test
    fun shape_scale_grows_from_small_to_large() {
        assertTrue(AppCorners.small.value < AppCorners.medium.value)
        assertTrue(AppCorners.medium.value < AppCorners.large.value)
    }
}
