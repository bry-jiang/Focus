package com.example.bryanjiang.focus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterListFragment extends Fragment { //This is just going to be a blank fragment to show the mainactivity underneath
    //The main activity will act as the masterlist activity instead because it already has the functionality

    public MasterListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_master_list, container, false);
    }
}
