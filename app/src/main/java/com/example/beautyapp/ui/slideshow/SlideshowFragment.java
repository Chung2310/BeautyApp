package com.example.beautyapp.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.beautyapp.adapter.ConsultantAdapter;
import com.example.beautyapp.databinding.FragmentSlideshowBinding;
import com.example.beautyapp.model.Consultant;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private SlideshowViewModel slideshowViewModel;
    private ConsultantAdapter consultantAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        // Khởi tạo adapter với danh sách rỗng
        consultantAdapter = new ConsultantAdapter(new ArrayList<>());
        binding.rvDanhSachBacSi.setHasFixedSize(true);
        binding.rvDanhSachBacSi.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDanhSachBacSi.setAdapter(consultantAdapter);



        // Quan sát danh sách tư vấn viên
        slideshowViewModel.getConsultantList().observe(getViewLifecycleOwner(), consultants -> {
            if (consultants != null && !consultants.isEmpty()) {
                consultantAdapter.setConsultantList(consultants);

            } else {

            }
            Log.d("SlideshowFragment", "Danh sách tư vấn viên cập nhật, kích thước: " + consultants.size());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}