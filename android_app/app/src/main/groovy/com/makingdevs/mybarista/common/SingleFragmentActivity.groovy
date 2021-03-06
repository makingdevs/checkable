package com.makingdevs.mybarista.common

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.makingdevs.mybarista.BuildConfig
import com.makingdevs.mybarista.R
import com.makingdevs.mybarista.service.SessionManager
import com.makingdevs.mybarista.service.SessionManagerImpl
import groovy.transform.CompileStatic
import io.fabric.sdk.android.Fabric

@CompileStatic
abstract class SingleFragmentActivity extends AppCompatActivity implements WithFragment {

    SessionManager mSessionManager = SessionManagerImpl.instance
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, new Crashlytics.Builder().core(core).build())
        setContentView(R.layout.activity_container)
        FragmentManager fm = getSupportFragmentManager()
        Fragment fragment = fm.findFragmentById(R.id.single_container)
        if (!fragment)
            fm.beginTransaction().add(R.id.single_container, createFragment()).commit()

        setToolbar()

    }

    def setToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar)
        mToolbar.navigationOnClickListener = { onNavigationButtonClicked() }
        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
        }
    }

    def setActivityToolbarVisible(boolean visible){
        mToolbar.setVisibility(visible? View.VISIBLE :  View.INVISIBLE )
    }

    def onNavigationButtonClicked() {
        finish()
    }
}