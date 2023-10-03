package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.example.myapplication.Pair;

import com.example.myapplication.Datacache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;
import Model.User;

public class TestDataCache {
    User rootUser = new User("sheila","parker",
            "sheila@parker.com","Sheila","Parker"
            ,"f","Sheila_Parker");
    Person rootPerson = new Person("Sheila_Parker", "sheila",
            "Sheila","Parker","f","Blaine_McGary",
            "Betty_White","Davis_Hyer");
    Person rootSpouse = new Person("Davis_Hyer","sheila", "Davis",
            "Hyer","m",null,null,"Sheila_Parker");
    Person rootFather = new Person("Blaine_McGary","sheila", "Blaine",
            "McGary","m","Ken_Rodham","Mrs_Rodham","Betty_White");
    Person rootMother = new Person("Betty_White","sheila", "Betty",
            "White","f",null,null,"Blaine_McGary");
    //these will be on the father sides of sheila parker
    Person rootGrandFather = new Person("Ken_Rodham","sheila", "Ken",
            "Rodham","m",null, null, "Mrs_Rodham");
    Person rootGrandMother = new Person("Mrs_Rodham","sheila","Mrs",
            "Rodham","f", null, null,"Ken_Rodham");
    Event sheilaBirth = new Event("Sheila_Birth","sheila", "Sheila_Parker",
            -36.1833f,144.9667f,"Australia","Melbourne","Sheila_Birth",1970);
    Event sheilaMarriage  = new Event("Sheila_Marriage", "sheila", "Sheila_Parker"
    ,34.0500f, -117.7500f, "United States", "Los Angeles", "marriage", 2012);
    Event sheilaAstroid1 = new Event("Sheila_Asteroids", "sheila", "Sheila_Parker",
            77.4667f, -68.7667f, "Denmark", "Qaanaaq", "completed asteroids",
            2014);
    Event SheilaAstroid2 = new Event("Other_Asteroids", "sheila", "Sheila_Parker",
            74.4667f, -60.7667f, "Denmark", "Qaanaaq", "COMPLETED ASTEROIDS",
            2014);
    Event sheilaDeath = new Event("Sheila_Death","sheila", "Sheila_Parker",
            40.2444f,111.6608f,"United States","Provo","death",2015);
    Event davisBirth = new Event("Davis_Birth","sheila","Davis_Hyer",
            41.7667f,140.7333f, "Japan", "Hakodate", "birth", 1970);
    Event blaineBirth = new Event("Blaine_Birth","sheila","Blaine_McGary",
            56.1167f,101.6000f, "Russia", "Bratsk", "birth", 1948);
    Event bettyDeath = new Event("Betty_Death","sheila", "Betty_White",
            52.4833f,-0.1000f,"United Kingdom","Birmingham","death",2017);
    Event kenGraduate = new Event("BYU_graduation","sheila", "Ken_Rodham",
            40.2338f,-111.6585f,"United States","Provo","Graduated from BYU",
            1879);
    Event mrsRodhamBackFlip = new Event("Mrs_Rodham_Backflip","sheila","Mrs_Rodham",
            32.6667f,-114.5333f,"Mexico","Mexicali","Did a backflip",
            1890);
    Datacache instance = Datacache.getInstance();
    String rootPersonId = "Sheila_Parker";
    int MAX_SIZE_EVENTS = 0;
    @Before
    public void setup() {
        instance.setRootUser(rootPersonId);
        instance.setFatherSide(true);
        instance.setMotherSide(true);
        instance.setMaleSwitch(true);
        instance.setFemaleSwitch(true);
        Person[] personArray = {rootPerson, rootSpouse, rootFather, rootMother, rootGrandFather, rootGrandMother};
        instance.setPeople(personArray);
        Event[] eventArray = {sheilaBirth, sheilaDeath, davisBirth, blaineBirth, bettyDeath, kenGraduate, mrsRodhamBackFlip, sheilaMarriage, sheilaAstroid1, SheilaAstroid2};
        instance.setEvents(eventArray);
        MAX_SIZE_EVENTS = instance.getEvents().size();

    }


