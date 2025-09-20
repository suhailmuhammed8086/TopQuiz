package com.example.quiztap.components

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.example.quiztap.R
import com.example.quiztap.databinding.LayoutTwoSideOptionBinding

class LeftRightOptionVIew: FrameLayout {

    private val binding: LayoutTwoSideOptionBinding = LayoutTwoSideOptionBinding.inflate(LayoutInflater.from(context),this, true)


    private var listener: Listener? = null
    interface Listener {
        fun onLeftButtonClick()
        fun onRightButtonClick()
    }

    init {
        binding.btnLeft.setOnClickListener {
            listener?.onLeftButtonClick()
        }

        binding.btnRight.setOnClickListener {
            listener?.onRightButtonClick()
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setValue(value: String) {
        binding.tvValue.text = value
    }

    fun enableLeftButton(enable: Boolean) {
        binding.btnLeft.isEnabled = enable
        onButtonEnable(enable, binding.btnLeft, binding.ivLeft)
    }
    fun enableRightButton(enable: Boolean) {
        onButtonEnable(enable, binding.btnRight, binding.ivRight)
    }

    fun onButtonEnable(enable: Boolean, buttonView: View, imageView: AppCompatImageView) {
        buttonView.isEnabled = enable
        if (enable) {
            buttonView.alpha = ENABLED_ALPHA
            imageView.alpha = ENABLED_ALPHA
        } else {
            buttonView.alpha = DISABLED_ALPHA
            imageView.alpha = DISABLED_ALPHA
        }
    }

    companion object {
        private const val ENABLED_ALPHA = 1f
        private const val DISABLED_ALPHA = 0.4f
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)
}