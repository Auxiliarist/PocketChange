package com.simplebytes.pocketchange.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.helpers.AppSingleton;

public class AboutActivity extends BaseActivity {

    Button btnLogout, btnFeedback;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("About");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnFeedback = (Button) findViewById(R.id.button4);

        final String[] email = {"support@pocketchange.biz"};
        final String subject = "Feedback";

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Logout the User and Restart App
                AppSingleton.getInstance().Logout(); //Branch Logout included in this method
                ActivityCompat.finishAffinity(AboutActivity.this);
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "support@pocketchange.biz", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send Feedback: "));
                }*/

                composeEmail(email, subject);
            }
        });

    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
