package se.ESNBTH.esnbth.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.ESNBTH.esnbth.R;
import se.ESNBTH.esnbth.RequestHelper.AppConst;
import se.ESNBTH.esnbth.RequestHelper.Event;

public class Fragment_Home extends Fragment {

    private static final String TAG = Fragment_Home.class.getSimpleName();

    private Button btnNews;
    private Button btnEvents;
    private Button btnAboutKarlskrona;
    private Button btnAboutUs;
    private Button btnVisitUs;

    private UiLifecycleHelper uiHelper;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    List<Event> events = new ArrayList<>();
    private int cont;
    private ArrayList<Event> imgEvents = new ArrayList<>();
    private ArrayList<Event> infoEvents = new ArrayList<>();

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    public Fragment_Home(){}


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (Session.getActiveSession().isOpened()){
            Log.i(TAG, "ESTOY DENTRO");
        }else{
            Log.i(TAG,"ESTOY FUERA");
        }

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Log.i("DATE",AppConst.getSinceTime());


        btnNews = (Button) rootView.findViewById(R.id.btnNews);
        btnEvents = (Button) rootView.findViewById(R.id.btnEvents);
        btnAboutKarlskrona = (Button) rootView.findViewById(R.id.btnAboutKarlskrona);
        btnAboutUs = (Button) rootView.findViewById(R.id.btnAboutUs);
        btnVisitUs = (Button) rootView.findViewById(R.id.btnVisitUs);




