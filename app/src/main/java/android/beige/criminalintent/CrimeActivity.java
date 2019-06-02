package android.beige.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SimpleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "com.android.beige.criminalintent.crime_id";

    public static Intent newIntent(Context pkgcontext, UUID crimeID) {
        Intent intent = new Intent(pkgcontext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
