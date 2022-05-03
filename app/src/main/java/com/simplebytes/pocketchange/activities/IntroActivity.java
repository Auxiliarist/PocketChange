package com.simplebytes.pocketchange.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.helpers.PrefManager;

public class IntroActivity extends AppIntro {

    private PrefManager prefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHome();
            finish();
        }

        // Just set a title, description,image and background. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide_1_title),getResources().getString(R.string.slide_1_desc), R.drawable.ic_check, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide_2_title),getResources().getString(R.string.slide_2_desc), R.drawable.ic_globe, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide_3_title),getResources().getString(R.string.slide_3_desc), R.drawable.ic_gift, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide_4_title),getResources().getString(R.string.slide_4_desc), R.drawable.ic_clock, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        showSkipButton(true);
        showBackButtonWithDone = true;
    }

    private void launchHome(){
        prefManager.setFirstTimeLaunch(false);
        Intent skip = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(skip);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        launchHome();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        launchHome();
    }
}
