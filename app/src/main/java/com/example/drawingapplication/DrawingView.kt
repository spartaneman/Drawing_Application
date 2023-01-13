package com.example.drawingapplication

import android.content.Context
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
    private var mDrawPath: CustomPath? = null

    //This allows the transfer of variables within the class and only within the class
    //Path class encapsulate compound geometric paths consisting lines and curves.
    //It can be drawn of a canvas.
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path(){

    }

}