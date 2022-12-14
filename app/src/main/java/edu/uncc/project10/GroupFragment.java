package edu.uncc.project10;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {

    private static final String EMAIL_PARAM = "email";
    private static final String FIRST_NAME_PARAM = "fName";
    private static final String SCORED_GROUP_NAME_PARAM = "scoredGroupName";

    String email, fName, scoredGroupName;

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


    IFragmentListener mListener;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance(String email, String fName, String scoredGroupName) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL_PARAM, email);
        args.putString(FIRST_NAME_PARAM, fName);
        args.putString(SCORED_GROUP_NAME_PARAM, scoredGroupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IFragmentListener) {
            mListener = (IFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL_PARAM);
            fName = getArguments().getString(FIRST_NAME_PARAM);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(Globals.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);




        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        barCodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Student Groups");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        TextView txtViewWelcome = view.findViewById(R.id.textViewWelcome);
        txtViewWelcome.setText("Welcome " + fName);

        cameraBtn = view.findViewById(R.id.groupCameraBtn);
        galleryBtn = view.findViewById(R.id.groupGalleryBtn);
        imageIv = view.findViewById(R.id.groupImageIv);
        scanBtn = view.findViewById(R.id.groupScanBtn);
        resultTv = view.findViewById(R.id.groupResultTv);

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
                    Toast.makeText(getActivity(), "Pickage image first...", Toast.LENGTH_SHORT).show();
                } else {
                    detectResultFromImage();
                }
            }
        });

        /*ViewGroup rgp = (RadioGroup) view.findViewById(R.id.radioGroup);
        int buttons = 5;
        for (int i = 0; i < buttons ; i++) {
            RadioButton rbn = new RadioButton(getActivity());
            int id = View.generateViewId();
            rbn.setId(View.generateViewId());
            rbn.setText("RadioButton " + i);
            rgp.addView(rbn);

            rbn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("scorer", rbn.getText().toString());
                    mListener.gotoQuestionnaire(email, fName, rbn.getText().toString());
                }
            });
        }*/

        return view;
    }

    private void detectResultFromImage() {
        try {
            InputImage inputImage = InputImage.fromFilePath(getContext(), imageUri);
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
                            Toast.makeText(getActivity(), "Failed scanning due to " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                case Barcode.TYPE_TEXT: {
                    String text = barcode.getDisplayValue();
                    resultTv.setText("TYPE: TYPE_TEXT \ntext: " +text + "\raw value: " + rawValue);

                    mListener.gotoQuestionnaire(email, fName, text);
                }
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

                        detectResultFromImage();
                        //mListener.gotoGroupFragment("", "", "");
                    } else {
                        Toast.makeText(getActivity(), "Cancelle", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");

        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

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
                        Toast.makeText(getActivity(), "Cancelled...", Toast.LENGTH_SHORT).show();;
                    }

                }
            }
    );

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        return result;

    }

    private void requestStoragePermissioin() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUES_CODE);
    }

    private boolean checkCameraPermission() {

        boolean resultCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        boolean resultStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        // return resultCamera && resultStorage;
        return true;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUST_CODE);
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
                        Toast.makeText(getActivity(), "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Storage permission is required...", Toast.LENGTH_SHORT).show();;
                    }
                }
            }
            break;
        }
    }
}