package com.makingdevs.mybarista.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.makingdevs.mybarista.R
import com.makingdevs.mybarista.model.Checkin
import com.makingdevs.mybarista.model.User
import com.makingdevs.mybarista.model.command.CheckinCommand
import com.makingdevs.mybarista.service.CheckinManager
import com.makingdevs.mybarista.service.CheckingManagerImpl
import com.makingdevs.mybarista.service.SessionManager
import com.makingdevs.mybarista.service.SessionManagerImpl
import groovy.transform.CompileStatic
import retrofit2.Call
import retrofit2.Response

@CompileStatic
public class RatingCoffeFragment extends Fragment {

    private static final String TAG = "RatingCoffeFragment"
    private RatingBar mRatingCoffeBar
    private TextView mRatingCoffeText
    private static Context contextView
    private User currentUser
    private String mCheckinId

    CheckinManager mCheckinManager = CheckingManagerImpl.instance
    SessionManager mSessionManager = SessionManagerImpl.instance

    RatingCoffeFragment() { super() }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        mCheckinId = getActivity().getIntent().getExtras().getString("checkin_id")
        currentUser = mSessionManager.getUserSession(getContext())
        mCheckinManager.show(mCheckinId, onSuccessShow(), onError())
    }

    private void addListenerToRatingCoffeBar() {
        mRatingCoffeBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                sendRatingCoffeToCheckin()
            }
        })
    }

    View onCreateView(LayoutInflater inflater,
                      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rating_coffee, container, false)
        mRatingCoffeBar = (RatingBar) root.findViewById(R.id.rating_coffe_bar)
        mRatingCoffeText = (TextView) root.findViewById(R.id.rating_coffe_text)
        contextView = getActivity().getApplicationContext()
        root
    }

    private Closure onSuccessShow() {
        { Call<Checkin> call, Response<Checkin> response ->
            if (response.code() == 200) {
                Checkin checkin = response.body()
                setRatingCoffe(checkin)
                addListenerToRatingCoffeBar()
                if (checkin.author == currentUser.username)
                    mRatingCoffeBar.isIndicator = false
            } else {
                Toast.makeText(contextView, R.string.toastCheckinFail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Closure onSuccess() {
        { Call<Checkin> call, Response<Checkin> response ->
            setRatingCoffe(response.body())
            if (response.code() == 200) {
                Log.d(TAG, response.body().toString())
            } else {
                Toast.makeText(contextView, R.string.toastCheckinFail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Closure onError() {
        { Call<Checkin> call, Throwable t ->
            Toast.makeText(contextView, R.string.toastCheckinFail, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRatingCoffeToCheckin() {
        CheckinCommand command = new CheckinCommand(rating: String.valueOf(mRatingCoffeBar.getRating()))
        mCheckinManager.saveRating(mCheckinId, command, onSuccess(), onError())
    }

    private void setRatingCoffe(Checkin checkin) {
        mRatingCoffeBar.setRating(Float.parseFloat(checkin.rating ?: "0"))
        mRatingCoffeText.text = checkin.rating ?: "0"
    }

}