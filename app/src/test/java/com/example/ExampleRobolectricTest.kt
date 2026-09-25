package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GestureAction
import com.example.data.model.GestureTrigger
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.data.model.ThemeStyle
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetType
import com.example.ui.components.evaluateMathExpression
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Zenith", appName)
    }

    @Test
    fun `math expression evaluator computes correctly`() {
        assertEquals("120", evaluateMathExpression("15 * 8"))
        assertEquals("25", evaluateMathExpression("100 / 4"))
        assertEquals("42", evaluateMathExpression("40 + 2"))
        assertEquals("35", evaluateMathExpression("50 - 15"))
    }

    @Test
    fun `launcher models are well configured`() {
        assertNotNull(ThemeStyle.AMOLED_BLACK)
        assertNotNull(IconShape.SQUIRCLE)
        assertNotNull(IconThemeMode.MONOCHROME)
        assertEquals("Open App Drawer", GestureAction.OPEN_DRAWER.displayName)
        assertEquals("Swipe Up", GestureTrigger.SWIPE_UP.displayName)
    }

    @Test
    fun `widget sizes and types work properly`() {
        assertEquals("Compact", WidgetSize.COMPACT.displayName)
        assertEquals("Standard", WidgetSize.STANDARD.displayName)
        assertEquals("Expanded", WidgetSize.EXPANDED.displayName)

        val clockConfig = WidgetConfig(
            id = "w1",
            type = WidgetType.CLOCK,
            size = WidgetSize.EXPANDED
        )
        assertEquals(WidgetSize.EXPANDED, clockConfig.size)
        assertEquals(WidgetType.CLOCK, clockConfig.type)

        val countdownConfig = WidgetConfig(
            id = "w2",
            type = WidgetType.COUNTDOWN,
            size = WidgetSize.STANDARD,
            title = "Tokyo Vacation"
        )
        assertEquals("Tokyo Vacation", countdownConfig.title)
    }
}
