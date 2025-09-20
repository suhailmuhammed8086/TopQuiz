package com.example.quiztap.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quiztap.R
import com.example.quiztap.base.QuizTapBaseActivity
import com.example.quiztap.components.MultiOptionView
import com.example.quiztap.databinding.ActivityHomeBinding
import com.example.quiztap.ui.game.settings.GameSettingsActivity

class HomeActivity : QuizTapBaseActivity() , View.OnClickListener{
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        // TODO: Remove this after multiplay mode implemented. 
        binding.btnQuickPlay.callOnClick()
    }

    private fun initView() {
        binding.btnQuickPlay.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.bgView.animateShapes()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnQuickPlay -> {
                GameSettingsActivity.start(this)
            }
        }
    }
}