package com.example.samscots.sosoffine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class StartActivity extends AppCompatActivity {

    private TextInputLayout til;
    private Button set_up;
    private String display_name="";
    private CircleImageView update_image;
    private static final int Gallery_Pick=10111;
    private Bitmap bitmap;
    public Bitmap bitmap2;
    private PermissionManager permissionManager;
    String ImageDecode;
    private static final int CAMERA_REQUEST = 357;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        update_image=(CircleImageView) findViewById(R.id.update_image);

        til=(TextInputLayout) findViewById(R.id.textInputLayout);
        set_up=(Button)findViewById(R.id.setup);

        //Permission
        permissionManager=new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);
        //Permission


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String DisplayName = preferences.getString("DisplayName", "");
        String getimg=preferences.getString("RealPath","");
        if(!DisplayName.equals(""))
            til.getEditText().setText(DisplayName);
        if(!getimg.equals("")){
            Uri uriFromPath = Uri.fromFile(new File(getimg));
            update_image.setImageURI(uriFromPath);
        }


        set_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display_name=til.getEditText().getText().toString();
                if(!display_name.equals("")){
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("DisplayName",display_name);
                    editor.apply();

                    Intent go_main=new Intent(StartActivity.this,MainActivity.class);
                    startActivity(go_main);
                    finish();
                    setDeviceName(display_name);

                }else
                    Toast.makeText(StartActivity.this, "Display Name is Empty", Toast.LENGTH_SHORT).show();

            }
        });

        update_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence option[] = new CharSequence[] {"Camera", "Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("Choose Option");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                        if(which==1){

                            Intent Gallery_Intent=new Intent();
                            Gallery_Intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(Gallery_Intent,"Select Image"),Gallery_Pick);

                        }
                    }
                });
                builder.show();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == Activity.RESULT_OK && data!=null) {

            Uri URI = data.getData();
            String[] FILE = { MediaStore.Images.Media.DATA };


            Cursor cursor = getContentResolver().query(URI,
                    FILE, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(FILE[0]);
            ImageDecode = cursor.getString(columnIndex);
            cursor.close();
            Log.d("StartActivity ",ImageDecode);

           // update_image.setImageBitmap(BitmapFactory
             //       .decodeFile(ImageDecode));

                try {

                    File compressedImageFile = new Compressor(this)
                            .setMaxWidth(300)
                            .setMaxHeight(300)
                            .setQuality(40)
                            .compressToFile(new File(ImageDecode));

                    FileInputStream fis = new FileInputStream(compressedImageFile);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/MyDp");
                    boolean success = true;
                    if (!folder.exists())
                        success = folder.mkdirs();
                    if (success) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/MyDp/.nomedia");
                        if(!file.exists()) {
                            FileOutputStream fos2 = new FileOutputStream(Environment.getExternalStorageDirectory() + "/SOSOffline/MyDp/.nomedia");
                        }
                        String temp = Environment.getExternalStorageDirectory() + "/SOSOffline/MyDp/" + System.currentTimeMillis() + ".jpg";
                        FileOutputStream fos = new FileOutputStream(temp);
                        fos.write(buffer);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("RealPath", temp);
                        editor.apply();
                        Log.d("The Real Path ", temp);
                        Picasso.with(StartActivity.this).load(compressedImageFile).into(update_image);
                    }
                }catch (FileNotFoundException e){
                    Toast.makeText(this, "File Not Found ", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Toast.makeText(this, "Error "+e, Toast.LENGTH_SHORT).show();
                }




        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            File folder = new File(Environment.getExternalStorageDirectory() + "/SOSOffline");
            boolean success = true;
            if (!folder.exists())
                success = folder.mkdirs();
            if(success) {
                String f_name = System.currentTimeMillis() + ".jpg";
                File f = new File(Environment.getExternalStorageDirectory() + "/SOSOffline/MyDp/", f_name);
                String uri_pos = Environment.getExternalStorageDirectory() + "/SOSOffline/MyDp/" + f_name;

                //Convert bitmap to byte array
                OutputStream os;
                try {
                    os = new FileOutputStream(f);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();

                    File compressedImageFile = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(40)
                            .compressToFile(f);
                    FileInputStream fis = new FileInputStream(compressedImageFile);
                    final byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("RealPath", uri_pos);
                    editor.apply();
                    Log.d("The Real Path ", uri_pos);
                    Picasso.with(StartActivity.this).load(compressedImageFile).into(update_image);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void setDeviceName(String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = WifiP2pManager.Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = WifiP2pManager.ActionListener.class;
            Method setDeviceName = MainActivity.mManager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = MainActivity.mChannel;
            arglist[1] = devName;
            arglist[2] = new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("StartActivity","setDeviceName succeeded");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("StartActivity","setDeviceName failed");
                }
            };

            setDeviceName.invoke(MainActivity.mManager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    //Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions,grantResults);
    }
    //Permission


}