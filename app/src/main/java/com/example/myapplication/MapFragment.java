package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    final String KEY = "EVENTID";
    private GoogleMap map;
    private TextView firstLine;
    private TextView secondLine;
    private ImageView image;
    private Map<String, Model.Event> events;
    private Map<String, Integer> colorCode;
    private Map<String, Model.Person> people;
    //made view global
    private Datacache instance;
    private LinearLayout eventInfo;
    private String passPersonId = null;
    private boolean createMenu;
    private String givenEventId;
    private Event previousEvent = null;
    private final List<Polyline> lineList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onResume()
    {
        //get called when you return back to the activity
        super.onResume();
        if(map != null)
        {
            map.clear(); // clear map
            events = instance.getEvents(); //upadate events map
            if(events != null)
            {
                if(previousEvent != null)
                {
                    populateMap(previousEvent);//make the map again
                }
                else
                {
                    addMarker();
                }
            }
        }

        //onmapready function pull out the markers and lines into function
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //set the views
        firstLine = view.findViewById(R.id.mapExplain);
        secondLine = view.findViewById(R.id.mapExplainPart2);
        image = view.findViewById(R.id.image_pic);
        eventInfo = view.findViewById(R.id.eventInformation);
        //Get the datacache
        instance = Datacache.getInstance();
        events = instance.getEvents();
        colorCode = instance.getColorCoded();
        people = instance.getPeople();
        //reset bundle settings
        givenEventId = null;
        createMenu = true;
        //check if bundle exists
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            givenEventId = getArguments().getString(KEY);
            createMenu = false;
        }


        eventInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passPersonId != null)
                {
                    Toast.makeText(getActivity(), "To The person Activity!!!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), PersonActivity.class).putExtra("PERSONID", passPersonId));
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if(createMenu == true)
        {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.map_menu, menu);
            //create the icons
            Drawable settingIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                    .colorRes(R.color.white).sizeDp(20);
            Drawable searchIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.white).sizeDp(20);
            //get the menuitems
            MenuItem setting = menu.findItem(R.id.setting);
            MenuItem search = menu.findItem(R.id.search);
            //set the icons to the menu items
            search.setIcon(searchIcon);
            setting.setIcon(settingIcon);
            search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    Toast.makeText(getActivity(), "To The Search Activity!!!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), SearchActivity.class));
                    return true;
                }
            });
            setting.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    Toast.makeText(getActivity(), "To The Setting Activity!!!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), SettingsActivity.class));
                    return true;
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(givenEventId != null)
        {
            Event event = events.get(givenEventId);
            String personId = event.getPersonID();
            //set the person
            Person eventPerson = people.get(personId);
            //set the startpoint of event
            LatLng startPoint = new LatLng(event.getLatitude(),event.getLongitude());
            showEvent(event, eventPerson, startPoint);//show the events
            drawLines(personId, 30.0F, startPoint); //draw the lines
            previousEvent = event;
        }
        //add the markers
        addMarker();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //remove the polylines
                removeLines();
                //get the event
                Event event = (Event) marker.getTag();
                if(marker.getTag() != null){
                    //set the person
                    Person eventPerson = people.get(event.getPersonID());
                    //set the startpoint of event
                    LatLng startPoint = new LatLng(event.getLatitude(),event.getLongitude());
                    showEvent(event, eventPerson, startPoint);//show the events
                    drawLines(eventPerson.getPersonID(), 30.0F, startPoint); //draw the lines
                    previousEvent = event;
                    return true;
                }
                else
                {
                    Toast.makeText(getActivity(), "Was Null", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });
        map.setOnMapLoadedCallback(this);

    }
    private void populateMap(Event event)
    {
        //remove the lines
        removeLines();
        String personId = event.getPersonID();
        //set the startpoint of event
        LatLng startPoint = new LatLng(event.getLatitude(),event.getLongitude());
        addMarker();
        drawLines(personId, 30.0F, startPoint); //draw the lines
    }
    private void drawLines(String personId, float width, LatLng startPoint)
    {
        String motherID = instance.findPerson(personId).getMotherID();
        String fatherID = instance.findPerson(personId).getFatherID();
        drawFamilyTreeLines(motherID, width - 5, startPoint); // mother sides family tree
        drawFamilyTreeLines(fatherID, width - 5, startPoint); // father sides family tree
        List<Event> eventList = instance.getPersonLifeEvents(personId);
        drawLifeLine(eventList, width);
        drawSpouseLine(personId, width, startPoint);
    }
    private void drawLifeLine(List<Event> eventList, float width)
    {
        if(eventList != null)
        {
            for(int i = 0; i < eventList.size() - 1; i++)
            {
                Event firstEvent = eventList.get(i);
                Event secondEvent = eventList.get(i + 1);
                //create the line
                if(width <= 0) {width = 1;}
                LatLng startPoint = new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude());
                LatLng endPoint = new LatLng(secondEvent.getLatitude(), secondEvent.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(startPoint).add(endPoint).color(Color.RED)
                        .width(width);
                Polyline line = map.addPolyline(options);
                lineList.add(line);
                width -= 8;
            }
        }
    }
    private boolean drawFamilyTreeLines(String personId, float width, LatLng startPoint)
    {
        Person person = instance.findPerson(personId);
        //get the person,event list

        List<Event> eventList = instance.getFamilyPersonEvents(personId);
        if((person == null) ||(eventList == null))
        {
            return true;
        }
        //get earliest event
        Event earliestEvent = eventList.get(0);
        //set the endpoint to be for the earliest event
        LatLng endPoint = new LatLng(earliestEvent.getLatitude(),earliestEvent.getLongitude());
        //set width if it is less than or equal to zero to be 1
        if(width <= 0) {width = 1;}
        //create polyLine
        PolylineOptions options = new PolylineOptions()
                .add(startPoint).add(endPoint).color(Color.BLUE)
                .width(width);
        //add the polyline to the map
        Polyline line = map.addPolyline(options);
        //add lines to the list
        lineList.add(line);
        drawFamilyTreeLines(person.getFatherID(), (width - 10), endPoint);
        drawFamilyTreeLines(person.getMotherID(), (width - 10), endPoint);
        return true;
    }
    private void drawSpouseLine(String personId, float width, LatLng startPoint)
    {
        Person person = instance.findPerson(personId);
        Person spouse = instance.findPerson(person.getSpouseID());
        if(spouse != null)//person exist
        {
            List<Event> spouseEvents = instance.getSpouseEvents(person.getSpouseID());
            if(spouseEvents != null)//person have events
            {
                Event firstEvent = spouseEvents.get(0);
                LatLng endPoint = new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(startPoint).add(endPoint).color(Color.GREEN)
                        .width(width);
                Polyline line = map.addPolyline(options);
                lineList.add(line);
            }
        }

    }
    private void removeLines()
    {
        for(int i = 0; i < lineList.size(); i++)
        {
            //remove from the list and clear the map
            lineList.get(i).remove();
        }
    }
    private void addMarker()
    {
        if(events != null)
        {
            // using for-each loop for iteration over Map.entrySet()
            for (Event event : events.values()){
                //we add a marker in the location of the given event
                // and set the color based on the event type
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(event.getLatitude(), event.getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(colorCode.get(event.getEventType()))));
                marker.setTag(event);
            }
        }
    }
    private void showEvent(Event event, Person eventPerson, LatLng startPoint)
    {
        map.animateCamera(CameraUpdateFactory.newLatLng(startPoint));//move camera to center on the event clicked
        //firstLine output is the full name for the event person and the event type
        String firstLineOutput = eventPerson.getFirstName() + " "
                + eventPerson.getLastName() + " " + event.getEventType() + ":";
        //secondLine output is the country and the city and year of the event
        String secondLineOutput = event.getCity() + ", " +
                event.getCountry() + " (" + event.getYear() + ")";
        //set the text here
        firstLine.setText(firstLineOutput);
        secondLine.setText(secondLineOutput);
        if(eventPerson.getGender().equals("m"))
        {
            image.setImageResource(R.drawable.male);
        }
        else
        {
            image.setImageResource(R.drawable.female);
        }
        passPersonId = eventPerson.getPersonID();
    }
    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }
}

