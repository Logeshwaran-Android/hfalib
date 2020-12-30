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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hoperlady.Pojo.AvailableDocumentPojo;
import com.hoperlady.Pojo.DocumentPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.DocumentAdapter;
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
import core.Dialog.PkDialog;
import core.Volley.AppController;
import core.Volley.ServiceRequest;
import core.Volley.VolleyMultipartRequest;
import core.service.ServiceConstant;


public class GetDocuments extends AppCompatActivity {
    private ImageView backIV;
    private RecyclerView licenceUploadRV;
    private ArrayList<DocumentPojo> licenceUploadArrayList = new ArrayList<>();
    private ArrayList<AvailableDocumentPojo> available_document_list = new ArrayList<>();
    private Button continueBTN;
    private SessionManager session;
    private ConnectionDetector cd;
    public int PDF_REQ_CODE = 101;
    public int REQUEST_CODE_DOC = 4;
    public String provider_id = "";
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
    private String Document_ID = "";
    private String Document_Name = "";
    private String Document_file_type = "";
    private String Document_path = "";
    DocumentAdapter adatpter;
    File imageRoot;
    ArrayList<byte[]> finalByteArray = new ArrayList<byte[]>();
    ArrayList<String> extensionPathArray = new ArrayList<String>();
    static String fileLastCountOne = "No";
    private String fileUploadStr = "";
    private String myFileTypeStr = "image";
    private String response = "";

    private boolean isChangedImage = false;

    private boolean is_image_new_add = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
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
        continueBTN = (Button) findViewById(R.id.UpdateBTN);
        licenceUploadRV.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        licenceUploadRV.setLayoutManager(llm);
        cd = new ConnectionDetector(GetDocuments.this);

