package android.beige.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "com.android.beige.criminalintent.time";
    private Calendar mCal;
    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Calendar cal) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, cal);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCal = (Calendar) getArguments().getSerializable(ARG_TIME);

        int hour = mCal.get(Calendar.HOUR_OF_DAY);
        int minute = mCal.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);


        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour, minute;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    hour = mTimePicker.getHour();
                                    minute = mTimePicker.getMinute();
                                } else {
                                    hour = mTimePicker.getCurrentHour();
                                    minute = mTimePicker.getCurrentMinute();
                                }


                                mCal.set(Calendar.HOUR_OF_DAY, hour);
                                mCal.set(Calendar.MINUTE, minute);
                                sendResult(Activity.RESULT_OK, mCal);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Calendar mCal) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, mCal);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
