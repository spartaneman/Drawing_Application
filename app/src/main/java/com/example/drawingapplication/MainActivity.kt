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
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.provider.MediaStore
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView?  =null

    //current paint selected b
    private var mCurrentImageButtonPaint: ImageButton? = null

    //Creating a Activity Launcher to be used by the Intent to select an image for the
    //External Storage
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode == RESULT_OK && result.data != null)
            {
                //URI is the location of the image in the device
                val imageBackground : ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }
        }


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

                        //Creating Intent to go to Gallery
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        //Goal is not just to go to gallery but to select and get an image, A launcher must be created for this
                        openGalleryLauncher.launch(galleryIntent)
                    }
                    else
                    {
                        Toast.makeText(this, "Permission Denied for $permissionName", Toast.LENGTH_SHORT).show()
                    }
                }
            }


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


        //UndoButton
        //Remove the last path in the array
        val ibUndo: ImageButton = findViewById(R.id.ib_undo)
        ibUndo.setOnClickListener {
            drawingView?.undoLastPath()
        }

        //this imageButton will be used to change the brush size
        //the onclicklistener will then call the function to show the dialog and change the brushSize
        val ibBrushSize: ImageButton = findViewById(R.id.ib_brush)
        ibBrushSize.setOnClickListener {
            showBrushSizeChooseDialog()
            drawingView
        }

        //Save the image into the external hard drive.
        //Make sure the permission is set to write into external
        //We want to know where we want to write
        val ibSave: ImageButton = findViewById(R.id.ib_save)





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

    //Dialog to come up when permission given or not
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

    //Get the bitmap of the view and return a bitmap from the view.
    private fun getBitmapFromView(view: View): Bitmap{
        val rBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        //bind the canvas that is in the view
        //in this case there are 3 views, the background view, canvas and the drawing view
        val canvas = Canvas(rBitmap)
        val bgDrawable = view.background
        if(bgDrawable != null)
        {
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return rBitmap
    }

    //A coroutine that will will take a bitmap nullable and return a String
    //Remember must be suspend and designate the type of coroutine
    private suspend fun saveBitmapFile(mBitmap: Bitmap?): String
    {
        var result = ""
        withContext(Dispatchers.IO){
            if(mBitmap != null){
                try {
                    //Since we are out putting an image
                    //the buffer size starts at 32 bytes but increases
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    //create a file and store it
                    //give us the path of where the file will be created as a file item
                    //file separator is a string / and then add app name
                    //Adding the currentTime in Millis/1000 to have unique id per image
                    val file = File(externalCacheDir?.absoluteFile.toString()
                    + File.separator + "DrawingApp" + System.currentTimeMillis()/1000+".png"
                    )

                    val fileOut = FileOutputStream(file)
                    fileOut.write(bytes.toByteArray())
                    fileOut.close()

                    //Location where image was saved
                    result = file.absolutePath

                    //this will run in the U.i Thread
                    runOnUiThread{
                        if (result.isNotEmpty())
                        {
                            Toast.makeText(
                                this@MainActivity, "Image saved at $result in your device", Toast.LENGTH_LONG).show()
                        }else
                        {
                            Toast.makeText(this@MainActivity, "Image was unable to be saved", Toast.LENGTH_LONG).show()
                        }
                    }
                }catch (e: java.lang.Exception)
                {
                    //will print to the log
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}


/*
* COROUTINES
* By Using coroutines we can do put heavier calculations
* into a different thread than the U.I thread,
*
* Benefits
* -light weight
*   -You can run many co routines on a single thread due to support of suspension
* -Fewer Memory Leaks
* -Built in Cancellation support
* -Jetpack Integration
*
* Coroutine Scopes
* -structured concurrency
* - Different Scopes
*   -viewModelScope
*       view model class
*   -lifeCycleScope
*       activity fragment
*   -Custom Scope attach to job and clear it once the Job is done.
*
* Suspend
*   -adding this infront of the Function will make the function a coroutine
*   -must also run in in a different thread.
*   -withContext(Dispatcher.IO)
* */