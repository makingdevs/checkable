package com.makingdevs.mybarista.common

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import groovy.transform.CompileStatic
import makingdevs.com.mybarista.R

@CompileStatic
abstract class SingleFragmentActivity extends AppCompatActivity implements WithFragment {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        FragmentManager fm = getSupportFragmentManager()
        Fragment fragment = fm.findFragmentById(R.id.container)
        if (!fragment)
            fm.beginTransaction().add(R.id.container, createFragment()).commit()
    }

}