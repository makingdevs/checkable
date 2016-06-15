package com.makingdevs.mybarista.ui.activity

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.makingdevs.mybarista.common.SingleFragmentActivity
import com.makingdevs.mybarista.ui.fragment.ProfilePublicFragment

class ProfilePublicActivity extends SingleFragmentActivity{

    static String USER_ID = "user_id"

    static Intent newIntentWithContext(Context context,String id){
        Intent intent = new Intent(context, ProfilePublicActivity)
        intent.putExtra(USER_ID, id)
        intent
    }

    @Override
    Fragment createFragment() {
        String id = getIntent()?.extras.getSerializable(USER_ID)
        new ProfilePublicFragment(id)
    }
}