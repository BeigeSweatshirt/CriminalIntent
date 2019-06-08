package android.beige.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private File mSuspectImage;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mSuspectImage = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = v.findViewById(R.id.et_crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = v.findViewById(R.id.btn_crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = v.findViewById(R.id.cb_crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.btn_crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager pm = getActivity().getPackageManager();
        if (pm.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mReportButton = v.findViewById(R.id.btn_crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_reportsubject));
                intent = Intent.createChooser(intent, getString(R.string.crime_reportintenttitle));
                startActivity(intent);
            }
        });

        mPhotoButton = v.findViewById(R.id.ib_crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (mSuspectImage != null) && (captureImage.resolveActivity(pm) != null);
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gets URI for camera
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.android.beige.criminalintent.fileprovider", mSuspectImage);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                // Filters out irrelevant activities
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                // ...and for the ones that are relevant, grants them access to the photo directory
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                //and finally starts the activity
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = v.findViewById(R.id.iv_crime_suspect);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_DATE:
                    Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                    mCrime.setDate(date);
                    updateCrime();
                    updateDate();
                    return;

                case REQUEST_CONTACT:
                    if (data != null) {
                        Uri contactUri = data.getData();
                        String[] queryFields = new String[]{
                                ContactsContract.Contacts.DISPLAY_NAME
                        };
                        Cursor c = getActivity().getContentResolver().query(
                                contactUri,
                                queryFields,
                                null,
                                null,
                                null);

                        try {
                            if (c.getCount() == 0) {
                                return;
                            }
                            c.moveToFirst();
                            String suspect = c.getString(0);
                            mCrime.setSuspect(suspect);
                            updateCrime();
                            mSuspectButton.setText(suspect);
                        } finally {
                            c.close();
                        }
                    }
                    return;

                case REQUEST_PHOTO:
                    Uri uri = FileProvider.getUriForFile(getActivity(),
                            "com.android.beige.criminalintent.fileprovider", mSuspectImage);
                    getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    updateCrime();
                    updatePhotoView();

                default:
                    Log.d(TAG, "Activity returned with unexpected requestCode");
                    return;
            }
        } else {
            Log.d(TAG, "Activity returned with unexpected resultCode");
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_reportsolved);
        } else {
            solvedString = getString(R.string.crime_reportunsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_reportnosuspect);
        } else {
            suspect = getString(R.string.crime_reportsuspect, suspect);
        }

        String report = getString(R.string.crime_fullreport,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView() {
        if (mSuspectImage == null || !mSuspectImage.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mSuspectImage.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
