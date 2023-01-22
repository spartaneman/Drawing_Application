package com.example.drawingapplication

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import android.Manifest
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView?  =null

    //Added the permission in the Android Manifest first
    //Requesting the user for permission
    private val permissionRequest : ActivityResultLauncher<Array<String>> =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions ->
                permissions.entries.forEach{
                    val permissionName = it.key
                    val isGranted = it.value
                    if(isGranted){
                        Toast.makeText(this, "Permission Granted for $permissionName", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this, "Permission Denied for $permissionName", Toast.LENGTH_SHORT).show()
                    }

                }
            }
    //current paint selected
    private var mCurrentImageButtonPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(5.toFloat())

        //We can treat the Linear layout as an array
        val linearLayoutPaints = findViewById<LinearLayout>(R.id.ll_paint_colors)

        val btnImage: ImageButton = findViewById(R.id.ib_gallery)

        //THIS WILL LAUNCH THE PERMISSION REQUEST
        //You must check that the build version matches and the shouldShow will state whether the
        //permission exists.
        btnImage.setOnClickListener {
                requestStoragePermission()
        }

        //this imageButton will be used to change the brush size
        //the onclicklistener will then call the function to show the dialog and change the brushSize
        val ibBrushSize: ImageButton = findViewById(R.id.ib_brush)
        ibBrushSize.setOnClickListener {
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

    fun paintClicked(view: View)
    {
        if(view !==mCurrentImageButtonPaint)
        {
            val imageButton = view as ImageButton
            //the tag will be the hex value
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)
            imageButton!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            mCurrentImageButtonPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )

            mCurrentImageButtonPaint = view
        }
    }

    //Function will show Permission Request when called
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            showRationalDialog("Drawing Application", "Drawing App Requires Access to your " +
                    "External Storage to be able to change your image background")
        }
        else{
            permissionRequest.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
                //TODO - Add Writing External Storage
            ))
        }
    }

    private fun showRationalDialog(
        title: String,
        message: String,
    ){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setNeutralButton("Cancel"){dialog, _->dialog.dismiss()}
        builder.create().show()
    }
}