package edu.uncc.project10;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.callback.Callback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements IFragmentListener {
    RetrofitInterface retrofitInterface;
    Retrofit retrofit;

    private MaterialButton cameraBtn;
    private MaterialButton galleryBtn;
    private ImageView imageIv;
    private MaterialButton scanBtn;
    private TextView resultTv;

    private static final int CAMERA_REQUST_CODE = 100;
    private static final int STORAGE_REQUES_CODE = 101;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    //Uri of image from Camera/Gallery
    private Uri imageUri = null;

    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barCodeScanner;

    private static final String TAG = "scorer";

//    private Auth0 auth0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*auth0 = new  Auth0(
                "WFoVbW6kKIZBTpFIi2ftGMUXQD1JuRTV",
                "dev-264uwjwu6c8xz02x.us.auth0.com"
        );

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });*/

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerview, new LoginFragment(), "LoginFragment")
                .addToBackStack(null)
                .commit();

        /*retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);


        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        imageIv = findViewById(R.id.imageIv);
        scanBtn = findViewById(R.id.scanBtn);
        resultTv = findViewById(R.id.resultTv);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        barCodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission()) {
                    pickImageCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermissioin();
                if (checkStoragePermission()) {
                    pickImageGallery();
                } else {
                    requestStoragePermissioin();
                }
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri == null) {
                    Toast.makeText(MainActivity.this, "Pickage image first...", Toast.LENGTH_SHORT).show();
                } else {
                    detectResultFromImage();
                }
            }
        });*/
    }

    @Override
    public void gotoGroupFragment(String email, String fName, String scoredGroupName) {
       /* ConstraintLayout mainLayout = findViewById(R.id.containerview);
        mainLayout.setVisibility(View.GONE);*/
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerview, GroupFragment.newInstance(email, fName, scoredGroupName), "GroupFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoQuestionnaire(String email, String fName, String studentGroupName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerview, QuestionnaireFragment.newInstance(email, fName, studentGroupName), "QuestionnaireFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerview, new LoginFragment(), "LoginFragment")
                .addToBackStack(null)
                .commit();
    }

    /*private void detectResultFromImage() {
        try {
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);
            Task<List<Barcode>> barcodeResult = barCodeScanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            extractBarCodeQRCodeInfo(barcodes);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed scanning due to " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void extractBarCodeQRCodeInfo(List<Barcode> barcodes) {
        for(Barcode barcode : barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();
            Log.d(TAG, "extractBarCodeQRCodeInfo: rawValue: " + rawValue);

            int valueType = barcode.getValueType();

            switch (valueType) {
                case Barcode.TYPE_WIFI: {
                    Barcode.WiFi typeWifi = barcode.getWifi();
                    String ssid = "" + typeWifi.getSsid();
                    String password = "" + typeWifi.getPassword();
                    String encryptionType = "" + typeWifi.getEncryptionType();

                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_WIFI: ");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: ssid: " + ssid);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: password: " + password);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: encryptionType: " + encryptionType);

                    resultTv.setText("TYPE: TYPE_WIFI \nssid: " + ssid + "\npassword: " + password + "\nencryptionType: " + encryptionType +"\nraw value: " + rawValue);
                }
                break;
                case Barcode.TYPE_URL: {
                    Barcode.UrlBookmark typeUrl = barcode.getUrl();
                    String title = "" +typeUrl.getTitle();
                    String url = "" + typeUrl.getUrl();

                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_URL");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: title: " + title);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: url: " + url);

                    resultTv.setText("TYPE: TYPE_URL\ntitle: " + title + "\nurl: "+ url + "\nraw value: " + rawValue);
                 }
                 break;
                case Barcode.TYPE_EMAIL: {
                    Barcode.Email typeEmail = barcode.getEmail();
                    String email = "" + typeEmail.getAddress();
                    String body = "" + typeEmail.getBody();
                    String subject = "" + typeEmail.getSubject();

                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_EMAIL");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: address: " + email);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: body: " + body);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: subject: " + subject);

                    String data[] = body.split("\n");
                    resultTv.setText("TYPE: TYPE_EMAIL \naddress: " + email + "\nbody: " + body + "\nsubject: " + subject + "\nraw value: " + rawValue);


                    login(email, data[0], "", "", "");
                }
                break;
                case Barcode.TYPE_CONTACT_INFO: {
                    Barcode.ContactInfo typeContact = barcode.getContactInfo();

                    String title = "" + typeContact.getTitle();
                    String organizer = "" + typeContact.getOrganization();
                    String name = "" + typeContact.getName().getFirst() + " " + typeContact.getName().getLast();
                    String phone = "" + typeContact.getPhones().get(0).getType();

                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_CONTACT_INFO");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: title: " + title);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: organizer: " + organizer);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: name: " + name);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: phone: " + phone);

                    resultTv.setText("TYPE: TYPE_CONACT_INFO \ntitle: " + title + "\norganizer: " + organizer + "\nname:" + name + "\nphones: " + phone + "\nraw value: " + rawValue);

                }
                break;
                default: {
                    resultTv.setText("raw value: " + rawValue);
                }
            }
        }
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        galleryActivityResultLanucher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();

                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);

                        imageIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(MainActivity.this, "Cancelle", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                        imageIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(MainActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();;
                    }

                }
            }
    );

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        return result;

    }

    private void requestStoragePermissioin() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUES_CODE);
    }

    private boolean checkCameraPermission() {

        boolean resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        boolean resultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

       // return resultCamera && resultStorage;
        return true;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case CAMERA_REQUST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUES_CODE: {
                if(grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is required...", Toast.LENGTH_SHORT).show();;
                    }
                }
            }
            break;
        }
    }*/

    private void login(String email, String fName, String lName, String userId, String password) {
        /*Auth0 account = new Auth0("bSPhMzpoeHixOqvV0nwZ5BCs36nqKGdk", "dev-264uwjwu6c8xz02x.us.auth0.com");

        com.auth0.android.callback.Callback<Credentials, AuthenticationException> callback = new Callback<Credentials, AuthenticationException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                Log.d(TAG, "onSuccess: auth0 login");
            }

            @Override
            public void onFailure(@NonNull AuthenticationException e) {
                Log.d(TAG, "onFailure: auth0 login");
            }
        };

        WebAuthProvider.login(account)
                .start(this, callback);*/

        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);

        Call<LoginResult> call = retrofitInterface.login(data);
        call.enqueue(new retrofit2.Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.code() == 200) {
                    LoginResult result = response.body();
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                    gotoGroupFragment(email, fName, null);
                } else {
                    Toast.makeText(MainActivity.this, response.code() + " Something went wrong ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
//    private void login() {
//        Log.d("00", "url:" + String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)));
//        WebAuthProvider.login(auth0)
//                .withScheme("demo")
//                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
//                /*.withScheme("demo")
//                .withScope("openid profile email")*/
//                .start(this, new Callback<Credentials, AuthenticationException>() {
//
//                    @Override
//                    public void onFailure(@NonNull final AuthenticationException exception) {
//                        Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess(@Nullable final Credentials credentials) {
//                       /* Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.getAccessToken());
//                        startActivity(intent);
//                        finish();*/
//                        String token = credentials.getAccessToken();
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.containerview, new ScreenOne(), "ScreenOne")
//                                .addToBackStack(null)
//                                .commit();
//                    }
//                });
//    }
}