        session = new SessionManager(GetDocuments.this);
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);

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


        //   UploadUserImage(ServiceConstant.GETTASKERDOCUMENT);

        if (cd.isConnectingToInternet()) {
            PostRequestGetTaskerDocument(ServiceConstant.PROFILEINFO_URL);
        }
    }

    private void UploadUserImage(String url, final byte[] byteImage) {
        final Dialog dialog1 = new Dialog(GetDocuments.this);
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
                    Smsg = jsonObject.getString("message");
                    if (sStatus.equalsIgnoreCase("1")) {
                        isChangedImage = true;

                        JSONObject docu_object = jsonObject.getJSONObject("documents");

                        if (docu_object.length() > 0) {

                            DocumentPojo pojo = new DocumentPojo();
                            pojo.set_id(docu_object.getString("id"));
                            pojo.setName(docu_object.getString("name"));
                            pojo.setByteImage(licenceUploadArrayList.get(mPosition).getByteImage());
                            pojo.setFinalPic(licenceUploadArrayList.get(mPosition).getFinalPic());
                            pojo.setFile_typle(docu_object.getString("file_type"));
                            pojo.setImageWebUrl(docu_object.getString("path"));
                            licenceUploadArrayList.set(mPosition, pojo);

                        }
                        adatpter.notifyDataSetChanged();

                        if (myFileTypeStr.endsWith("pdf")) {
                            Toast.makeText(GetDocuments.this, Smsg, Toast.LENGTH_SHORT).show();
                        } else if (myFileTypeStr.endsWith("doc")) {
                            Toast.makeText(GetDocuments.this, Smsg, Toast.LENGTH_SHORT).show();
                        } else if (myFileTypeStr.equals("image")) {
                            Toast.makeText(GetDocuments.this, Smsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (jsonObject.has("response")) {
                            sResponse = jsonObject.getString("response");
                            Toast.makeText(GetDocuments.this, sResponse, Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> user = session.getUserDetails();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("type", "tasker");
                headers.put("user", provider_id);
                headers.put("device", user.get(SessionManager.KEY_GCM_ID));
                headers.put("devicetype", "android");
                return headers;
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doc_id", Document_ID);
                params.put("name", Document_Name);
                params.put("file_type", Document_file_type);
                params.put("provider_id", provider_id);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //params.put("file[" + 0 + "]", new DataPart("text.jpg", byteImage));
                if (myFileTypeStr.equals("pdf")) {
                    params.put("file", new DataPart("text.pdf", byteImage));
                } else if (myFileTypeStr.equals("doc")) {
                    params.put("file", new DataPart("text.doc", byteImage));
                } else {
                    params.put("file", new DataPart("text.jpg", byteImage));
                }
                Log.e("file", "file" + params.toString());
                //Log.e("file", "file" + params.toString());
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
                boolean isSelected = true;
                if (licenceUploadArrayList != null && licenceUploadArrayList.size() > 0) {
                    for (int i = 0; i < licenceUploadArrayList.size(); i++) {
                        if (licenceUploadArrayList.get(i).getImageWebUrl().isEmpty()) {
                            Toast.makeText(GetDocuments.this, R.string.register_licence_upload_activity_please_fill_all_fields, Toast.LENGTH_SHORT).show();
                            isSelected = false;
                            break;
                        }
                    }
                }
                if (isSelected) {
                    if (cd.isConnectingToInternet()) {
                        PostRequestUploadDocument();
                    } else {
                        Toast.makeText(GetDocuments.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
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
                            .start(GetDocuments.this);

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
                                .start(GetDocuments.this);
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

                            DocumentPojo pojo = new DocumentPojo();
                            pojo.setLicenceCount("");
                            pojo.setName(licenceUploadArrayList.get(mPosition).getName());
                            pojo.setFile_typle("");
                            pojo.setExpiryDate("");
                            pojo.setByteImage(byteArray);
                            pojo.setFinalPic(finalPic);
                            licenceUploadArrayList.set(mPosition, pojo);


                        }
                        adatpter.notifyDataSetChanged();
//                        Img_profileimg.setImageBitmap(finalPic);
                        myFileTypeStr = "image";

                        if (is_image_new_add) {

                            AddNewImageRequest(ServiceConstant.UPDATE_TASKER_DOCUMENT, byteArray);

                        } else {

                            UploadUserImage(ServiceConstant.UPDATE_TASKER_DOCUMENT, byteArray);
                        }


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
                    else{
                    uploadFile(file);
                }}
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

                if (is_image_new_add) {

                    AddNewImageRequest(ServiceConstant.UPDATE_TASKER_DOCUMENT, byteArray);

                } else {

                    UploadUserImage(ServiceConstant.UPDATE_TASKER_DOCUMENT, byteArray);
                }


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
        jsonParams.put("provider_id", provider_id);

        ServiceRequest mRequest = new ServiceRequest(GetDocuments.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("status")) {
                        if (jsonObject.getString("status").equals("3") || jsonObject.getString("status").equals("1") || jsonObject.getString("status").equals("2")) {
                            JSONObject MyResponse = jsonObject.getJSONObject("response");

                            JSONArray jaResponse = MyResponse.getJSONArray("documents");
                            if (jaResponse.length() > 0) {
                                licenceUploadArrayList = new ArrayList<DocumentPojo>();
                                for (int i = 0; i < jaResponse.length(); i++) {
                                    JSONObject joResponse = jaResponse.getJSONObject(i);
                                    DocumentPojo pojo = new DocumentPojo();
                                    pojo.set_id(joResponse.getString("id"));
                                    pojo.setLicenceCount("");
                                    pojo.setName(joResponse.getString("name"));
                                    pojo.setString_status("");
                                    pojo.setImageWebUrl(joResponse.getString("path"));
                                    Log.e("Img", ServiceConstant.Review_image + joResponse.getString("path"));
                                    pojo.setExpiryDate("");
                                    pojo.setFile_typle(joResponse.getString("file_type"));
                                    pojo.setDocumentPath(joResponse.getString("path"));
                                    licenceUploadArrayList.add(pojo);
                                }


                            } else {

                                //Toast.makeText(GetDocuments.this, "No document available!", Toast.LENGTH_SHORT).show();

                            }

                            JSONArray available_document = MyResponse.getJSONArray("available_documents");

                            if (available_document.length() > 0) {

                                for (int i = 0; i < available_document.length(); i++) {

                                    JSONObject available_doc_object = available_document.getJSONObject(i);
                                    AvailableDocumentPojo pojo = new AvailableDocumentPojo();
                                    pojo.setDocId(available_doc_object.getString("_id"));
                                    pojo.setDocName(available_doc_object.getString("name"));
                                    pojo.setDocMandatory(available_doc_object.getString("mandatory"));

                                    available_document_list.add(pojo);
                                }

                            }

                            DocumentMatching();

                        } else if (jsonObject.getString("status").equals("0")) {
                            Toast.makeText(GetDocuments.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(GetDocuments.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
//

            }

            @Override
            public void onErrorListener() {

            }
        });
    }


    private void DocumentMatching() {

        try {

            if (available_document_list.size() > 0) {

                if (licenceUploadArrayList.size() == 0) {

                    for (int i = 0; i < available_document_list.size(); i++) {

                        DocumentPojo pojo = new DocumentPojo();
                        pojo.set_id(available_document_list.get(i).getDocID());
                        pojo.setLicenceCount("");
                        pojo.setName(available_document_list.get(i).getDocName());
                        pojo.setString_status("");
                        pojo.setImageWebUrl("");
                        pojo.setExpiryDate("");
                        pojo.setFile_typle("");
                        pojo.setDocumentPath("");
                        licenceUploadArrayList.add(pojo);
                    }

                } else {

                    for (int j = 0; j < available_document_list.size(); j++) {

                        boolean available_id_status=false;

                        for (int k = 0; k < licenceUploadArrayList.size(); k++) {

                            String doc_id = licenceUploadArrayList.get(k).get_id();

                            if (doc_id.equals(available_document_list.get(j).getDocID())) {

                                available_id_status=true;

                            }
                        }

                        if(!available_id_status){

                            DocumentPojo pojo = new DocumentPojo();
                            pojo.set_id(available_document_list.get(j).getDocID());
                            pojo.setLicenceCount("");
                            pojo.setName(available_document_list.get(j).getDocName());
                            pojo.setString_status("");
                            pojo.setImageWebUrl("");
                            pojo.setExpiryDate("");
                            pojo.setFile_typle("");
                            pojo.setDocumentPath("");
                            licenceUploadArrayList.add(pojo);
                        }

                    }
                }

            }

            AdapterSet();

        } catch (Exception e) {

            Log.e("Error", e.toString());
        }


    }

    private void AdapterSet() {

        try {

            adatpter = new DocumentAdapter(licenceUploadArrayList, GetDocuments.this, new DocumentAdapter.PickImages() {
                @Override
                public void Update(File captured_image, int position, String type) {
                    is_image_new_add = false;
                    mPosition = position;
                    Document_ID = licenceUploadArrayList.get(position).get_id();
                    Document_Name = licenceUploadArrayList.get(position).getName();
                    Document_file_type = licenceUploadArrayList.get(position).getFile_typle();
                    Document_path = licenceUploadArrayList.get(position).getDocumentPath();
                    if (type.equalsIgnoreCase("camera")) {
                        fCapturedImage = captured_image;
                        Uri photoURI = FileProvider.getUriForFile(GetDocuments.this, getPackageName() + ".provider", fCapturedImage);
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

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GetDocuments.this);
                    // Get the layout inflater
                    LayoutInflater inflater = GetDocuments.this.getLayoutInflater();
                    // Inflate the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    View RequestPaymentView = inflater.inflate(R.layout.dialog_imageview, null);
                    ImageView image = (ImageView) RequestPaymentView.findViewById(R.id.View);
                    Picasso.with(GetDocuments.this).load(URL).error(R.drawable.pdf_icon).into(image);
                    // Picasso.with(GetDocuments.this).load(ServiceConstant.Review_image + aDataItem.get(position).getImageWebUrl()).error(R.drawable.pdf_icon).into(image);
                    builder.setView(RequestPaymentView);
                    builder.create();

                    builder.setPositiveButton("Close", null);
                    builder.setView(RequestPaymentView);

                    final AlertDialog alertDialog = builder.show();
                    alertDialog.show();
                }
            }, new DocumentAdapter.DocumentRemove() {


                @Override
                public void Remove(int position) {

                    mPosition = position;
                    DeleteDocument(licenceUploadArrayList.get(position).get_id());

                }
            }, new DocumentAdapter.AddImage() {


                @Override
                public void Update(File captured_image, int position, String type) {

                    is_image_new_add = true;
                    mPosition = position;
                    Document_ID = licenceUploadArrayList.get(position).get_id();
                    Document_Name = licenceUploadArrayList.get(position).getName();
                    if (type.equalsIgnoreCase("camera")) {
                        fCapturedImage = captured_image;
                        Uri photoURI = FileProvider.getUriForFile(GetDocuments.this, getPackageName() + ".provider", fCapturedImage);
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
            });

            licenceUploadRV.setAdapter(adatpter);

        } catch (Exception e) {

        }
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

    void PostRequestUploadDocument() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("provider_id", provider_id);
            Gson gson2 = new Gson();
            // String documentStringPojo = licenceUploadArrayList.toString();
            if (licenceUploadArrayList.size() > 0) {
               /* DocumentUploadPojo mPojo = gson2.fromJson(documentStringPojo, DocumentUploadPojo.class);
                System.out.println("available----------" + gson2.fromJson(documentStringPojo, DocumentUploadPojo.class));
                ArrayList<RegisterLicenceUploadPojo> pojoArrayList = mPojo.getPojoArrayList();
                JSONArray jsonArrayDocument = new JSONArray();*/
                JSONArray jsonArrayDocument = new JSONArray();
                for (int i = 0; i < licenceUploadArrayList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", licenceUploadArrayList.get(i).getName());
                    jsonObject.put("file_type", licenceUploadArrayList.get(i).getFile_typle());
                    jsonObject.put("path", licenceUploadArrayList.get(i).getImageWebUrl());
                    jsonArrayDocument.put(jsonObject);
                }
                jsonParams.put("documents", jsonArrayDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ServiceConstant.UPDATEFILES, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("RegistrationRequest response" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String str_Status, str_Message, str_Response;
                            str_Status = jsonObject.getString("status");

                            if (isChangedImage) {
                                Toast.makeText(GetDocuments.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(GetDocuments.this, NavigationDrawer.class);
                                intent.putExtra("response", "");
                                startActivity(intent);
                            } else {
                                Toast.makeText(GetDocuments.this, "Already documents has been updated", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String Str_Status = "";
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GetDocuments.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> user = session.getUserDetails();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("type", "tasker");
                headers.put("user", provider_id);
                headers.put("device", user.get(SessionManager.KEY_GCM_ID));
                headers.put("devicetype", "android");
                return headers;
            }

        };
        ;

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }


    private void DeleteDocument(final String Doc_id) {

        try {
            final PkDialog mDialog = new PkDialog(GetDocuments.this);
            mDialog.setDialogTitle(getResources().getString(R.string.confirm_txt));
            mDialog.setDialogMessage(getResources().getString(R.string.document_delete_txt));
            mDialog.setCancelableIn(false);
            mDialog.setOnKeyListenerIn(true);
            mDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    PostRequestDeleteDocument(Doc_id);
                }
            });

            mDialog.setNegativeButton(getResources().getString(R.string.no), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDialog.dismiss();

                }
            });
            mDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void PostRequestDeleteDocument(final String Doc_id) {

        final Dialog dialog1 = new Dialog(GetDocuments.this);
        dialog1.getWindow();
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.custom_loading);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        TextView dialog_title = (TextView) dialog1.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("doc_id", Doc_id);
        jsonParams.put("provider_id", provider_id);

        ServiceRequest mRequest = new ServiceRequest(GetDocuments.this);
        mRequest.makeServiceRequest(ServiceConstant.REMOVE_DOCUMENT, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("status")) {

                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {

                            DocumentPojo pojo = new DocumentPojo();
                            pojo.set_id(Doc_id);
                            pojo.setLicenceCount("");
                            pojo.setName(available_document_list.get(mPosition).getDocName());
                            pojo.setString_status("");
                            pojo.setImageWebUrl("");
                            pojo.setExpiryDate("");
                            pojo.setFile_typle("");
                            pojo.setDocumentPath("");
                            licenceUploadArrayList.set(mPosition, pojo);

                            adatpter.notifyDataSetChanged();
                        }

                    } else {

                        Toast.makeText(GetDocuments.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

                dialog1.dismiss();

            }

            @Override
            public void onErrorListener() {

                dialog1.dismiss();
            }
        });
    }


    private void AddNewImageRequest(String url, final byte[] byteImage) {
        final Dialog dialog1 = new Dialog(GetDocuments.this);
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

                        JSONObject docu_object = jsonObject.getJSONObject("documents");

                        if (docu_object.length() > 0) {

                            DocumentPojo pojo = new DocumentPojo();
                            pojo.set_id(docu_object.getString("id"));
                            pojo.setName(Document_Name);
                            pojo.setByteImage(licenceUploadArrayList.get(mPosition).getByteImage());
                            pojo.setFinalPic(licenceUploadArrayList.get(mPosition).getFinalPic());
                            pojo.setFile_typle(docu_object.getString("file_type"));
                            pojo.setImageWebUrl(docu_object.getString("path"));
                            licenceUploadArrayList.set(mPosition, pojo);

                        }

                        adatpter.notifyDataSetChanged();

                        if (myFileTypeStr.endsWith("pdf")) {
                            Toast.makeText(GetDocuments.this, getResources().getString(R.string.upload_pdf_file), Toast.LENGTH_SHORT).show();

                        } else if (myFileTypeStr.endsWith("doc")) {
                            Toast.makeText(GetDocuments.this, getResources().getString(R.string.upload_doc_file), Toast.LENGTH_SHORT).show();
                        } else if (myFileTypeStr.equals("image")) {
                            Toast.makeText(GetDocuments.this, getResources().getString(R.string.edit_profile_success_label), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (jsonObject.has("response")) {
                            sResponse = jsonObject.getString("response");
                            Toast.makeText(GetDocuments.this, sResponse, Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> user = session.getUserDetails();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("type", "tasker");
                headers.put("user", provider_id);
                headers.put("device", user.get(SessionManager.KEY_GCM_ID));
                headers.put("devicetype", "android");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doc_id", Document_ID);
                params.put("name", Document_Name);
                params.put("file_type", Document_file_type);
                params.put("provider_id", provider_id);
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

}
