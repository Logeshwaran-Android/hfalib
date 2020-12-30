package com.hoperlady.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.hoperlady.Pojo.DocumentUploadPojo;
import com.hoperlady.Pojo.RegisterLicenceUploadPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.adapter.RegisterLicenceUploadAdatpter;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.Volley.AppController;
import core.Volley.ServiceRequest;
import core.Volley.VolleyMultipartRequest;
import core.service.ServiceConstant;

public class RegisterLicenceUploadActivity extends AppCompatActivity {

    private ImageView backIV;
    private RecyclerView licenceUploadRV;
    private ArrayList<RegisterLicenceUploadPojo> licenceUploadArrayList;
    private Button continueBTN;

    private ConnectionDetector cd;
    public int PDF_REQ_CODE = 101;
    public int REQUEST_CODE_DOC = 4;

    private int CAMERA_REQUEST_2 = 1;
    private int PICK_IMAGE = 2;
    private File fCapturedImage;
    private String sImagePath = "";
    private Uri mImageCaptureUri;
    Uri selectedImage;
    Uri outputUri = null;
    Bitmap finalPic = null;
    String encode = "";
    private byte[] byteArray;
    private int mPosition = -1;
    private String Document_ID="";
    private String Docume_Name="";
    RegisterLicenceUploadAdatpter adatpter;
    File imageRoot;
    ArrayList<byte[]> finalByteArray = new ArrayList<byte[]>();
    ArrayList<String> extensionPathArray = new ArrayList<String>();
    static String fileLastCountOne = "No";
    private String fileUploadStr = "";

    private String myFileTypeStr = "image";

    private String response = "";

