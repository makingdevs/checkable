package com.makingdevs.mybarista.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makingdevs.mybarista.R
import com.makingdevs.mybarista.common.ImageUtil
import com.makingdevs.mybarista.common.OnItemClickListener
import com.makingdevs.mybarista.common.RequestPermissionAndroid
import com.makingdevs.mybarista.model.Checkin
import com.makingdevs.mybarista.model.PhotoCheckin
import com.makingdevs.mybarista.model.command.UploadCommand
import com.makingdevs.mybarista.service.S3assetManager
import com.makingdevs.mybarista.service.S3assetManagerImpl
import com.makingdevs.mybarista.ui.adapter.PhotoAdapter
import com.makingdevs.mybarista.view.LoadingDialog
import groovy.transform.CompileStatic
import retrofit2.Call
import retrofit2.Response

@CompileStatic
class ShowGalleryFragment extends Fragment implements OnItemClickListener<PhotoCheckin> {

    ShowGalleryFragment() { super() }
    RecyclerView recyclerViewPhotos
    PhotoAdapter photoAdapter
    ImageUtil imageUtil = new ImageUtil()
    String currentUser
    String checkinId
    S3assetManager mS3Manager = S3assetManagerImpl.instance
    FloatingActionButton floatingActionButtonCamera
    RequestPermissionAndroid requestPermissionAndroid = new RequestPermissionAndroid()
    LoadingDialog loadingDialog = LoadingDialog.newInstance(R.string.message_uploading_photo)

    @Override
    View onCreateView(LayoutInflater inflater,
                      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_gallery, container, false)
        recyclerViewPhotos = (RecyclerView) root.findViewById(R.id.list_photos)
        recyclerViewPhotos.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3))
        floatingActionButtonCamera = (FloatingActionButton) root.findViewById(R.id.button_go_camera)
        root
    }

    @Override
    void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState)
        photoAdapter = new PhotoAdapter(getActivity(), populateGallery())
        photoAdapter.setOnItemClickListener(this)
        recyclerViewPhotos.adapter = photoAdapter
        currentUser = activity.getIntent().getStringExtra("USERID")
        checkinId = activity.getIntent().getStringExtra("CHECKINID")

        floatingActionButtonCamera.onClickListener = {
            requestPermissionAndroid.checkPermission(getActivity(), "storage")
            Fragment cameraFragment = new CameraFragment()
            cameraFragment.setSuccessActionOnPhoto { File photo ->
                getActivity().onBackPressed()
                uploadPicture(photo)
            }
            cameraFragment.setErrorActionOnPhoto {
                Toast.makeText(context, "Error al caputar la foto", Toast.LENGTH_SHORT).show()
                getActivity().onBackPressed()
            }
            getFragmentManager()
                    .beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), cameraFragment)
                    .addToBackStack(null).commit()
        }

    }

    void uploadPicture(File photo) {
        String option = activity.getIntent().getStringExtra("CONTAINER")
        if (option != "new_checkin")
            loadingDialog.show(getActivity().getSupportFragmentManager(), "Loading dialog")
        switch (option) {
            case "new_checkin":
                sendResult(photo.absolutePath)
                getActivity().finish()
                break
            case "profile":
                mS3Manager.uploadPhotoUser(new UploadCommand(idUser: currentUser, pathFile: photo.getPath()), onSuccessPhoto(), onError())
                break
            case "checkin":
                mS3Manager.upload(new UploadCommand(idCheckin: checkinId, idUser: currentUser, pathFile: photo.absolutePath), onSuccessPhoto(), onError())
                break
            case "barista":
                mS3Manager.uploadPhoto(new UploadCommand(pathFile: photo.getPath()), onSuccessPhoto(), onError())
                break
        }
    }


    List<PhotoCheckin> populateGallery() {
        List<String> pathPhotos = imageUtil.getGalleryPhotos(getActivity())
        pathPhotos.reverse().collect { urlPhoto ->
            new PhotoCheckin(url_file: urlPhoto)
        }
    }

    @Override
    void onItemClicked(PhotoCheckin photo) {
        uploadPicture(new File(photo.url_file))
    }

    private Closure onSuccessPhoto() {
        { Call<PhotoCheckin> call, Response<PhotoCheckin> response ->
            sendResult(response.body().url_file)
            loadingDialog.dismiss()
            getActivity().finish()

        }
    }

    private Closure onError() {
        { Call<Checkin> call, Throwable t ->
            Log.d("ERRORZ", "el error " + t.message)
            loadingDialog.dismiss()
            getActivity().finish()
        }
    }

    void sendResult(String urlFile) {
        Intent intent = new Intent()
        intent.putExtra("PATH_PHOTO", urlFile)
        getActivity().setResult(Activity.RESULT_OK, intent)
    }
}