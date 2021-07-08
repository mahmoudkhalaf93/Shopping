package com.example.shopping;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static final String TAG = "MainActivity1";
    Uri imageUri;
    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://horizon.salemsaber.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api apisss= retrofit.create(api.class);
        ImageView imageView=findViewById(R.id.imageView);
        MaterialButton getphoto=findViewById(R.id.getphoto);
        MaterialButton uploadphoto=findViewById(R.id.uploadphoto);

        intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                       imageUri=  data.getData();
                        imageView.setImageURI(data.getData());
                        Toast.makeText(this, FileUtil.getPath(imageUri,MainActivity.this), Toast.LENGTH_LONG).show();
                    }
                });

        getphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intentActivityResultLauncher.launch(intent);
            }
        });

        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if (imageUri==null)
    return;
                File file= new File(FileUtil.getPath(imageUri,MainActivity.this));
                RequestBody requestFile= RequestBody.create(MediaType.parse("multipart/form-data"),file);
                MultipartBody.Part body=MultipartBody.Part.createFormData("image",file.getName(),requestFile);
                RequestBody orderid=RequestBody.create(MediaType.parse("multipart/form-data"),"6");
                RequestBody carId=RequestBody.create(MediaType.parse("multipart/form-data"),"1");
                RequestBody price=RequestBody.create(MediaType.parse("multipart/form-data"),"200");
                RequestBody note=RequestBody.create(MediaType.parse("multipart/form-data"),"mahmoud_khalaf");
                apisss.uploadphoto(body,orderid,carId,price,note)
                        .enqueue(new Callback<ResponsTest>() {
                    @Override
                    public void onResponse(Call<ResponsTest> call, Response<ResponsTest> response) {
                        if(response.isSuccessful()&&response.body()!=null){
                            Toast.makeText(MainActivity.this, response.body().getStatus() +" /// "+response.body().getMessage() , Toast.LENGTH_LONG).show();
                            Log.i(TAG, "onResponse: "+response.body().getStatus() +" /// "+response.body().getMessage());
                        }
                        else
                            Toast.makeText(MainActivity.this, response.raw().toString(), Toast.LENGTH_LONG).show();
                            Log.i(TAG, "onFailure:  "+response.raw().toString());
                    }

                    @Override
                    public void onFailure(Call<ResponsTest> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onFailure: ",t);
                    }
                });
            }
        });





    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}