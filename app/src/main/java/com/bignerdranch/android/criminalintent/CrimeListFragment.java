package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    interface Callbacks {
        void onCrimeSelected(UUID crimeID);
    }

    private Callbacks callbacks;
    CrimeListViewModel crimeListViewModel;
    CrimeAdapter crimeAdapter;

    RecyclerView crimeRecyclerView;
    TextView noCrimesTextView;
    Button newCrimeButton;

    public CrimeListFragment () {
        crimeAdapter = new CrimeAdapter(crimeItemCallback);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crimeListViewModel = new ViewModelProvider(this).get(CrimeListViewModel.class);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        crimeRecyclerView.setAdapter(crimeAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noCrimesTextView = view.findViewById(R.id.no_crimes_textview);
        newCrimeButton = view.findViewById(R.id.new_crime_button);
        crimeListViewModel.crimeListLiveData.observe(getViewLifecycleOwner(), this::updateUI);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_crime){
            Crime crime = new Crime();
            crimeListViewModel.addCrime(crime);
            callbacks.onCrimeSelected(crime.id);
            return  true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI(List<Crime> crimes) {
        if (crimes.size() > 0){
            newCrimeButton.setVisibility(View.INVISIBLE);
            noCrimesTextView.setVisibility(View.INVISIBLE);
        } else {
            newCrimeButton.setOnClickListener(v -> {
                Crime crime = new Crime();
                crimeListViewModel.addCrime(crime);
                callbacks.onCrimeSelected(crime.id);
            });
            newCrimeButton.setVisibility(View.VISIBLE);
            noCrimesTextView.setVisibility(View.VISIBLE);
        }

        crimeAdapter.submitList(crimes);
    }

    public static CrimeListFragment newInstance(){
        return new CrimeListFragment();
    }

    DiffUtil.ItemCallback<Crime> crimeItemCallback = new DiffUtil.ItemCallback<Crime>() {
        @Override
        public boolean areItemsTheSame(@NonNull Crime oldItem, @NonNull Crime newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Crime oldItem, @NonNull Crime newItem) {
            return oldItem.equals(newItem);
        }
    };

    class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime crime;
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final ImageView solvedImageView;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.crime_title);
            dateTextView = itemView.findViewById(R.id.crime_date);
            solvedImageView = itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        public void Bind(Crime crime) throws ParseException {
            this.crime = crime;
            titleTextView.setText(this.crime.getTitle());

            SimpleDateFormat dt = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault());
            dateTextView.setText(dt.format(this.crime.getDate()));

            if (crime.isSolved()){
                solvedImageView.setVisibility(View.VISIBLE);
            } else {
                solvedImageView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            callbacks.onCrimeSelected(crime.id);
        }
    }

    class CrimeAdapter extends ListAdapter<Crime, CrimeHolder> {

        protected CrimeAdapter(@NonNull DiffUtil.ItemCallback<Crime> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public CrimeListFragment.CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_police_required, parent, false);
            }
            else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_crime, parent, false);
            }

            return new CrimeListFragment.CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeListFragment.CrimeHolder holder, int position) {
           Crime currentCrime = getCurrentList().get(position);
            try {
                holder.Bind(currentCrime);
                String s = CrimeUtils.getCrimeReport(currentCrime, requireContext());
                holder.itemView.setContentDescription(CrimeUtils.getCrimeReport(currentCrime, requireContext()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (getCurrentList().get(position).RequiresPolice()){
                return 1;
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            return getCurrentList().size();
        }
    }
}