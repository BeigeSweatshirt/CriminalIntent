package android.beige.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private static final int IS_BENIGN_CRIME = 0;
    private static final int IS_SERIOUS_CRIME = 1;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crimelist, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.recyclerview_crimelist);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == IS_BENIGN_CRIME) return new CrimeHolderBenign(layoutInflater, parent);
            else return new CrimeHolderSerious(layoutInflater, parent);
        }

        @Override
        public int getItemViewType(int position) {
            Crime mCrime = mCrimes.get(position);
            int viewType;

            if (!mCrime.getRequiresPolice()) viewType = IS_BENIGN_CRIME;
            else viewType = IS_SERIOUS_CRIME;
            return viewType;
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private abstract class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int layout) {
            super(inflater.inflate(layout, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.tv_crimelist_title);
            mDateTextView = itemView.findViewById(R.id.tv_crimelist_date);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class CrimeHolderBenign extends CrimeHolder {

        CrimeHolderBenign(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.item_crimelist_benign);
        }
    }

    private class CrimeHolderSerious extends CrimeHolder {
        private Button mBtnContactPolice;

        CrimeHolderSerious(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.item_crimelist_serious);
        }

        @Override
        public void bind(final Crime crime) {
            super.bind(crime);

            mBtnContactPolice = itemView.findViewById(R.id.btn_crimelist);
            mBtnContactPolice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String policeToast = "srs crime: "
                            + crime.getTitle();

                    Toast.makeText(getActivity(), policeToast, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
