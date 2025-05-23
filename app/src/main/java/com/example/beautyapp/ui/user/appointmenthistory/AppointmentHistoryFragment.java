package com.example.beautyapp.ui.user.appointmenthistory;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.AppointmentHistoryAdapter;
import com.example.beautyapp.model.Booking;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AppointmentHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentHistoryAdapter adapter;
    private List<Booking> lichHenList = new ArrayList<>();
    private AppointmentHistoryViewModel viewModel;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAppointmentHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppointmentHistoryAdapter(getContext(), lichHenList);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AppointmentHistoryViewModel.class);
        userId = FirebaseAuth.getInstance().getUid();

        viewModel.getAppointmentList().observe(getViewLifecycleOwner(), list -> {
            lichHenList.clear();
            lichHenList.addAll(list);
            adapter.notifyDataSetChanged();
        });

        viewModel.fetchAppointmentList(userId);

        return view;
    }

}