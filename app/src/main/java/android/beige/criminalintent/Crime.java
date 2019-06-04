package android.beige.criminalintent;

import java.util.Calendar;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Calendar mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = Calendar.getInstance();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Calendar getDate() {
        return mDate;
    }

    public void setDate(Calendar date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
