package com.locus.locusassignment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.locus.locusassignment.model.Item;

import java.util.List;

public interface DataLoaderTaskListener {

    @NonNull Context getContext();

    void onDataLoaded(@Nullable List<Item> items);

}
