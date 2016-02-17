package com.android.jhansi.designchallenge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class GreetingFragment extends Fragment {

    private static final String TAG = GreetingFragment.class.getSimpleName();
    private Tracker mTracker;

    public GreetingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Greetings screen name: " + TAG);
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Greetings")
                .setAction("View")
                .build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greeting, container, false);
    }

}
