package businesscamera.libtiff.businesscam;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    //////////////////////////////////////////////////

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA= 100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static String[] PERMISSIONS_CAM = {
            Manifest.permission.CAMERA
    };
    //////////////////////////////////////////////////

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        frameLayout=(FrameLayout)findViewById(R.id.frameLayout);
        camera = Camera.open();
        showCamera=new ShowCamera(this,camera);
        frameLayout.addView(showCamera);



    }




    public static void verifyStoragePermissions(Activity activity)
    {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int campermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (campermission!=PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,PERMISSIONS_CAM, REQUEST_CAMERA);
        }
        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data,Camera camera)
        {

            File picture_file = getOutputMediaFile();

            if(picture_file == null)
            {
                return;
            }
            else
            {
                try {
                    FileOutputStream fos = new FileOutputStream((picture_file));
                    fos.write(data);
                    fos.close();
                    camera.startPreview();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    private void galleryAddPic(String file) {

            File f = new File(file);
            Uri contentUri = Uri.fromFile(f);
            Intent mediaScanIntent = new Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
            sendBroadcast(mediaScanIntent);
    }

    private File getOutputMediaFile()
    {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED))
        {
            return null;
        }
        else
        {
            File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "GUI");
            if (!folder_gui.exists())
            {
                folder_gui.mkdirs();
            }

            SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
            String dtnameformat = s.format(new Date()); //We use this to produce different name in order to avoid same name and image override

            File outputFile=new File(folder_gui,"test");
            System.out.print(outputFile);
            galleryAddPic(folder_gui.toString());
            return outputFile;
        }
    }

    public void captureImage(View v)
    {
        if (camera!=null)
        {
            camera.takePicture(null,null,mPictureCallback);
        }
    }
}
