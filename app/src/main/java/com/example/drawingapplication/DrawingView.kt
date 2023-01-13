package com.example.drawingapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
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

    //onSizeChanged - this is called during layout when the size of the view has changed
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    //If fails change Canvas to Canvas?
    //This is what should happen when we draw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f, mCanvasPaint)

        //make sure draw path is not empty
        //This deals with what should happen when we draw
        if(!mDrawPath!!.isEmpty){
            //how thick the paint
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    //This is When we Should draw
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action)
        {
            //when the user touches the screen
            MotionEvent.ACTION_DOWN -> {
                //how thick the path
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)
            }
            //when the user moves their finger
            MotionEvent.ACTION_MOVE-> {
                mDrawPath!!.lineTo(touchX!!,touchY!!)
            }

            //when the user lifts their finger
            MotionEvent.ACTION_UP -> {
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> {return false}


        }
        return true
    }

    //This allows the transfer of variables within the class and only within the class
    //Path class encapsulate compound geometric paths consisting lines and curves.
    //It can be drawn of a canvas.
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path(){

    }

    //NOTES

    //

}