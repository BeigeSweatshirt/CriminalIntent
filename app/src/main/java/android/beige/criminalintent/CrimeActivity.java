package android.beige.criminalintent;

import android.support.v4.app.Fragment;

public class CrimeActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
