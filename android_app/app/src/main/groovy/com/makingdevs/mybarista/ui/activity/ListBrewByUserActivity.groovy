package com.makingdevs.mybarista.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.makingdevs.mybarista.common.SingleFragmentActivity
import com.makingdevs.mybarista.ui.fragment.ListBrewByUserFragment
import groovy.transform.CompileStatic

@CompileStatic
public class ListBrewByUserActivity extends SingleFragmentActivity {

    static Intent newIntentWithContext(Context context){
        Intent intent = new Intent(context, ListBrewByUserActivity)
        intent
    }

    @Override
    Fragment createFragment() {
        new ListBrewByUserFragment("juan@makingdevs.com")
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        createMenu()
    }
}