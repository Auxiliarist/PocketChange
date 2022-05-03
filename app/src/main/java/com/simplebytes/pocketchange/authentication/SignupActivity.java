package com.simplebytes.pocketchange.authentication;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.text.InputType;
import android.util.Log;
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

public class SignupActivity extends BaseActivity {

    private EditText signupUsername, signupPassword, signupEmail;
    private Button signupJoinBtn;
    private TextView mActionLogin;
    private String username, password, email;
    private android.app.ProgressDialog progressDialog;
    private FirebaseAnalytics mFirebaseAnalytics;
    final LoginValidation validation = new LoginValidation(SignupActivity.this);

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_signup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mActionLogin = (TextView) findViewById(R.id.actionLogin);
        signupUsername = (EditText) findViewById(R.id.signupUsername);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        signupEmail = (EditText) findViewById(R.id.signupEmail);
        signupJoinBtn = (Button) findViewById(R.id.signupJoinBtn);

        signupPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
        signupPassword.setTypeface(Typeface.DEFAULT);

        mActionLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                ActivityCompat.finishAffinity(SignupActivity.this);
            }
        });

        signupJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });

    }

    private void SignUp() {

        username = signupUsername.getText().toString();
        password = signupPassword.getText().toString();
        email = signupEmail.getText().toString();

        if(validation.verifyRegForm(signupUsername, signupPassword, signupEmail, username, password, email))
        {
            if(AppSingleton.getInstance().isConnected())
            {
                progressDialog = android.app.ProgressDialog.show(SignupActivity.this, "Please Wait", "Creating Account...");

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Config.METHOD_ACCOUNT_SIGNUP, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (AppSingleton.getInstance().authorize(response)) {
                            //signup worked
                            mFirebaseAnalytics.setUserId(AppSingleton.getInstance().getUsername());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, null);

                            //Reload Main Activity Clearing All other activies so user can't go back to signin/login page
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {

                            switch (AppSingleton.getInstance().getErrorCode()) {

                                case 300:
                                    signupUsername.setError(getString(R.string.error_signup_taken_username));
                                    break;

                                case 301:
                                    signupEmail.setError(getString(R.string.error_signup_taken_email));
                                    break;

                                default:

                                    break;

                            }
                        }

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Something Went Wrong, Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams()  {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        params.put("email", email);
                        params.put("clientId", Config.CLIENT_ID);

                        return params;
                    }
                };

                AppSingleton.getInstance().addToRequestQueue(jsonReq);
            }else{
                //check internet
                Toast.makeText(getApplicationContext(), R.string.error_network_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
