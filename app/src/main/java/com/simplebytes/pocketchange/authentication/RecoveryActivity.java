package com.simplebytes.pocketchange.authentication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.activities.BaseActivity;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.helpers.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecoveryActivity extends BaseActivity {

    Button mContinueBtn;
    EditText mEmail;
    String email;
    private android.app.ProgressDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_recovery;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mEmail = (EditText) findViewById(R.id.recoveryEmail);
        mContinueBtn = (Button) findViewById(R.id.actionRecovery);

        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();

                if(!AppSingleton.getInstance().isConnected()){
                    Toast.makeText(RecoveryActivity.this, R.string.error_network_connection, Toast.LENGTH_SHORT).show();
                }else{
                    LoginValidation validation = new LoginValidation(RecoveryActivity.this);

                    if(validation.isValidEmail(email))
                        RecoverPassword();
                    else
                        Toast.makeText(RecoveryActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RecoverPassword()
    {
        progressDialog = android.app.ProgressDialog.show(RecoveryActivity.this, "Please Wait", "Sending Email...");

        CustomRequest request = new CustomRequest(Request.Method.POST, Config.METHOD_ACCOUNT_RECOVERY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if(!response.getBoolean("error")){
                        Toast.makeText(RecoveryActivity.this, R.string.recovery_link_sent, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(RecoveryActivity.this, R.string.recovery_no_user, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                } finally {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                //Toast.makeText(RecoveryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                //Toast.makeText(RecoveryActivity.this, R.string.error_network_login, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("clientId", Config.CLIENT_ID);
                params.put("email", email);

                return params;
            }
        };

        AppSingleton.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onBackPressed(){

        finish();
    }
}
