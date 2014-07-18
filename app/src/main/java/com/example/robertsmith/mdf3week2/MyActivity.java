package com.example.robertsmith.mdf3week2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;


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
            mBitmap = (Bitmap) data.getExtras().get("data");
            mPic.setImageBitmap(mBitmap);
        }
    }

    private void Save(Bitmap image)
    {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ mFolderName;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(cal.getTime());
        File dir = new File(file_path);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        File file = new File(dir, mFileName + formattedDate + ".jpg");
        Log.e("FILE", "" + file.toString());

        try
        {
            FileOutputStream fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener(){

                @Override
                public void onScanCompleted(String s, Uri uri)
                {
                    //Toast.makeText(mContext, "Picture Saved", Toast.LENGTH_LONG).show();
                    Log.e("SAVED AS", "" + s);
                    mPic.setImageResource(0);
                }
            });

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
