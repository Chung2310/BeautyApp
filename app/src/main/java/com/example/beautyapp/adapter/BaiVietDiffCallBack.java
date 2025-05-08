package com.example.beautyapp.adapter;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.model.BaiViet;

import java.util.List;

public class BaiVietDiffCallBack extends DiffUtil.Callback {
    private final List<BaiViet> oldLists;
    private final List<BaiViet> newLists;

    public BaiVietDiffCallBack(List<BaiViet> oldLists, List<BaiViet> newLists) {
        this.oldLists = oldLists;
        this.newLists = newLists;
    }

    @Override
    public int getOldListSize() {
        return oldLists.size();
    }

    @Override
    public int getNewListSize() {
        return newLists.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldLists.get(oldItemPosition).getId() == newLists.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldLists.get(oldItemPosition).equals(newLists.get(newItemPosition));
    }
}
