package com.hoperlady.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hoperlady.R;
import com.hoperlady.Utils.SessionManager;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import core.gcm.GCMIntializer;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;

public class RegistrationSuccessPage extends AppCompatActivity {

    TextView TvOk;
    private String response = "", GCM_ID = "";
    private SessionManager session;
    private SocketHandler socketHandler;

    String Str_status = "", Str_response = "", Str_providerid = "",
            Str_socky_id = "", Str_provider_name = "", Str_provider_email = "",
            Str_provider_img = "", Str_key = "", verified_status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success_page);

        TvOk = (TextView) findViewById(R.id.TvOk);

        response = getIntent().getStringExtra("response");

        session = new SessionManager(RegistrationSuccessPage.this);
        socketHandler = SocketHandler.getInstance(RegistrationSuccessPage.this);

        TvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");
                    verified_status = jobject.getString("verified_status");

                    if (Str_status.equalsIgnoreCase("1") || Str_status.equalsIgnoreCase("3")) {

                        JSONObject object = jobject.getJSONObject("response");
                        Str_providerid = object.getString("provider_id");
                        Str_provider_name = object.getString("provider_name");
                        Str_provider_email = object.getString("email");
                        Str_provider_img = object.getString("provider_image");

                        String ScurrencyCode = object.getString("currency");

                        session.setTaskerVerification(verified_status);
                        session.createWalletSession(ScurrencyCode);

                    } else {

                        Str_response = jobject.getString("response");

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }


                if (Str_status.equalsIgnoreCase("1")) {

                    GCMIntializer initializer = new GCMIntializer(RegistrationSuccessPage.this, new GCMIntializer.CallBack() {
                        @Override
                        public void onRegisterComplete(String registrationId) {

                            GCM_ID = registrationId;
                            RegisterSuccess();
                        }

                        @Override
                        public void onError(String errorMsg) {

                            GCM_ID="";
                            RegisterSuccess();
                        }

                    });

                    initializer.init();
                }
            }
        });

    }

    private void RegisterSuccess(){

        session.createLoginSession(Str_provider_email, Str_providerid, Str_provider_name, Str_provider_img, GCM_ID);
        socketHandler.getSocketManager().connect();

        if (!ChatMessageService.isStarted()) {

            Intent intent1 = new Intent(RegistrationSuccessPage.this, ChatMessageService.class);
            startService(intent1);

        }

        Intent intent1 = new Intent();
        intent1.setAction("com.newlogin.finish");
        sendBroadcast(intent1);

        Intent intent2 = new Intent(RegistrationSuccessPage.this, NavigationDrawer.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent2);
        finish();

    }
}
