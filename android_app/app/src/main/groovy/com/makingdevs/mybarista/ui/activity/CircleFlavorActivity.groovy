package com.makingdevs.mybarista.ui.activity

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.makingdevs.mybarista.common.SingleFragmentActivity
import com.makingdevs.mybarista.ui.fragment.CircleFlavorFragment
import groovy.transform.CompileStatic

@CompileStatic
public class CircleFlavorActivity extends SingleFragmentActivity {

    static Intent newIntentWithContext(Context context){
        Intent intent = new Intent(context, CircleFlavorActivity)
        intent
    }

    @Override
    Fragment createFragment() {
        //TODO: cambiar a meotodo newInstance
        new CircleFlavorFragment()
    }

}
