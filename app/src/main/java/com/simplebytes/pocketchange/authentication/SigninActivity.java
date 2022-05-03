package com.simplebytes.pocketchange.authentication;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.activities.BaseActivity;
import com.simplebytes.pocketchange.activities.MainActivity;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.helpers.CustomRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends BaseActivity {

    private Button signinBtn;
    private EditText signinUsername, signinPassword;
    private TextView mActionForgot, mActionSignup;
    private String username, password;

    private android.app.ProgressDialog progressDialog;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_signin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final LoginValidation validation = new LoginValidation(SigninActivity.this);

        mActionForgot = (TextView) findViewById(R.id.actionForgot);
        mActionSignup = (TextView) findViewById(R.id.actionSignup);
        signinUsername = (EditText) findViewById(R.id.signinUsername);
        signinPassword = (EditText) findViewById(R.id.signinPassword);
        signinBtn = (Button) findViewById(R.id.signinBtn);

        mActionForgot.setVisibility(View.GONE);

        signinPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
        signinPassword.setTypeface(Typeface.DEFAULT);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = signinUsername.getText().toString();
                password = signinPassword.getText().toString();

                if(!AppSingleton.getInstance().isConnected())
                {
                    Toast.makeText(getApplicationContext(), R.string.error_network_connection, Toast.LENGTH_SHORT).show();

                }else if (!validation.checkUsername(username, signinUsername) || !validation.checkPassword(password, signinPassword)) {
                    //Check if fields are empty
                    //do nothing, they will show error on EditText fields
                }else{
                    SignIn();

                }
            }
        });

        mActionForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RecoveryActivity.class));
            }
        });

        mActionSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));

            }
        });

    }

    private void SignIn() {

        progressDialog = android.app.ProgressDialog.show(SigninActivity.this, "Please Wait", "Signing In...");

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Config.METHOD_ACCOUNT_LOGIN, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (AppSingleton.getInstance().authorize(response)) {

                    if (AppSingleton.getInstance().getState() == Config.ACCOUNT_STATE_ENABLED) {

                        mFirebaseAnalytics.setUserId(AppSingleton.getInstance().getUsername());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null);
                        ActivityCompat.finishAffinity(SigninActivity.this);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        //logout because user is banned
                        AppSingleton.getInstance().Logout();
                        Toast.makeText(SigninActivity.this, "This account is blocked by a moderator", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // incorrect username or password
                    Toast.makeText(getApplicationContext(), getText(R.string.error_login_wrong_fields), Toast.LENGTH_LONG).show();
                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), getText(R.string.error_network_login), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("clientId", Config.CLIENT_ID);

                return params;
            }
        };

        AppSingleton.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        ActivityCompat.finishAffinity(SigninActivity.this);
    }
}
