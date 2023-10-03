package com.example.myapplication;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Model.Event;
import Model.Person;
import com.example.myapplication.Pair;

public class Datacache {
    //constants
    final int colorSize = 9;
    String rootUserId = null;
    //person id and person
    Map<String, Model.Person> people = new HashMap<>();
    //List of personID that are males
    List<String> malePeople = new ArrayList<>();
    //List of personID that are females
    List<String> femalePeople = new ArrayList<>();
    //eventid and event
    Map<String, Model.Event> events = new HashMap<>();
    //event id and event
    Map<String, Model.Event> temp_events = new HashMap<>();
    //map for colors(eventType, color)
    Map<String, Integer> colorCoded = new HashMap<>();
    //map String is relation and Personid is the person
    List<Pair<String, String>> relation = new ArrayList<>();
    //persondID
    List<String> paternalAncestor = new ArrayList<>();
    //PersonID
    List<String> maternalAncestor = new ArrayList<>();
    final Float[] colors ={BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN,BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW};
    boolean isLifeLine = true;
    boolean isFamilyTree = true;
    boolean isSpouseLine = true;
    boolean isFatherSide = true;
    boolean isMotherSide = true;
    boolean isMaleSwitch = true;
    boolean isFemaleSwitch = true;
    private static Datacache instance = new Datacache();
    public static Datacache getInstance()
    {
        return instance;
    }
    public void setRootUser(String rootUserId) {
        this.rootUserId = rootUserId;
    }


    //setting the people map and getting it
    public Map<String, Model.Person> getPeople() {
        return people;
    }
    public void setPeople(Model.Person[] personList) {
        for (int i = 0; i < personList.length; i++)
        {
            Person temp_person = personList[i];
            String personId = temp_person.getPersonID();
            people.put(personId, temp_person);
            if(temp_person.getGender().equals("m"))
            {
                malePeople.add(personId);
            }
            else
            {
                femalePeople.add(personId);
            }
        }
        Person rootPerson = findPerson(rootUserId);
        setMaternalAncestor(rootPerson.getMotherID());
        setPaternalAncestor(rootPerson.getFatherID());
    }


    //setting the events and getting the events
    public void setEvents(Model.Event[] eventList) {
        for (int i = 0; i < eventList.length; i++)
        {
            events.put(eventList[i].getEventID(), eventList[i]);
        }
    }
    public Map<String, Model.Event> getEvents() {
        temp_events = events;
        if(!isMaleSwitch)
        {
            maleFilterEvents();
        }
        if(!isFemaleSwitch)
        {
            femaleFilterEvents();
        }
        if(!isFatherSide)
        {
            fatherSideFilterEvents();
        }
        if(!isMotherSide)
        {
            motherSideFilterEvents();
        }
        if(temp_events == null)
        {
            return null;
        }
        return temp_events;
    }


    //setting the color and getting the colorcode
    public Map<String, Integer> getColorCoded() {
        return colorCoded;
    }
    public void setColorCoded(Model.Event[] eventList) {

        for (int i = 0; i < eventList.length; i++)
        {
            float color = colors[(i % colorSize)];
            //random number generate
            colorCoded.put(eventList[i].getEventType(), (int) color );
        }

        //these will always be set to be the same colors
        colorCoded.put("birth", (int)BitmapDescriptorFactory.HUE_BLUE);
        colorCoded.put("death", (int)BitmapDescriptorFactory.HUE_RED);
        colorCoded.put("marriage", (int)BitmapDescriptorFactory.HUE_GREEN);
    }


    //get and set relations
    public List<Pair<String,String>> getRelation() {
        return relation;
    }
    public void setRelation(String personId) {
        //determines the relationship of a person finding there father mother spouse and children
        //clear the relation and reset
        relation.clear();
        Person person = findPerson(personId);
        if(person == null)
        {
            relation = null;
        }
        else
        {
            String motherId = person.getMotherID();
            String fatherId = person.getFatherID();
            String spouseId = person.getSpouseID();
            if(fatherId != null)
            {
                relation.add(new Pair<>("Father", fatherId));
                //relation.add(new Pair<String, String>("Father", fatherId));
            }
            if(motherId != null)
            {
                relation.add(new Pair<>("Mother", motherId));
            }
            if(spouseId != null)
            {
                relation.add(new Pair<>("Spouse", spouseId));
            }
            for (Person tempPerson : people.values())
            {
                if((tempPerson.getFatherID() != null))
                {
                    if(tempPerson.getFatherID().equals(personId))
                    {
                        relation.add(new Pair<>("Child", tempPerson.getPersonID()));
                    }
                }
                if(tempPerson.getMotherID() != null)
                {
                    if(tempPerson.getMotherID().equals(personId))
                    {
                        relation.add(new Pair<>("Child", tempPerson.getPersonID()));
                    }
                }

            }

        }
    }

    //get and set settings
    public boolean[] getSettings()
    {
        boolean[] output = new boolean[7];
        output[0] = isLifeLine;
        output[1] = isFamilyTree;
        output[2] = isSpouseLine;
        output[3] = isFatherSide;
        output[4] = isMotherSide;
        output[5] = isMaleSwitch;
        output[6] = isFemaleSwitch;
        return output;
    }
    public void setSettings(boolean[] settings) {

        isLifeLine = settings[0];
        isFamilyTree = settings[1];
        isSpouseLine = settings[2];
        isFatherSide = settings[3];
        isMotherSide = settings[4];
        isMaleSwitch = settings[5];
        isFemaleSwitch = settings[6];
    }