    int bytesRead, bytesAvailable, bufferSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_licence_upload);
        initGetIntent();
        initialization();
        initOnClick();
    }

    private void initGetIntent() {
        response = getIntent().getStringExtra("response");
    }

    private void initialization() {
        backIV = (ImageView) findViewById(R.id.backIV);
        licenceUploadRV = (RecyclerView) findViewById(R.id.licenceUploadRV);
        continueBTN = (Button) findViewById(R.id.continueBTN);
        licenceUploadRV.setHasFixedSize(true);
        licenceUploadRV.setLayoutManager(new LinearLayoutManager(this));
        cd = new ConnectionDetector(RegisterLicenceUploadActivity.this);

        String appDirectoryName = getString(R.string.app_name);
        imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);


        if (!imageRoot.exists()) {
            imageRoot.mkdir();
        } else if (!imageRoot.isDirectory()) {
            imageRoot.delete();
            imageRoot.mkdir();
        }
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        fCapturedImage = new File(imageRoot,
                name + ".jpg");


        if (cd.isConnectingToInternet()) {
            PostRequestGetTaskerDocument(ServiceConstant.GETTASKERDOCUMENT);
        } else {
            Toast.makeText(RegisterLicenceUploadActivity.this, R.string.action_no_internet_message, Toast.LENGTH_SHORT).show();
        }
    }


    private void UploadUserImage(String url, final byte[] byteImage) {
        final Dialog dialog1 = new Dialog(RegisterLicenceUploadActivity.this);
        dialog1.getWindow();
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.custom_loading);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        TextView dialog_title = (TextView) dialog1.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                System.out.println("UploadUserImage response" + response.data);
                String resultResponse = new String(response.data);
                System.out.println("UploadUserImage response" + resultResponse);
                String sStatus = "", sResponse = "", Surl = "", Smsg = "";
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    sStatus = jsonObject.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONArray responseObject = jsonObject.getJSONArray("documents");
                        if (responseObject.length() > 0) {
                            for (int i = 0; i < responseObject.length(); i++) {
                                JSONObject imageJArray = responseObject.getJSONObject(i);
                                RegisterLicenceUploadPojo pojo = new RegisterLicenceUploadPojo();
                                pojo.set_id(Document_ID);
                                pojo.setName(Docume_Name);
                                pojo.setFile_typle(imageJArray.getString("file_type"));
                                pojo.setImageWebUrl(imageJArray.getString("path"));
                                licenceUploadArrayList.set(mPosition, pojo);
                            }

                        }
                        adatpter.notifyDataSetChanged();
                        if (myFileTypeStr.endsWith("pdf")) {
                            Toast.makeText(RegisterLicenceUploadActivity.this, getResources().getString(R.string.upload_pdf_file), Toast.LENGTH_SHORT).show();

                        } else if (myFileTypeStr.endsWith("doc")) {
                            Toast.makeText(RegisterLicenceUploadActivity.this, getResources().getString(R.string.upload_doc_file), Toast.LENGTH_SHORT).show();
                        } else if (myFileTypeStr.equals("image")) {
                            Toast.makeText(RegisterLicenceUploadActivity.this, getResources().getString(R.string.edit_profile_success_label), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        if (jsonObject.has("response")) {
                            sResponse = jsonObject.getString("response");
                            Toast.makeText(RegisterLicenceUploadActivity.this, sResponse, Toast.LENGTH_SHORT).show();
                        }
                        Log.e("jsonObject", "jsonObject" + jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog1.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doc_id",Document_ID);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //  params.put("file[" + 0 + "]", new DataPart("text.jpg", byteImage));
                if (myFileTypeStr.equals("pdf")) {
                    params.put("file", new DataPart("text.pdf", byteImage));
                } else if (myFileTypeStr.equals("doc")) {
                    params.put("file", new DataPart("text.doc", byteImage));
                } else {
                    params.put("file", new DataPart("text.jpg", byteImage));
                }
                Log.e("file", "file" + params.toString());
                return params;
            }
        };
        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        multipartRequest.setRetryPolicy(retryPolicy);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        multipartRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(multipartRequest);
    }


    private void initOnClick() {

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog1 = new Dialog(RegisterLicenceUploadActivity.this);
                dialog1.getWindow();
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(R.layout.custom_loading);
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                TextView dialog_title = (TextView) dialog1.findViewById(R.id.custom_loading_textview);
                dialog_title.setText(getResources().getString(R.string.action_loading));

                boolean isSelected = true;

                for (int i = 0; i < licenceUploadArrayList.size(); i++) {

                    if(licenceUploadArrayList.get(i).getMandatory_status().equals("1")){

                        if (licenceUploadArrayList.get(i).getImageWebUrl().isEmpty()) {
                            Toast.makeText(RegisterLicenceUploadActivity.this, R.string.register_licence_upload_activity_please_fill_all_fields, Toast.LENGTH_SHORT).show();
                            isSelected = false;
                            break;
                        }
                    }

                    if(licenceUploadArrayList.get(i).getMandatory_status().equals("2")){

                        if (licenceUploadArrayList.get(i).getImageWebUrl().isEmpty()) {
                            Toast.makeText(RegisterLicenceUploadActivity.this, "Upload the required document", Toast.LENGTH_SHORT).show();
                            isSelected = false;
                            break;
                        }
                    }

                }

                if (isSelected) {

                    SessionManagerRegistration managerRegistration = new SessionManagerRegistration(RegisterLicenceUploadActivity.this);
                    DocumentUploadPojo pojo = new DocumentUploadPojo();
                    pojo.setPojoArrayList(licenceUploadArrayList);
                    managerRegistration.setDocumentDays(pojo);

                    Intent intent = new Intent(RegisterLicenceUploadActivity.this, RegisterPageFourth.class);
                    intent.putExtra("response", response);
                    startActivity(intent);
                    dialog1.dismiss();

                } else {
                    dialog1.dismiss();
                }
            }
        });

    }


    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(RegisterLicenceUploadActivity.this, "data: " + data, Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_2) {
                try {
                    sImagePath = fCapturedImage.getAbsolutePath();
                    mImageCaptureUri = Uri.fromFile(new File(sImagePath));
                    outputUri = mImageCaptureUri;
                    System.out.println("Image Captured Uri = " + mImageCaptureUri);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                    System.out.println("Image Captured Uri bitmap = " + bitmap.toString());

                    UCrop.Options options = new UCrop.Options();
                    options.setStatusBarColor(getResources().getColor(R.color.appmain_color));
                    options.setToolbarColor(getResources().getColor(R.color.appmain_color));
                    options.setMaxBitmapSize(800);

                    UCrop.of(mImageCaptureUri, outputUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(8000, 8000)
                            .withOptions(options)
                            .start(RegisterLicenceUploadActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == PICK_IMAGE) {

                if (data != null) {
                    selectedImage = data.getData();
                }

                try {
                    if (selectedImage != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        int wid = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        if (!imageRoot.exists()) {
                            imageRoot.mkdir();
                        } else if (!imageRoot.isDirectory()) {
                            imageRoot.delete();
                            imageRoot.mkdir();
                        }

                        final File image = new File(imageRoot, System.currentTimeMillis() + ".jpg");
                        outputUri = Uri.fromFile(image);

                        UCrop.Options options = new UCrop.Options();
                        options.setStatusBarColor(getResources().getColor(R.color.appmain_color));
                        options.setToolbarColor(getResources().getColor(R.color.appmain_color));
                        options.setMaxBitmapSize(800);

                        UCrop.of(selectedImage, outputUri)
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(8000, 8000)
                                .withOptions(options)
                                .start(RegisterLicenceUploadActivity.this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {

                final Uri resultUri = UCrop.getOutput(data);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    if (bitmap.getWidth() == 500 && bitmap.getHeight() == 500) {
                        finalPic = bitmap;
                    } else {

                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        float bitmapRatio = (float) width / (float) height;
                        if (bitmapRatio > 0) {
                            width = 500;
                            height = (int) (width / bitmapRatio);

                        } else {
                            height = 500;
                            width = (int) (height * bitmapRatio);
                        }

                        finalPic = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        encode = encodeToBase64(finalPic, Bitmap.CompressFormat.JPEG, 100);
                    }

                    if (finalPic == null) {
                        Log.d("Bitmap", "null");
                    } else {
                        Log.d("Bitmap", "not null");

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        finalPic.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();

                        Log.d("byteArray", "byteArray" + byteArray);

                        if (mPosition != -1) {
                            RegisterLicenceUploadPojo pojo = new RegisterLicenceUploadPojo();
                            pojo.setLicenceCount("");
                            pojo.setName(licenceUploadArrayList.get(mPosition).getName());
                            pojo.setFile_typle("");
                            pojo.setExpiryDate("");
                            licenceUploadArrayList.set(mPosition, pojo);
                        }
                        adatpter.notifyDataSetChanged();
                        myFileTypeStr = "image";

                        if (cd.isConnectingToInternet())
                            UploadUserImage(ServiceConstant.UPLOADTASKERDOCUMENT, byteArray);
                        else
                            Toast.makeText(RegisterLicenceUploadActivity.this, R.string.action_no_internet_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PDF_REQ_CODE && data != null) {
                Uri uri = data.getData();
                File file = null;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    file = new File(getApplicationContext().getFilesDir(), "myfile.pdf");
//                    long sizeInBytes = file.length();
////transform in MB
//                    long sizeInMb = sizeInBytes / (1024 * 1024);
//
//                    if(sizeInBytes>sizeInMb){
//                        Toast.makeText(getApplicationContext(),"less than 1 mb not allowed",Toast.LENGTH_LONG).show();
//                    }


                    OutputStream outStream = new FileOutputStream(file);
                    outStream.write(buffer);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (file != null && file.exists()) {

                    Log.d("", "onActivityResult exists1 : " + file.exists());
                    myFileTypeStr = "pdf";
                    long fileSize =file.length();

                    long sizemb = fileSize/1024;

                    if(sizemb>1024){
                        Toast.makeText(getApplicationContext(),"Please less than 1 mb file only upload",Toast.LENGTH_SHORT).show();
                    }
                else {


                        uploadFile(file);
                    }}

            } else if (requestCode == REQUEST_CODE_DOC && data != null) {
                Uri uri = data.getData();
                File file = null;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    file = new File(getApplicationContext().getFilesDir(), "myfile.doc");

//                    long sizeInBytes = file.length();
////transform in MB
//                    long sizeInMb = sizeInBytes / (1024 * 1024);
                    OutputStream outStream = new FileOutputStream(file);
                    outStream.write(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (file != null && file.exists()) {
                    Log.d("", "onActivityResult exists1 : " + file.exists());
                    myFileTypeStr = "doc";


                    long fileSize =file.length();

                    long sizemb = fileSize/1024;

                    if(sizemb>1024){
                        Toast.makeText(getApplicationContext(),"Please less than 1 mb file only upload",Toast.LENGTH_SHORT).show();
                    }

else {

                    uploadFile(file);

}
                }
            }
        }
    }

    private void uploadFile(File file) {
        byteArray = new byte[(int) file.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file.getAbsolutePath());
            fis.read(byteArray);
            fis.close();
            if (byteArray != null) {
                UploadUserImage(ServiceConstant.UPLOADTASKERDOCUMENT, byteArray);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void PostRequestGetTaskerDocument(String url) {
        continueBTN.setVisibility(View.GONE);
        HashMap<String, String> jsonParams = new HashMap<String, String>();

        ServiceRequest mRequest = new ServiceRequest(RegisterLicenceUploadActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.GET, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jaResponse = jsonObject.getJSONArray("documents");
                    if (jaResponse.length() > 0) {
                        licenceUploadArrayList = new ArrayList<RegisterLicenceUploadPojo>();
                        for (int i = 0; i < jaResponse.length(); i++) {
                            JSONObject joResponse = jaResponse.getJSONObject(i);
                            RegisterLicenceUploadPojo pojo = new RegisterLicenceUploadPojo();
                            pojo.set_id(joResponse.getString("_id"));
                            pojo.setLicenceCount("");
                            pojo.setName(joResponse.getString("name"));
                            pojo.setMandatory_status(joResponse.getString("mandatory"));
                            pojo.setString_status("");
                            pojo.setImageWebUrl("");
                            pojo.setExpiryDate("");
                            pojo.setFile_typle("");
                            licenceUploadArrayList.add(pojo);
                        }

                        if (licenceUploadArrayList.size() > 0)
                            continueBTN.setVisibility(View.VISIBLE);

                        adatpter = new RegisterLicenceUploadAdatpter(licenceUploadArrayList, RegisterLicenceUploadActivity.this, new RegisterLicenceUploadAdatpter.PickImages() {
                            @Override
                            public void Update(File captured_image, int position, String type) {
                                mPosition = position;
                                Document_ID=licenceUploadArrayList.get(position).get_id();
                                Docume_Name=licenceUploadArrayList.get(position).getName();
                                if (type.equalsIgnoreCase("camera")) {
                                    fCapturedImage = captured_image;
                                    Uri photoURI = FileProvider.getUriForFile(RegisterLicenceUploadActivity.this, getPackageName() + ".provider", fCapturedImage);
                                    Intent cIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    cIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    cIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                                    startActivityForResult(cIntent, CAMERA_REQUEST_2);
                                } else if (type.equalsIgnoreCase("pdf")) {
                                    Intent intent = new Intent();
                                    intent.setType("application/pdf");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);
                                } else if (type.equalsIgnoreCase("document")) {
                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("*/*");
                                    String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                    startActivityForResult(intent, REQUEST_CODE_DOC);

                                } else {
                                    Intent gIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(gIntent, PICK_IMAGE);
                                }
                            }

                            @Override
                            public void DisplayImageInPopUp(String URL) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterLicenceUploadActivity.this);
                                // Get the layout inflater
                                LayoutInflater inflater = RegisterLicenceUploadActivity.this.getLayoutInflater();
                                // Inflate the layout for the dialog
                                // Pass null as the parent view because its going in the dialog layout
                                View RequestPaymentView = inflater.inflate(R.layout.dialog_imageview, null);
                                ImageView image = (ImageView) RequestPaymentView.findViewById(R.id.View);
                                Picasso.with(RegisterLicenceUploadActivity.this).load(URL).error(R.drawable.pdf_icon).into(image);
                                builder.setView(RequestPaymentView);
                                builder.create();

                                builder.setPositiveButton("Close", null);
                                builder.setView(RequestPaymentView);

                                final AlertDialog alertDialog = builder.show();
                                alertDialog.show();
                            }
                        });
                        licenceUploadRV.setAdapter(adatpter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onErrorListener() {

            }
        });
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("getRealPathFromURI", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
