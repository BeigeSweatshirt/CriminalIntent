package android.beige.criminalintent;

import android.support.v4.app.Fragment;

public class CrimeListActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
