package com.zachschulze.elementaryupdate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zachschulze.elementaryupdate.LoginActivity;

/**
 * Created by Zach on 11/21/2014.
 */
public class LogoutFragment extends Fragment {
    public void onCreate (Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
