package com.locus.locusassignment;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.locus.locusassignment.model.Item;
import com.locus.locusassignment.model.ItemList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

public class DataLoaderTask extends AsyncTask<Void, Void, List<Item>> {

    private WeakReference<DataLoaderTaskListener> refListener;

    public DataLoaderTask(DataLoaderTaskListener listener) {
        this.refListener = new WeakReference<>(listener);
    }

    @Override
    protected List<Item> doInBackground(Void... voids) {
        if (refListener.get() == null) {
            return null;
        }
        Context context = refListener.get().getContext();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("data.json")));
//            String mLine;
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((mLine = reader.readLine()) != null) {
//                stringBuilder.append(mLine);
//            }
            return new Gson().fromJson(reader, ItemList.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        if (refListener.get() == null) {
            return;
        }
        refListener.get().onDataLoaded(items);
    }
}
