package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File


class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setting the description of the post
        // 2. a button to launch camera
        // 3. image view to show the picture the user taken
        // 4. a button to save and send the post to our parse

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {

            // send post to server
            // get the description

            val description = findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()

            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else  {
                // TODO: Print error log message
                // TODO show a toast to user let them know to take picture
            }

        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            goToLogoutActivity()
        }
        findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // launch camera to let the user take picture
            onLaunchCamera()
        }

        queryPosts()


    }

    fun goToLogoutActivity(){
        ParseUser.logOut()

        val intent = Intent(this@MainActivity, LoginActivity::class.java)

        startActivity(intent)

    }

    // send object to our parse server
    fun submitPost(description: String, user: ParseUser, file:File){
        // Create the post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            if(exception != null){
                // some thing sas went wrong
                Log.e(TAG, "Error while saving post")
                exception.printStackTrace()

                // show a toast to tell user some thing went wrong with saving post
            } else{
                Log.i(TAG, "Successfully saved post")
                //TODO: resetting the edit field to be empty
                // TODO: reset the imageview to empty
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }


    private fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // find all post object
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post>{
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
               if(e != null) {
                   Log.e(TAG, "Error fetching posts")
               } else{
                   if (posts != null){
                       for (post in posts){
                           Log.i(TAG, "Post: " + post.getDescription() + " , USERNAME: " +
                           post.getUser()?.username)
                       }
                   }
               }
            }
        })
    }
    companion object {
        const val TAG = "MainActivity"
    }
    // query for all posts in our server
}