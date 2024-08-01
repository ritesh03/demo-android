package com.maktoday.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.TextView;

import com.maktoday.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by cbl81 on 1/12/17.
 */

public class ImagePicker {

    private static final String TAG = ImagePicker.class.getSimpleName();

    String title="";

    private File imageFile;

    /**
     * Activity object that will be used while calling startActivityForResult(). Activity then will
     * receive the callbacks to its own onActivityResult() and is responsible of calling the
     * onActivityResult() of the ImagePicker for handling result and being notified.
     */
    private Activity context;

    /**
     * Fragment object that will be used while calling startActivityForResult(). Fragment then will
     * receive the callbacks to its own onActivityResult() and is responsible of calling the
     * onActivityResult() of the ImagePicker for handling result and being notified.
     */
    private Fragment fragment;

    private BottomSheetDialog bottomSheetDialog;

    private ImagePickerListener imagePickerListener;

    public ImagePicker(@NonNull Fragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getActivity();
        title="Change Picture";

        setupPickerDialog();
    }

    public void setImagePickerListener(@NonNull ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
    }

    private void setupPickerDialog() {
        final View layoutDialog = View.inflate(context, R.layout.item_image_chooser, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(layoutDialog);
        ((TextView)layoutDialog.findViewById(R.id.tvTitle)).setText(title);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnCamera:
                        openCamera();
                        break;

                    case R.id.btnPhotoGallery:
                        openGallery();
                        break;
                }
                bottomSheetDialog.dismiss();
            }
        };
        layoutDialog.findViewById(R.id.btnCamera).setOnClickListener(clickListener);
        layoutDialog.findViewById(R.id.btnPhotoGallery).setOnClickListener(clickListener);
        layoutDialog.findViewById(R.id.btnCancel).setOnClickListener(clickListener);
    }

    public void showImagePicker() {
        if (bottomSheetDialog != null)
            bottomSheetDialog.show();
    }

    /**
     * Handles the result of events that the Activity or Fragment receives on its own
     * onActivityResult(). This method must be called inside the onActivityResult()
     * of the container Activity or Fragment.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Constants.REQUEST_CODE_GALLERY_IMAGE) && (resultCode == Activity.RESULT_OK)) {
            String imagePath = getImagePathFromGallery(context, data.getData());
            if (imagePath != null) {
                Log.d(TAG, "Gallery image selected");
                imageFile = new File(imagePath);
                imagePickerListener.onImageSelectedFromPicker(imageFile);
            }
        } else if ((requestCode == Constants.REQUEST_CODE_CAMERA) && (resultCode == Activity.RESULT_OK)) {
            Log.d(TAG, "Image selected from camera");
            if (imageFile != null) {
                imagePickerListener.onImageSelectedFromPicker(imageFile);
                revokeUriPermission();
            }
        }

    }

    /**
     * Save the image to device external cache
     */
    private void openCamera() {
        checkListener();

        File imageDirectory = context.getExternalCacheDir();

        if (imageDirectory != null)
            startCameraIntent(imageDirectory.getAbsolutePath());
        else
            Log.d(TAG, "External cache directory is null");
    }

    private void startCameraIntent(@NonNull final String imageDirectory) {
        try {
            imageFile = GeneralFunction.createImageFile(imageDirectory);

            if (fragment == null)
                context.startActivityForResult(getCameraIntent(), Constants.REQUEST_CODE_CAMERA);
            else
                fragment.startActivityForResult(getCameraIntent(), Constants.REQUEST_CODE_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the camera intent using FileProvider to avoid the FileUriExposedException in Android N and above
     */
    private Intent getCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Put the uri of the image file as intent extra
        Uri imageUri = FileProvider.getUriForFile(context,
                context.getPackageName()+".provider",
                imageFile);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // Get a list of all the camera apps
        List<ResolveInfo> resolvedIntentActivities =
                context.getPackageManager()
                        .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);

        // Grant uri read/write permissions to the camera apps
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;

            context.grantUriPermission(packageName, imageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        return cameraIntent;
    }

    private void openGallery() {
        checkListener();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (fragment == null)
            context.startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY_IMAGE);
        else
            fragment.startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY_IMAGE);
    }

    private String getImagePathFromGallery(@NonNull final Context context, @NonNull final Uri imageUri) {
        String imagePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return imagePath;
    }

    /**
     * Revoke access permission for the content URI to the specified package otherwise the permission won't be
     * revoked until the device restarts.
     */
    private void revokeUriPermission() {
        Log.i(TAG, "Uri permission revoked");
        context.revokeUriPermission(FileProvider.getUriForFile(context,
                context.getPackageName()+ ".provider", imageFile),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private void checkListener() {
        if (imagePickerListener == null) {
            throw new RuntimeException("ImagePickerListener must be set before calling openCamera() or openGallery()");
        }
    }

    public interface ImagePickerListener {
        void onImageSelectedFromPicker(File imageFile);
    }
}
