package com.ext.flipcardview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.flipcard.FlipCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val card = findViewById<FlipCardView>(R.id.flipCard)

        val view1 = layoutInflater.inflate(R.layout.front_card, null)
        val view2 = layoutInflater.inflate(R.layout.back_card, null)
        val view3 = layoutInflater.inflate(R.layout.third_card, null)

        card.addCard(view1)
        card.addCard(view2)
        card.addCard(view3)


        card.setOnClickListener {
            card.nextCard()
        }
    }
}