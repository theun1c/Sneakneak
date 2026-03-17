package com.example.sneakneak.ui.assets

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignAssetsTest {

    @Test
    fun stockAndCardItemAssetsUseDesignFolder() {
        assertEquals("design/Stock.png", DesignAssets.Stock)
        assertEquals("design/Card Item.png", DesignAssets.CardItem)
    }

    @Test
    fun stockAndCardItemFilesExistInAppAssets() {
        assertTrue(File("app/src/main/assets/design/Stock.png").exists())
        assertTrue(File("app/src/main/assets/design/Card Item.png").exists())
    }

    @Test
    fun homeVisualTuningUsesExpectedImageSizing() {
        assertEquals(108, HomeVisualTuning.promotionHeightDp)
        assertEquals(132, HomeVisualTuning.productImageContainerSizeDp)
        assertEquals(124, HomeVisualTuning.productImageSizeDp)
        assertEquals(-6, HomeVisualTuning.productImageOffsetXDp)
        assertEquals(2, HomeVisualTuning.productTitleLineCount)
    }
}