    //test calculate family relationships
    //pass case
    @Test
    public void successSetRelations()
    {
        instance.setRelation(rootPersonId);
        List<Pair<String,String>> output = instance.getRelation();
        assertEquals("Blaine_McGary",output.get(0).getValue());//check if it contains Blaine
        assertEquals("Betty_White", output.get(1).getValue());//betty mother
        assertEquals("Davis_Hyer", output.get(2).getValue());//spouse
        assertEquals(3, output.size());
        //Test Father Blaine to see children
        instance.setRelation("Blaine_McGary");
        List<Pair<String,String>> outputFather = instance.getRelation();
        assertEquals("Ken_Rodham",output.get(0).getValue());//father is Ken Rodham
        assertEquals("Mrs_Rodham", output.get(1).getValue());//mother is MRS. Rodham
        assertEquals("Betty_White", output.get(2).getValue());//spouse
        assertEquals(rootPersonId, output.get(3).getValue());//sheila is child
        assertEquals(4, output.size());
    }
    //fail case  when passing a bad personId
    @Test
    public void badPersonIDSetRelations()
    {
        instance.setRelation("badPersonID");
        List<Pair<String,String>> output = instance.getRelation();
        assertEquals(null, output);
    }


    //filter events according to the filter settings
    //filter father side events
    @Test
    public void fatherSideFilter()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter Father Side
        instance.setFatherSide(false);
        Map<String, Event> output = instance.getEvents();
        assertEquals(7, output.size());
        assertTrue(output.containsKey("Sheila_Birth"));
        assertTrue(output.containsKey("Sheila_Death"));
        assertTrue(output.containsKey("Sheila_Marriage"));
        assertTrue(output.containsKey("Sheila_Asteroids"));
        assertTrue(output.containsKey("Other_Asteroids"));
        assertTrue(output.containsKey("Davis_Birth"));
        assertTrue(output.containsKey("Betty_Death"));
    }
    //filter motherside events
    @Test
    public void motherSideFilter()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter mother side
        instance.setMotherSide(false);
        Map<String, Event> output = instance.getEvents();
        assertEquals(9, output.size());
        assertTrue(output.containsKey("Sheila_Birth"));
        assertTrue(output.containsKey("Sheila_Death"));
        assertTrue(output.containsKey("Sheila_Marriage"));
        assertTrue(output.containsKey("Sheila_Asteroids"));
        assertTrue(output.containsKey("Other_Asteroids"));
        assertTrue(output.containsKey("Davis_Birth"));
        assertTrue(output.containsKey("Blaine_Birth"));
        assertTrue(output.containsKey("BYU_graduation"));
        assertTrue(output.containsKey("Mrs_Rodham_Backflip"));
    }
    //abnormal case where both mother and father side events are filtered
    @Test
    public void fatherAndMotherSideFilter()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter both sides
        instance.setMotherSide(false);
        instance.setFatherSide(false);
        Map<String, Event> output = instance.getEvents();
        assertEquals(6, output.size());
        assertTrue(output.containsKey("Sheila_Birth"));
        assertTrue(output.containsKey("Sheila_Death"));
        assertTrue(output.containsKey("Sheila_Marriage"));
        assertTrue(output.containsKey("Sheila_Asteroids"));
        assertTrue(output.containsKey("Other_Asteroids"));
        assertTrue(output.containsKey("Davis_Birth"));
    }
    //successfully filter Male events
    @Test
    public void maleEventFilterPass()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter male events
        instance.setMaleSwitch(false);
        Map<String, Event> output = instance.getEvents();
        assertEquals(7, output.size());
        assertTrue(output.containsKey("Sheila_Birth"));
        assertTrue(output.containsKey("Sheila_Death"));
        assertTrue(output.containsKey("Betty_Death"));
        assertTrue(output.containsKey("Sheila_Marriage"));
        assertTrue(output.containsKey("Sheila_Asteroids"));
        assertTrue(output.containsKey("Other_Asteroids"));
        assertTrue(output.containsKey("Mrs_Rodham_Backflip"));
    }
    //successfully filter female events
    @Test
    public void femaleEventFilterPass()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter female events
        instance.setFemaleSwitch(false);
        Map<String, Event> output = instance.getEvents();
        assertEquals(3, output.size());
        assertTrue(output.containsKey("Davis_Birth"));
        assertTrue(output.containsKey("Blaine_Birth"));
        assertTrue(output.containsKey("BYU_graduation"));
    }
    //abnormal case where we filter both male and female no events should exist
    @Test
    public void maleAndFemaleEventFilterPass()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter male and female events
        instance.setFemaleSwitch(false);
        instance.setMaleSwitch(false);
        Map<String, Event> output = instance.getEvents();
        assertEquals(0, output.size());
    }


    //chronologically sort a persons events
    //correctly sorts by events
    @Test
    public void correctSort()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        List<Event> output = instance.getPersonEvents(rootPersonId);
        int outputLastElement = output.size() - 1;
        assertEquals("Sheila_Birth", output.get(0).getEventID());
        assertEquals("Sheila_Marriage", output.get(1).getEventID());
        assertEquals("Sheila_Death", output.get(outputLastElement).getEventID());
    }
    //bad personID fail
    @Test
    public void badPersonIDSort()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        List<Event> output = instance.getPersonEvents("badPersonID");
        assertNull(output);
    }

    //correctly searches for people and events search activity
    //Search events with FatherSide filter on
    @Test
    public void fatherSideSearch()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter Father Side
        instance.setFatherSide(false);
        instance.getEvents();
        assertEquals(0, instance.searchEvents("Russia").size());//blaines birth place
        assertEquals(0, instance.searchEvents("Graduated from BYU").size());//Kens event
        assertEquals(0, instance.searchEvents("backflip").size());//MRS.Rodhams event
        assertEquals(1, instance.searchEvents("Birmingham").size());//bettys event
        assertEquals(1, instance.searchEvents("Japan").size());//hyers event

    }
    //filter motherside events and searches for events
    @Test
    public void motherSideSearch()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter motherside
        instance.setMotherSide(false);
        instance.getEvents();
        assertEquals(0, instance.searchEvents("Birmingham").size());//bettys event
        assertEquals(1, instance.searchEvents("backflip").size());//MRS.Rodhams event
        assertEquals(1, instance.searchEvents("Russia").size());//blaines birth place
        assertEquals(2, instance.searchEvents("asteroids").size());
        assertEquals(1, instance.searchEvents("Japan").size());//hyers event
    }
    //abnormal case where both mother and father side events are filtered
    @Test
    public void fatherAndMotherSideSearch()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter Father and mother Side
        instance.setFatherSide(false);
        instance.setMotherSide(false);
        instance.getEvents();
        assertEquals(0, instance.searchEvents("Russia").size());//blaines birth place
        assertEquals(0, instance.searchEvents("Graduated from BYU").size());//Kens event
        assertEquals(0, instance.searchEvents("backflip").size());//MRS.Rodhams event
        assertEquals(0, instance.searchEvents("Birmingham").size());//bettys event
        assertEquals(2, instance.searchEvents("asteroids").size());//sheilas event
        assertEquals(1, instance.searchEvents("Japan").size());//hyers event
    }
    //successfully filter Male events in search
    @Test
    public void maleEventSearch()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter maleEvents
        instance.setMaleSwitch(false);
        instance.getEvents();
        assertEquals(0, instance.searchEvents("Russia").size());//blaines birth place
        assertEquals(0, instance.searchEvents("Graduated from BYU").size());//Kens event
        assertEquals(1, instance.searchEvents("backflip").size());//MRS.Rodhams event
        assertEquals(1, instance.searchEvents("Birmingham").size());//bettys event
        assertEquals(2, instance.searchEvents("asteroids").size());//sheilas event
        assertEquals(0, instance.searchEvents("Japan").size());//hyers event
    }
    //successfully filter female events in search
    @Test
    public void femaleEventSearch()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter female events
        instance.setFemaleSwitch(false);
        instance.getEvents();
        assertEquals(1, instance.searchEvents("Russia").size());//blaines birth place
        assertEquals(1, instance.searchEvents("Graduated from BYU").size());//Kens event
        assertEquals(0, instance.searchEvents("backflip").size());//MRS.Rodhams event
        assertEquals(0, instance.searchEvents("Birmingham").size());//bettys event
        assertEquals(0, instance.searchEvents("asteroids").size());//sheilas event
        assertEquals(1, instance.searchEvents("Japan").size());//hyers event
    }
    //abnormal case where we filter both male and female no events should exist in search
    @Test
    public void maleAndFemaleEventSearch()
    {
        //check events size to be the whole size
        assertEquals(MAX_SIZE_EVENTS, instance.getEvents().size());
        //filter male and female events
        instance.setFemaleSwitch(false);
        instance.setMaleSwitch(false);
        instance.getEvents();
        assertEquals(0, instance.searchEvents("Russia").size());//blaines birth place
        assertEquals(0, instance.searchEvents("Graduated from BYU").size());//Kens event
        assertEquals(0, instance.searchEvents("backflip").size());//MRS.Rodhams event
        assertEquals(0, instance.searchEvents("Birmingham").size());//bettys event
        assertEquals(0, instance.searchEvents("asteroids").size());//sheilas event
        assertEquals(0, instance.searchEvents("Japan").size());//hyers event
        assertEquals(0, instance.searchEvents("a").size());//any event that has a a
    }
    @After
    public void tearDown() {
        instance.clearAll();
    }
}
