package se.ESNBTH.esnbth.Fragments;


import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.ESNBTH.esnbth.ListView.CustomAdapter;
import se.ESNBTH.esnbth.ListView.RowItem;
import se.ESNBTH.esnbth.R;

public class Fragment_Timetables extends Fragment implements AdapterView.OnItemClickListener {

    String[] shop_names;
    TypedArray shop_pics;
    String[] localisation;

    List<RowItem> rowItems;
    ListView mylistview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timetables, container, false);

        rowItems = new ArrayList<RowItem>();

        shop_names = getResources().getStringArray(R.array.ShopsName);

        shop_pics = getResources().obtainTypedArray(R.array.ShopsPicture);

        localisation = getResources().getStringArray(R.array.ShopsLocalisation);


        for (int i = 0; i < shop_names.length; i++) {
            RowItem item = new RowItem(shop_names[i],
                    shop_pics.getResourceId(i, -1),
                    localisation[i]);
            rowItems.add(item);
        }

        mylistview = (ListView) rootView.findViewById(R.id.list);
        CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), rowItems);
        mylistview.setAdapter(adapter);
        shop_pics.recycle();
        mylistview.setOnItemClickListener(this);


        return rootView;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String member_name = rowItems.get(position).getShop_name();
        Toast.makeText(getActivity().getApplicationContext(), "" + member_name,
                Toast.LENGTH_SHORT).show();
    }

}