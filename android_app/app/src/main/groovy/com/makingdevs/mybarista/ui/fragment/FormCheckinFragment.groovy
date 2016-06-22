package com.makingdevs.mybarista.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.makingdevs.mybarista.R
import com.makingdevs.mybarista.common.LocationUtil
import com.makingdevs.mybarista.model.Checkin
import com.makingdevs.mybarista.model.GPSLocation
import com.makingdevs.mybarista.model.User
import com.makingdevs.mybarista.model.Venue
import com.makingdevs.mybarista.model.command.CheckinCommand
import com.makingdevs.mybarista.model.command.VenueCommand
import com.makingdevs.mybarista.service.*
import com.makingdevs.mybarista.ui.activity.PrincipalActivity
import com.makingdevs.mybarista.ui.activity.SearchVenueFoursquareActivity
import groovy.transform.CompileStatic
import retrofit2.Call
import retrofit2.Response

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

@CompileStatic
class FormCheckinFragment extends Fragment {

    static final String TAG = "FormCheckinFragment"
    static Context contextView
    EditText originEditText
    EditText priceEditText
    EditText noteEditText
    Spinner methodFieldSprinner
    Button checkInButton
    RatingBar ratingCoffe
    Spinner venueSpinner
    GPSLocation mGPSLocation
    Button addVenueButton

    CheckinManager mCheckinManager = CheckingManagerImpl.instance
    SessionManager mSessionManager = SessionManagerImpl.instance
    // TODO: Refactor de nombres, diseño y responsabilidad
    LocationUtil mLocationUtil = LocationUtil.instance

    List<Venue> venues = [new Venue(name: "Selecciona lugar")]

    FoursquareManager mFoursquareManager = FoursquareManagerImpl.instance

    FormCheckinFragment() {}

    @Override
    View onCreateView(LayoutInflater inflater,
                      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_chek_in, container, false)
        originEditText = (EditText) root.findViewById(R.id.originField)
        priceEditText = (EditText) root.findViewById(R.id.priceField)
        noteEditText = (EditText) root.findViewById(R.id.noteField)
        methodFieldSprinner = (Spinner) root.findViewById(R.id.methodSpinner)
        checkInButton = (Button) root.findViewById(R.id.btnCheckIn)
        addVenueButton = (Button) root.findViewById(R.id.btnAddVenue)
        contextView = getActivity().getApplicationContext()
        ratingCoffe = (RatingBar) root.findViewById(R.id.rating_coffe_bar)
        venueSpinner = (Spinner) root.findViewById(R.id.spinner_venue)
        checkInButton.onClickListener = { View v -> saveCheckIn(getFormCheckIn()) }
        addVenueButton.onClickListener = {
            Intent intent = SearchVenueFoursquareActivity.newIntentWithContext(getContext())
            startActivity(intent)
        }
        Log.d(TAG, "${mGPSLocation}")
        root
    }

    @Override
    void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        mGPSLocation = new GPSLocation()
        mGPSLocation.addPropertyChangeListener { property ->
            GPSLocation gpsLocation = property["source"] as GPSLocation
            if (gpsLocation.latitude && gpsLocation.longitude) {
                mFoursquareManager.getVenuesNear(new VenueCommand(latitude: gpsLocation.latitude.toString(), longitude: gpsLocation.longitude.toString(), query: "cafe,coffee"), onSuccessGetVenues(), onErrorGetVenues())
            }
        }
        // TODO: Refactor de nombres, diseño y responsabilidad
        // No sé si es un singleton, y si hay que inicializar con context y objeto al mismo tiempo
        mLocationUtil.init(getActivity(), mGPSLocation)
        Log.d(TAG, "${mGPSLocation}")
    }

    void onStart() {
        super.onStart()
        mLocationUtil.mGoogleApiClient.connect()
        Log.d(TAG, "${mGPSLocation}")
    }

    void onStop() {
        super.onStop()
        mLocationUtil.mGoogleApiClient.disconnect()
        Log.d(TAG, "${mGPSLocation}")
    }


    private CheckinCommand getFormCheckIn() {
        String origin = originEditText.getText().toString()
        String price = priceEditText.getText().toString()
        String note = noteEditText.getText().toString()
        String method = methodFieldSprinner.getSelectedItem().toString()
        String rating = ratingCoffe.getRating()
        Integer selectIndexvenue = venueSpinner.getSelectedItemPosition()
        Venue detailVenue = getDetailVenueFromList(selectIndexvenue)
        User currentUser = mSessionManager.getUserSession(getContext())
        new CheckinCommand(method: method, note: note, origin: origin, price: price?.toString(), username: currentUser.username, rating: rating.toString(), idVenueFoursquare: detailVenue.id)
    }

    private void saveCheckIn(CheckinCommand checkin) {
        mCheckinManager.save(checkin, onSuccess(), onError())
    }

    private Closure onSuccess() {
        { Call<Checkin> call, Response<Checkin> response ->
            Log.d(TAG, response.dump().toString())
            if (response.code() == 201) {
                Intent intent = PrincipalActivity.newIntentWithContext(getContext())
                startActivity(intent)
                getActivity().finish()
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

    private Closure onSuccessGetVenues() {
        { Call<Checkin> call, Response<Checkin> response ->
            //Log.d(TAG,"Venues... "+ response.body().dump().toString())
            venues.addAll(response.body() as List)
            setVenuesToSpinner(venueSpinner, venues)
        }
    }

    private Closure onErrorGetVenues() {
        { Call<Checkin> call, Throwable t ->
            Log.d(TAG, "Error get venues... " + t.message)
        }
    }

    private void cleanForm() {
        originEditText.setText("")
        priceEditText.setText("")
        noteEditText.setText("")
    }

    void setVenuesToSpinner(Spinner spinner, List<Venue> venues) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, venues.name)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
    }

    Venue getDetailVenueFromList(Integer itemSelectedIndex) {
        Venue detailVenue = venues.getAt(itemSelectedIndex)
    }

}