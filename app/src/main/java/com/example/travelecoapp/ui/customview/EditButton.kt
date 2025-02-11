package com.example.travelecoapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.travelecoapp.R

class EditButton : AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        canvas?.let { super.onDraw(it) }
        val textColor = if (isEnabled) ContextCompat.getColor(context, R.color.light_font) else ContextCompat.getColor(context, R.color.disabled_text_button)
        setTextColor(textColor)
        background = if (isEnabled) enabledBackground else disabledBackground
    }

    private fun init() {
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disabled) as Drawable
    }

}