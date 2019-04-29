package com.example.liorkaramany.opticsdatabase;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an activity which allows the user to capture a document and upload it along with the customer, or replace an existing document.
 */
public class Document extends AppCompatActivity {

    /**
     * An constant used for requesting the image from the camera.
     */
    static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * An constant used for requesting to take a photo.
     */
    static final int REQUEST_TAKE_PHOTO = 1;


    /**
     * An ImageView that displays the captured document.
     */
    ImageView img;

    /**
     * A button used to go back to the previous activity.
     */
    Button back;

    /**
     * A button used to upload the captured document.
     */
    Button upload;

    /**
     * A Storage Reference to the image's location.
     */
    StorageReference r;

    /**
     * A Database Reference to the customer that contains the captured document.
     */
    DatabaseReference ref;

    /**
     * A Database Reference to the document that is captured.
     */
    DatabaseReference imgRef;

    /**
     * A ProgressBar that shows the image uploading progress.
     */
    ProgressBar progressBar;

    /**
     * The URI of the captured photo.
     */
    Uri uri = null;

    /**
     * The Bitmap of the captured photo.
     */
    Bitmap image = null;

    /**
     * The path of the captured photo.
     */
    String mCurrentPhotoPath;

    /**
     * The Storage Task which maintains the document uploading process.
     */
    StorageTask uploadTask;

    /**
     * The URL of an existing document.
     */
    String url;

    /**
     * The ID of an existing document.
     */
    String idFromIntent;

    /**
     * The ID of the image of an existing document.
     */
    String imgId;

    /**
     * The first name of the customer.
     */
    String fname;

    /**
     * The last name of the customer.
     */
    String lname;

    /**
     * The personal ID of the customer.
     */
    String customerID;

    /**
     * The address of the customer.
     */
    String address;

    /**
     * The city of the customer.
     */
    String city;

    /**
     * The phone number of the customer.
     */
    String phone;

    /**
     * The phone number of the customer.
     */
    String mobile;

    /**
     * The type of equipment that the customer bought.
     */
    int typeID;

    /**
     * A sign which tells if a new customer is being uploaded, or if a new document is being added to a customer.
     */
    int sign;

    /**
     * The ConnectionReceiver listener that listens to the connectivity of the application.
     */
    ConnectionReceiver connectionReceiver;


    /**
     * Initializes the activity, the widgets, the reference and the connectionReceiver, gets the information passed from the previous activity and sets them inside the appropriate variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        img = (ImageView) findViewById(R.id.img);

        r = FirebaseStorage.getInstance().getReference("customers");
        ref = FirebaseDatabase.getInstance().getReference("customers");
        imgRef = FirebaseDatabase.getInstance().getReference("images");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        upload = (Button) findViewById(R.id.upload);
        back = (Button) findViewById(R.id.back);

        Intent gt = getIntent();

        fname = gt.getStringExtra("fname");
        lname = gt.getStringExtra("lname");
        customerID = gt.getStringExtra("customerID");
        address = gt.getStringExtra("address");
        city = gt.getStringExtra("city");
        phone = gt.getStringExtra("phone");
        mobile = gt.getStringExtra("mobile");
        typeID = gt.getIntExtra("typeID", -1);

        sign = gt.getIntExtra("sign", 0);

        if (sign == 1)
        {
            upload.setText(getString(R.string.save));
            back.setText(getString(R.string.cancel));
            url = gt.getStringExtra("url");
            imgId = gt.getStringExtra("imgId");
            idFromIntent = gt.getStringExtra("id");

            Picasso.get().load(url).fit().centerInside().into(img);
        }
        else if (sign == 2)
            idFromIntent = gt.getStringExtra("id");

        connectionReceiver = new ConnectionReceiver();
    }


    /**
     * Registers the receiver when the application resumes the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(connectionReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Unregisters the receiver when the application stops the activity.
     */
    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(connectionReceiver);
    }

    /**
     * Captures an image with the camera.
     */
    public void capture(View view) {

        if (uploadTask != null)
            Toast.makeText(this, getString(R.string.image_uploaded), Toast.LENGTH_LONG).show();
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    uri = FileProvider.getUriForFile(this,
                            "com.example.liorkaramany.opticsdatabase.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }

    /**
     * Creates the file that contains the captured image.
     *
     * @return the file that contains the captured image.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Puts the image inside the ImageView once the image has been captured.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                image = getBitmapFromUri();
                image = imageOrientationValidator(image, mCurrentPhotoPath);
                img.setImageBitmap(image);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Obtains a Bitmap from the URI.
     *
     * @return the Bitmap from the URI.
     */
    public Bitmap getBitmapFromUri() {

        getContentResolver().notifyChange(uri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;

        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
            return bitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Rotates the image to the right orientation.
     *
     * @param bitmap the bitmap to be rotated.
     * @param path the path of the captured image.
     * @return the rotated Bitmap.
     */
    private Bitmap imageOrientationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Rotates the Bitmap by a certain angle.
     *
     * @param source the Bitmap to rotate.
     * @param angle the angle that the Bitmap will be rotated.
     * @return the new Bitmap.
     */
    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Uploads the image as a document to the database and creates a new customer, or replaces an existing document with the new one.
     */
    public void upload(View view) {
        if (uploadTask != null)
            Toast.makeText(this, getString(R.string.currently_uploading), Toast.LENGTH_LONG).show();
        else {
            if (image != null) {

                final String id;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] data = baos.toByteArray();

                StorageReference tmpRef;
                final String imgId;
                if (sign == 1) {
                    id = idFromIntent;
                    imgId = this.imgId;
                    tmpRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                }
                else if (sign == 2) {
                    id = idFromIntent;
                    imgId = ref.push().getKey();
                    tmpRef = r.child(id).child(imgId);
                }
                else
                {
                    id = ref.push().getKey();
                    imgId = ref.push().getKey();
                    tmpRef = r.child(id).child(imgId);
                }

                uploadTask = tmpRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = uri.toString();

                                if (sign == 1) {
                                    imgRef.child(id).child(imgId).setValue(new Image(imgId, url));
                                }
                                else {

                                    imgRef.child(id).push().getKey();
                                    if (sign == 0)
                                        ref.child(id).setValue(new Customer(id, fname, lname, customerID, address, city, phone, mobile, typeID));
                                }
                                imgRef.child(id).child(imgId).setValue(new Image(imgId, url));
                            }
                        });

                        /*Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        }, 500);*/

                        if (sign != 1) {
                            //Toast.makeText(Document.this, getString(R.string.customer_uploaded), Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else {
                            //Toast.makeText(Document.this, getString(R.string.customer_edited), Toast.LENGTH_SHORT).show();
                            Intent gt = getIntent();
                            setResult(RESULT_OK, gt);
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setProgress(0);
                        Toast.makeText(Document.this, getString(R.string.push_failed), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
            } else Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Goes back to the previous activity if a document isn't uploading.
     */
    public void back(View view) {
        finish();
    }

}
