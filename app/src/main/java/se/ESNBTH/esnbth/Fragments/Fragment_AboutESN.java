package se.ESNBTH.esnbth.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.ESNBTH.esnbth.R;

public class Fragment_AboutESN extends Fragment {


    public Fragment_AboutESN(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_karlskrona, container, false);
        //TODO: CHANGE TO FRAGMENT ESN



        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
    }


}
