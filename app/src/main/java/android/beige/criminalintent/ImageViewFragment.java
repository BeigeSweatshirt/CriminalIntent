package android.beige.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class ImageViewFragment extends DialogFragment {
    private static final String ARG_IMAGE = "image";
    private ImageView mSuspectImage;

    public static ImageViewFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, imagePath);

        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);
        mSuspectImage = v.findViewById(R.id.iv_crime_suspect);
        Bitmap bitmap = PictureUtils.getScaledBitmap(
                (String) getArguments().getSerializable(ARG_IMAGE), getActivity());
        mSuspectImage.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }
}
