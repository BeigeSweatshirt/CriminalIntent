package android.beige.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.framelayout_crimelist_details) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_crimelist_details, newDetail)
                    .commit();
        }
    }

    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.framelayout_crimelist_list);
        listFragment.updateUI();
    }
}
