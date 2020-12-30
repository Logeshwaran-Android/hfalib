package core.Volley;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hoperlady.R;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.app.NewLoginHomePageActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import core.Dialog.PkDialog;


public class ServiceRequest {
    PkDialog mDialog;
    private Context context;
    private ServiceListener mServiceListener;
    private StringRequest stringRequest;
    private String provider_id = "", gcmID = "";
    private SessionManager session;
    private String Status = "";

    public ServiceRequest(Context context) {
        this.context = context;

        session = new SessionManager(context);
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
        gcmID = user.get(SessionManager.KEY_GCM_ID);

    }

    public void cancelRequest() {
        if (stringRequest != null) {
            stringRequest.cancel();
        }
    }

    public void makeServiceRequest(final String url, int method, final HashMap<String, String> param, ServiceListener listener) {

        this.mServiceListener = listener;

        Log.e("URL", "URL---------->" + url);
        Log.e("param", "param---------->" + param);

        stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mServiceListener.onCompleteListener(response);
                    Log.d(ServiceRequest.class.toString(), "Response------->" + response);

                    JSONObject object = new JSONObject(response);
                    Status = object.getString("status");
                    if (Status.equalsIgnoreCase("5")) {
                        if (mDialog == null) {
                            System.out.println("-----------is dead----------------");
                            mDialog = new PkDialog(context);
                            mDialog.setCancelOnTouchOutside(false);
                            mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                            mDialog.setDialogMessage(context.getResources().getString(R.string.action_session_expired_message));
                            mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    session.logoutUser();
                                    Intent intent = new Intent(context, NewLoginHomePageActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            });
                            mDialog.show();
                        }

                    }

                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(context, R.string.service_request_unable_fetch_data, Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(context, R.string.service_request_auth_failure_error, Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        //Toast.makeText(context, R.string.service_request_server_error, Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(context, R.string.service_request_no_internet, Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(context, R.string.service_request_parse_error, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
                mServiceListener.onErrorListener();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                 Map<String, String> headers = new HashMap<String, String>();
                headers.put("type", "tasker");
                headers.put("user", provider_id);
                headers.put("device", gcmID);
                headers.put("devicetype", "android");
                return headers;
            }

        };
        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public interface ServiceListener {

        void onCompleteListener(String response);

        void onErrorListener();
    }


}
