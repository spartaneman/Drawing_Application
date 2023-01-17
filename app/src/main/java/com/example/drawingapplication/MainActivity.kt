package com.example.drawingapplication

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView?  =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(5.toFloat())

        val ib_brush_size: ImageButton = findViewById(R.id.ib_brush)
        ib_brush_size.setOnClickListener {
            showBrushSizeChooseDialog()
        }

    }

    //Function will choose the size of the brush
    //A diaglog is like a pop up on the screen
    private fun showBrushSizeChooseDialog()
    {
        val brushDialog = Dialog(this)
        //set how the diaglog should look like
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")

        val smallBtn : ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)

        brushDialog.show()
        //Set brush sizes according to which button has been pressed
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(15.toFloat())
            brushDialog.dismiss()
        }

        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

    }
}