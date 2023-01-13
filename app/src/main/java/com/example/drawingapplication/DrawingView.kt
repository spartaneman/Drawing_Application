package com.example.drawingapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View


/*
*The purpose of this class is to create a view that will
* Allow the user to draw in it
* */
class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {

    //be able to draw
    //we need to know the colors
    //the type of style
    //Thickness of brush
    //need a bitmap
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap?= null
    private var mDrawPaint: Paint?= null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    //the canvas we want to draw on
    private var canvas: Canvas?= null

    init {
        setUpDrawing()
    }

    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        //how lines meet
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        //how lines start and end
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    //This allows the transfer of variables within the class and only within the class
    //Path class encapsulate compound geometric paths consisting lines and curves.
    //It can be drawn of a canvas.
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path(){

    }

}