    //setting filters
    public void setFatherSide(boolean fatherSide) {
        isFatherSide = fatherSide;
    }

    public void setMotherSide(boolean motherSide) {
        isMotherSide = motherSide;
    }

    public void setMaleSwitch(boolean maleSwitch) {
        isMaleSwitch = maleSwitch;
    }

    public void setFemaleSwitch(boolean femaleSwitch) {
        isFemaleSwitch = femaleSwitch;
    }
    public List<Model.Event> getFamilyPersonEvents(String personID)
    {
        //life events true or not settings
        if(isFamilyTree)
        {
            return getPersonEvents(personID);
        }
        else
        {
            return null;
        }

    }
    public List<Model.Event> getSpouseEvents(String personID)
    {
        //life events true or not settings
        if(isSpouseLine)
        {
            return getPersonEvents(personID);
        }
        else
        {
            return null;
        }

    }
    public List<Model.Event> getPersonLifeEvents(String personID)
    {
        //life events true or not settings
        if(isLifeLine)
        {
            return getPersonEvents(personID);
        }
        else
        {
            return null;
        }

    }
    public void maleFilterEvents()
    {
        Map<String, Model.Event> replaceEvents = new HashMap<>();
        for(Event event : temp_events.values())
        {
            String personID = event.getPersonID();
            if(malePeople.contains(personID))
            {
            }
            else
            {
                replaceEvents.put(event.getEventID(), event);
            }
        }
        temp_events = replaceEvents;
    }
    public void femaleFilterEvents()
    {
        Map<String, Model.Event> replaceEvents = new HashMap<>();
        for(Event event : temp_events.values())
        {
            String personID = event.getPersonID();
            if(femalePeople.contains(personID))
            {
            }
            else
            {
                replaceEvents.put(event.getEventID(), event);
            }
        }
        temp_events = replaceEvents;
    }
    public void fatherSideFilterEvents()
    {
        Map<String, Model.Event> replaceEvents = new HashMap<>();
        for(Event event : temp_events.values())
        {
            String personID = event.getPersonID();
            if(paternalAncestor.contains(personID))
            {
            }
            else
            {
                replaceEvents.put(event.getEventID(), event);
            }
        }
        temp_events = replaceEvents;
    }
    public void motherSideFilterEvents()
    {
        Map<String, Model.Event> replaceEvents = new HashMap<>();
        for(Event event : temp_events.values())
        {
            String personID = event.getPersonID();
            if(maternalAncestor.contains(personID))
            {
            }
            else
            {
                replaceEvents.put(event.getEventID(), event);
            }
        }
        temp_events = replaceEvents;
    }

    public boolean setMaternalAncestor(String personId) {
        if(personId == null)
        {
            return true;
        }
        Person temp = findPerson(personId);
        maternalAncestor.add(personId);
        setMaternalAncestor(temp.getFatherID());
        setMaternalAncestor(temp.getMotherID());
        return true;
    }

    public boolean setPaternalAncestor(String personId) {
        if(personId == null)
        {
            return true;
        }
        Person temp = findPerson(personId);
        paternalAncestor.add(personId);
        setPaternalAncestor(temp.getFatherID());
        setPaternalAncestor(temp.getMotherID());
        return true;
    }


    //find person in people
    public Model.Person findPerson(String personId)
    {
        if (people.containsKey(personId)) {
            return people.get(personId);
        }
        else
        {
            return null;
        }
    }


    //get persons events
    public List<Model.Event> getPersonEvents(String personID)
    {
        List<Model.Event> output = new ArrayList<>();
        for (Event event : temp_events.values())
        {
            if(event.getPersonID().equals(personID))
            {
                output.add(event);
            }
        }
        if(output.size() == 0)
        {
            return null;
        }
        else
        {
            output.sort(new sortByYear());
            return output;
        }
    }

    //search event and people for search activity
    public List<Event> searchEvents(String search)
    {
        search = search.toLowerCase();
        List<Event> output = new ArrayList<>();
        for (Event event : temp_events.values())
        {
            Integer year = event.getYear();
            //check for countries cities eventtype years if any contain it then add it to the list
            if((event.getCountry().toLowerCase().contains(search)) || (event.getCity().toLowerCase().contains(search))
                    || (event.getEventType().toLowerCase().contains(search))
                    || (year.toString().contains(search)))
            {
                output.add(event);
            }
        }

        return output ;
    }
    public List<Person> searchPeople(String search)
    {
        search = search.toLowerCase();
        List<Person> output = new ArrayList<>();
        for(Person person : people.values())
        {
            //check for first name and last name
            if((person.getFirstName().toLowerCase().contains(search)) || (person.getLastName().toLowerCase().contains(search)))
            {
                output.add(person);
            }
        }
        return output;
    }


    //clear all datacache
    public void clearAll()
    {

        people.clear();
        malePeople.clear();
        femalePeople.clear();
        events.clear();
        temp_events.clear();
        colorCoded.clear();
        if(relation != null)
        {
            relation.clear();
        }
        paternalAncestor.clear();
        maternalAncestor.clear();
    }
    class sortByYear implements Comparator<Event>
    {

        @Override
        public int compare(Event event, Event t1) {
            return event.getYear() - t1.getYear();
        }
    }
}