        btnAboutKarlskrona.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View v, MotionEvent event){

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    Fragment_Karlskrona newFragment = new Fragment_Karlskrona();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.frame_container, newFragment);
                    transaction.commit();


                   /* fragmentManager = getFragmentManager();
                    fragment = new Fragment_Karlskrona();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
                    return true;
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume(){
        super.onResume();

        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            btnAboutUs.setVisibility(View.VISIBLE);

            doBatchRequest();
            requestAllEvents();


            // Request user data and show the results
            /*Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        btnAboutUs.setText(buildUserInfoDisplay(user));
                    }
                }
            });*/
        } else if (state.isClosed()) {
            btnAboutUs.setVisibility(View.INVISIBLE);

        }
    }

    private void doBatchRequest() {

        String[] requestIds = {"me", "1390607591232311"};

        RequestBatch requestBatch = new RequestBatch();
        for (final String requestId : requestIds) {
            requestBatch.add(new Request(Session.getActiveSession(),
                    requestId, null, null, new Request.Callback() {
                public void onCompleted(Response response) {
                    GraphObject graphObject = response.getGraphObject();
                    String s = btnAboutUs.getText().toString();
                    if (graphObject != null) {
                        if (graphObject.getProperty("id") != null) {
                            s = s + String.format("%s: %s\n",
                                    graphObject.getProperty("id"),
                                    graphObject.getProperty("name"));
                        }
                    }
                    btnAboutUs.setText(s);
                }
            }));
        }
        requestBatch.executeAsync();


    }

    /**
     * Update the event array with last 100 events.
     */
    private void requestAllEvents(){
        String EVENTS = "events";
        Bundle bun = new Bundle();
        //bun.putString("fields","id,name,description,location,start_time");
        bun.putString("fields","id");
        bun.putString("since",AppConst.getSinceTime());
        bun.putInt("limit",100);
        //PAGEID/Events
        String requestStuff = AppConst.Facebook_PageName+EVENTS;
        new Request(Session.getActiveSession(),requestStuff,bun, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.i(TAG,response.toString());
                events = new ArrayList<>();
                GraphObject jRequest = response.getGraphObject();
                JSONArray jEvents = (JSONArray) jRequest.getProperty("data");
                if (jEvents != null) {
                    for (int i = 0; i < jEvents.length(); i++) {
                        JSONObject item = null;
                        try {
                            item = (JSONObject) jEvents.get(i);
                            Event event = new Event();
                            event.setId(item.getString(AppConst.ID_KEY));
                            //event.setName(item.getString(AppConst.NAME_KEY));
                            //event.setDescription(item.getString(AppConst.DESCRIPTION_KEY));
                            //event.setLocation(item.getString(AppConst.DESCRIPTION_KEY));
                            //event.setStartTime(item.getString(AppConst.START_TIME_KEY));

                            String message = (String) item.get("id");
                            Log.i(TAG + ".ITEM " + i, message);
                            events.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //Get the data of the events
                batchRequestImages(events);
                //Get the Images of the events
                batchRequestEvents(events);


                //TODO:SET ADAPTER FOR THE LISTVIEW
                //TODO:IF EVENTS NOT WORKS TRY WITH FEED LINKS.

            }
        }).executeAsync();

    }

    /**
     * Request a single event from the server
     * @param eventID Current id of the event.
     */
    private void requestEvent(String eventID){

        Bundle bun = new Bundle();
        bun.putString("fields","id,name,description,location,start_time");
        new Request(Session.getActiveSession(),eventID,bun, null, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if(response!= null){
                    Log.i(TAG,response.toString());
                    GraphObject gEvent = response.getGraphObject();
                    //Here you will receive the response that we can change depend on what we want.
                    //Just use response.getProperty(KEY);
                    //The Keys are in the APPCONST.
                }

            }
        }).executeAsync();

    }

    /**
     * Update photo of selected event
     * @param eventID current id of the event.
     */
    private void requestEventImage(String eventID){

        String IMAGE = "/photos";
        String requestID = eventID + IMAGE;
        Bundle imageBun = new Bundle();
        imageBun.putString("fields", "images");
        new Request(Session.getActiveSession(), requestID + "/photos", imageBun, null, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                //Test if we don't have an empty response
                if (response != null) {
                    //Get the graphObject
                    GraphObject graphObject = response.getGraphObject();
                    //Get the data from the graphObject
                    JSONArray jsonArray = (JSONArray) graphObject.getProperty("data");
                    try {
                        //Get the first JsonObject of data that is an JSONArray
                        JSONObject imgArray = (JSONObject) jsonArray.get(0);
                        jsonArray = (JSONArray) imgArray.get("images");
                        //Get the first JsonObject of images.
                        JSONObject imgItem = (JSONObject) jsonArray.get(0);
                        Event item = new Event();
                        item.setImgUrl(imgItem.getString(AppConst.IMAGE_SOURCE_KEY));
                        imgEvents.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).executeAsync();

    }

    /**
     * Update the array just with the event of the next week
     */
    private void requestFurtherWeekEvents(){
        String EVENTS = "events";
        Bundle bun = new Bundle();
        bun.putString("fields","id");
        bun.putInt("limit",100);
        //PAGEID/Events
        String requestStuff = AppConst.Facebook_PageName+EVENTS;
        new Request(Session.getActiveSession(),requestStuff,bun, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.i(TAG,response.toString());
                events = new ArrayList<>();
                GraphObject jRequest = response.getGraphObject();
                JSONArray jEvents = (JSONArray) jRequest.getProperty("data");
                if (jEvents != null) {
                    for (int i = 0; i < jEvents.length(); i++) {
                        JSONObject item = null;
                        try {
                            item = (JSONObject) jEvents.get(i);
                            Event event = new Event();
                            event.setId(item.getString(AppConst.ID_KEY));
                            //event.setName(item.getString(AppConst.NAME_KEY));
                            //event.setDescription(item.getString(AppConst.DESCRIPTION_KEY));
                            //event.setLocation(item.getString(AppConst.DESCRIPTION_KEY));
                            //event.setStartTime(item.getString(AppConst.START_TIME_KEY));

                            String message = (String) item.get("id");
                            Log.i(TAG + ".ITEM " + i, message);
                            events.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //Get the data of the events
                batchRequestImages(events);
                //Get the Images of the events
                batchRequestEvents(events);


                //TODO:SET ADAPTER FOR THE LISTVIEW
                //TODO:IF EVENTS NOT WORKS TRY WITH FEED LINKS.

            }
        }).executeAsync();
    }

    /**
     * Called internally by requestAllEvents it fetch data from the event as
     * Id, name,Description,location and start time
     * @param eventsArray Contains an array of Events with their IDs
     */
    private void batchRequestEvents(List<Event> eventsArray){
        final Session session = Session.getActiveSession();

        //clear the infoEvents
        infoEvents.clear();


        if(session != null && session.isOpened()){
            //Create a requestBatch
            RequestBatch requestBatch = new RequestBatch();


            //Request the information of the events and save it in @infoEvents
            for( int i = 0; i<eventsArray.size();i++){

                //Get de id of the current event
                String requestID = eventsArray.get(i).getId();

                //Create the request to the requestID
                Bundle bun = new Bundle();
                bun.putString("fields","id,name,description,location,start_time");
                requestBatch.add( new Request(session,requestID,bun,null,new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                       // Log.i(TAG,response.toString());
                        //Test if we don't have an empty response
                        if(response != null) {
                            GraphObject graphObject = response.getGraphObject();
                            Event item = new Event();
                            item.setId((String) graphObject.getProperty(AppConst.ID_KEY));
                            item.setName((String) graphObject.getProperty(AppConst.NAME_KEY));
                            item.setDescription((String) graphObject.getProperty(AppConst.DESCRIPTION_KEY));
                            item.setLocation((String) graphObject.getProperty(AppConst.DESCRIPTION_KEY));
                            item.setStartTime((String) graphObject.getProperty(AppConst.START_TIME_KEY));

                            //add the item to events
                            infoEvents.add(item);
                        }
                    }
                }));

            }


            requestBatch.addCallback(new RequestBatch.Callback() {
                @Override
                public void onBatchCompleted(RequestBatch batch) {
                   events = AppConst.mergeAllInfoEvents(events,infoEvents);
                    Log.i("HOLA", events.get(0).getDescription());
                }
            });

            requestBatch.executeAsync();


        }

    }

    /**
     * Called internally by requestAllEvents it fetch the image
     * that the event will use.
     * @param eventsArray Contains an array of Events with their IDs
     */
    private void batchRequestImages(List<Event> eventsArray){
        final Session session = Session.getActiveSession();
        //Clear the imgEvents
        imgEvents.clear();

        if(session != null && session.isOpened()) {
            //Create a requestBatch
            RequestBatch requestBatch = new RequestBatch();

            //Request the information of the events and save it in @infoEvents
            for (int i = 0; i < eventsArray.size(); i++) {

                //Get de id of the current event
                String requestID = eventsArray.get(i).getId();
               // Log.i(TAG, requestID);
                Bundle imageBun = new Bundle();
                imageBun.putString("fields", "images");
                requestBatch.add(new Request(session, requestID + "/photos", imageBun, null, new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        //Test if we don't have an empty response
                        if (response != null) {
                            //Get the graphObject
                            GraphObject graphObject = response.getGraphObject();
                            //Get the data from the graphObject
                            JSONArray jsonArray = (JSONArray) graphObject.getProperty("data");
                            try {
                                //Get the first JsonObject of data that is an JSONArray
                                JSONObject imgArray = (JSONObject) jsonArray.get(0);
                                jsonArray = (JSONArray) imgArray.get("images");
                                //Get the first JsonObject of images.
                                JSONObject imgItem = (JSONObject) jsonArray.get(0);
                                Event item = new Event();
                                item.setImgUrl(imgItem.getString(AppConst.IMAGE_SOURCE_KEY));
                                imgEvents.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }));
            }
            requestBatch.addCallback(new RequestBatch.Callback() {
                @Override
                public void onBatchCompleted(RequestBatch batch) {
                    events = AppConst.mergeAllImgEvents(events,imgEvents);

                    Log.i("HOLA", events.get(0).getImgUrl());
                }
            });

            requestBatch.executeAsync();
        }
    }





}

