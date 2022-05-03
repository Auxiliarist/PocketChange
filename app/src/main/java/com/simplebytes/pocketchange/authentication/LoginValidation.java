package com.simplebytes.pocketchange.authentication;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.simplebytes.pocketchange.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginValidation extends Application {

    Context context;

    public LoginValidation(Context context)
    {
        this.context = context;
    }

    public boolean isValidEmail(String email) {

        if (TextUtils.isEmpty(email)) {

            return false;

        } else {

            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public boolean isValidLogin(String login) {

        String regExpn = "^([a-zA-Z]{4,24})?([a-zA-Z][a-zA-Z0-9_]{4,24})$";
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(login);

        if (matcher.matches()) {

            return true;

        } else {

            return false;
        }
    }

    public boolean isValidSearchQuery(String query) {

        String regExpn = "^([a-zA-Z]{1,24})?([a-zA-Z][a-zA-Z0-9_]{1,24})$";
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if (matcher.matches()) {

            return true;

        } else {

            return false;
        }
    }

    public boolean isValidPassword(String password) {

        String regExpn = "^[a-z0-9_]{6,24}$";
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {

            return true;

        } else {

            return false;
        }
    }

    public boolean checkUsername(String username, EditText input)
    {
        username = input.getText().toString();
        input.setError(null);

        if(username.length() == 0){
            input.setError(context.getString(R.string.error_login_empty_field));

            return false;
        }

        return true;
    }

    public boolean checkPassword(String password, EditText input)
    {
        password = input.getText().toString();
        input.setError(null);

        if(password.length() == 0){
            input.setError(context.getString(R.string.error_login_empty_field));

            return false;
        }

        return true;
    }

    public boolean verifyRegForm(EditText sUsername, EditText sPassword, EditText sEmail, String username, String password, String email){
        sUsername.setError(null);
        sPassword.setError(null);
        sEmail.setError(null);

        if(username.length() == 0){
            sUsername.setError(context.getString(R.string.error_login_empty_field));
            return false;
        }

        if(username.length() < 5){
            sUsername.setError(context.getString(R.string.error_signup_too_short_username));
            return false;
        }

        if(!isValidLogin(username)){
            sUsername.setError(context.getString(R.string.error_signup_wrong_format));
            return false;
        }

        if(password.length() == 0)
        {
            sPassword.setError(context.getString(R.string.error_login_empty_field));
            return false;
        }

        if(password.length() < 6)
        {
            sPassword.setError(context.getString(R.string.error_signup_too_short_password));
            return false;
        }

        if(!isValidPassword(password))
        {
            sPassword.setError(context.getString(R.string.error_signup_wrong_format));
            return false;
        }

        if(email.length() == 0){
            sEmail.setError(context.getString(R.string.error_login_empty_field));
            return false;
        }

        if(!isValidEmail(email)){
            sEmail.setError(context.getString(R.string.error_signup_wrong_format));
            return false;
        }

        return true;
    }
}

