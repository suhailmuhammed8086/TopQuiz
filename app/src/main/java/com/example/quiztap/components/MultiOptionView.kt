package com.example.quiztap.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.example.quiztap.R

class MultiOptionView : ConstraintLayout {
    private var options = arrayListOf<Option>()
    private var optionViews = arrayListOf<AppCompatTextView>()
    private var selectedOptionId: Int = -1
    private var selectionView = View(context)
    private var onOptionSelectListener: ((Option) -> Unit)? = null

    init {
        options.addAll(listOf(Option(1, "ABBBBB"), Option(2, "BCCCCC")))
    }

    fun loadOptions(vararg options: Option): MultiOptionView {
        this.options.clear();this.options.addAll(options)
        setOptionViews()
        setSelectionView()
        return this
    }

    private fun onOptionSelected(view: View, updateListener: Boolean = true) {
        selectedOptionId = (view.tag as? Int) ?: -1
        selectionView.animate().translationX(view.x)
        val selectedColor = ContextCompat.getColor(context, R.color.white)
        val unSelectedColor = ContextCompat.getColor(context, R.color.white)
        optionViews.forEach {
            if (it.id == view.id) {
                it.setTextColor(selectedColor)
                it.typeface = ResourcesCompat.getFont(context, R.font.montserrat_semibold)
            } else {
                it.setTextColor(unSelectedColor)
                it.typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
            }
        }
        if (updateListener) {
            val selectedOption = options.find { it.id == selectedOptionId }
            selectedOption?.let { onOptionSelectListener?.invoke(it) }
        }
    }

    fun setOnOptionSelectedListener(listener: (Option) -> Unit): MultiOptionView {
        onOptionSelectListener = listener
        return this
    }
    private fun setOptionViews() {
        optionViews.clear()
        removeAllViews()
        selectionView.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_10r)
        selectionView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.app_four))
        addView(selectionView)
        options.forEachIndexed { index, option ->
            val view = AppCompatTextView(context).apply {
                text = option.title
                gravity = Gravity.CENTER
                id = generateViewId()
                textSize = 16f
                tag = option.id
                includeFontPadding = false
                setPadding(0,5,0,5)
                typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
            }

            val lp = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT) // width=0 for constraint-based sizing

            view.layoutParams = lp
            view.setOnClickListener {
                onOptionSelected(view)
            }
            addView(view)
            optionViews.add(view)
        }

        var prevId = PARENT_ID
        for (i in 0..<childCount) {
            val child = getChildAt(i)
            if (child !is AppCompatTextView) continue
            val nextChildId = getChildAt(i + 1)?.id ?: PARENT_ID
            child.updateLayoutParams<ConstraintLayout.LayoutParams> {
                if (prevId == PARENT_ID) {
                    startToStart = PARENT_ID
                } else {
                    startToEnd = prevId
                }
                if (nextChildId == PARENT_ID) {
                    endToEnd = PARENT_ID
                } else {
                    endToStart = nextChildId
                }

                topToTop = PARENT_ID
                bottomToBottom = PARENT_ID

            }
            prevId = child.id
        }

        requestLayout()
    }

    private fun setSelectionView() {
        post {
            val firstChild = optionViews.first()
            val width = firstChild.width
            val height = optionViews.maxOf { it.height }
            selectionView.layoutParams = LayoutParams(width, this.measuredHeight).apply {
                topToTop = PARENT_ID
                bottomToBottom = PARENT_ID

            }
        }
    }

    fun setSelectedOption(id: Int) {
        post {
            val selectedOptionView = optionViews.find { it.tag == id }
            selectedOptionView?.let {
                onOptionSelected(selectedOptionView, false)
            }
        }
    }




    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    data class Option(
        val id: Int,
        val title: String
    )
}