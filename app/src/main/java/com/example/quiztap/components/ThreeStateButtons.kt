package com.example.quiztap.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class ThreeStateButtons : AppCompatTextView, View.OnClickListener {
    enum class State {
        STATE_ONE,
        STATE_TWO,
        STATE_THREE;

        fun nextState(): State {
            return when (this) {
                STATE_ONE -> STATE_TWO
                STATE_TWO -> STATE_THREE
                STATE_THREE -> STATE_THREE
            }
        }
    }

    private var state = State.STATE_ONE
    private var clickListener: OnClickListener? = null
    private var stateChangeListener: ((State) -> Unit)? = null

    init {
        super.setOnClickListener(this)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickListener = l
    }

    fun setStateChangeListener(onChange: (state: State) -> Unit) {
        stateChangeListener = onChange
    }

    override fun onClick(v: View?) {
        state = state.nextState()
        stateChangeListener?.invoke(state)
        clickListener?.onClick(v)
    }

    fun getState() = state
    fun setState(state: State) {
        this.state = state
        stateChangeListener?.invoke(state)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

}