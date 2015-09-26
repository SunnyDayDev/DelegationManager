package com.development.sunnyday.sample;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.development.sunnyday.delegationmanager.Delegate;
import com.development.sunnyday.delegationmanager.DelegationInterface;
import com.development.sunnyday.delegationmanager.DelegationManager;

/**
 * A placeholder fragment containing a simple view.
 */
@Delegate({View.OnClickListener.class})
public class MainActivityFragment extends Fragment implements View.OnClickListener{

    public MainActivityFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DelegationManager.delegateToActivity((Activity)context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onClick(View v) {

    }
}
