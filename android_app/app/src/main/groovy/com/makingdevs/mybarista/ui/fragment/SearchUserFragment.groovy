package com.makingdevs.mybarista.ui.fragment

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.makingdevs.mybarista.R
import com.makingdevs.mybarista.model.User
import com.makingdevs.mybarista.model.UserProfile
import com.makingdevs.mybarista.service.SessionManager
import com.makingdevs.mybarista.service.SessionManagerImpl
import com.makingdevs.mybarista.service.UserManager
import com.makingdevs.mybarista.service.UserManagerImpl
import com.makingdevs.mybarista.ui.adapter.UserAdapter
import groovy.transform.CompileStatic
import retrofit2.Call
import retrofit2.Response

@CompileStatic
public class SearchUserFragment extends Fragment {

    private static final String TAG = "SearchUserFragment"
    RecyclerView mListUsers
    EditText mSearchText
    ImageView mSearchButton
    UserAdapter mUserAdapter

    SessionManager mSessionManager = SessionManagerImpl.instance
    UserManager mUserManager = UserManagerImpl.instance

    SearchUserFragment() { super() }

    @Override
    View onCreateView(LayoutInflater inflater,
                      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_user,container, false)
        mSearchText = (EditText) root.findViewById(R.id.search)
        mSearchButton = (ImageView) root.findViewById(R.id.search_button)
        mSearchButton.onClickListener = { searchUsers() }
        mListUsers = (RecyclerView) root.findViewById(R.id.list_users)
        mListUsers.setLayoutManager(new LinearLayoutManager(getActivity()))

        Map textWatcherMethodsForsearch = [
                beforeTextChanged:{CharSequence s, int start, int count, int after -> },
                onTextChanged:{CharSequence s, int start, int count, int after -> searchUsers() },
                afterTextChanged:{ Editable s -> }
        ]
        mSearchText.addTextChangedListener( textWatcherMethodsForsearch as TextWatcher )

        root
    }

    void searchUsers() {
        String search = mSearchText.text
        mUserManager.seachUsers([search:search],onSuccess(),onError())
    }

    private Closure onSuccess(){
        { Call<List<UserProfile>> call, Response<List<UserProfile>> response ->
            List<UserProfile> list = cleanList(response.body().toList())
            if(!mUserAdapter){
                mUserAdapter = new UserAdapter(getActivity(), list)
                mListUsers.adapter = mUserAdapter
            } else {
                mUserAdapter.setmUsers(list)
                mUserAdapter.notifyDataSetChanged()
            }
        }
    }

    private Closure onError(){
        { Call<List<UserProfile>> call, Throwable t -> Log.d("ERRORZ", "el error") }
    }

    private List<UserProfile> cleanList(List<UserProfile> list){
        User user = mSessionManager.getUserSession(getContext())
        UserProfile element = list.find{it.username == user.username}
        list.remove(element)
        list
    }

}
