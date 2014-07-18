package com.example.robertsmith.mdf3week2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//Robert Smith
//MDF3 Term 1407
//Take n Share
//This will be a Single Activity application that will serve as a Camera app
//That will take a picture and allow the user to save it to the devices gallery
//and send them a notification when the save occurs

public class MyActivity extends Activity {

    private static final int CODE = 1;
    private ImageView mPic;
    private Button mTakePic;
    private Button mSavePic;
    private Bitmap mBitmap;
    public String mFileName = "pic";
    public String mFolderName = "/Take'n'Save";
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mPic = (ImageView)findViewById(R.id.picture);
        mPic.setImageResource(R.drawable.placeholder);

        mTakePic = (Button)findViewById(R.id.takePic);
        mSavePic = (Button)findViewById(R.id.savePic);

        mTakePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Intent to start the devices camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CODE);
            }
        });

        mSavePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Save(mBitmap);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CODE && resultCode == RESULT_OK)
        {
            //get bitmap from image so it can be saved
            mBitmap = (Bitmap) data.getExtras().get("data");
            mPic.setImageBitmap(mBitmap);
        }
    }

    private void Save(Bitmap image)
    {
        //get file path from device
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ mFolderName;
        //calender is needed so every pic will have a unique name
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(cal.getTime());
        //see if directory exist
        File dir = new File(file_path);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        //make unique file name for image
        File file = new File(dir, mFileName + formattedDate + ".jpg");
        Log.e("FILE", "" + file.toString());

        //save the file
        try
        {
            FileOutputStream fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            //scanFile tells the device to search for new images-without this the image will not appear in gallery unless the phone resets
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener(){

                @Override
                public void onScanCompleted(String s, Uri uri)
                {
                    //Toast.makeText(mContext, "Picture Saved", Toast.LENGTH_LONG).show();
                    Log.e("SAVED AS", "" + s);
                }
            });
            //reset image
            mPic.setImageResource(R.drawable.placeholder);

            //fire the notification
            generateLocalNotification();

        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(mContext, "Picture cannot be saved", Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            Toast.makeText(mContext, "Picture cannot be saved", Toast.LENGTH_LONG).show();
        }
    }

    private void generateLocalNotification()
    {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification();

        Notification.Builder builder = new Notification.Builder(this)

                .setSmallIcon(R.drawable.photo)
                .setContentTitle("New Photo")
                .setContentText("A new photo is in your gallery");

        Intent intent = new Intent(Intent.ACTION_PICK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        notification = builder.build();

        manager.notify(0, notification);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}