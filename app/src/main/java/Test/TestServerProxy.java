package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.myapplication.Datacache;
import com.example.myapplication.ServerProxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Model.Person;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import Response.RegisterResponse;

public class TestServerProxy {
    RegisterRequest registerRequest = new RegisterRequest("TEQ", "pass", "hi", "t", "p", "m");
    LoginRequest loginRequest;
    String ServerHost = "localhost";
    String ServerPort = "8080";
    ServerProxy server;
    @Before
    public void setup()
    {
        server = new ServerProxy();
        registerRequest = new RegisterRequest("TEQ", "pass", "hi", "t", "p", "m");
        loginRequest = new LoginRequest("TEQ", "pass" );
        ServerHost = "localhost";
        ServerPort = "8080";

    }


    //register tests
    //successful register test
    @Test
    public void successRegister()
    {
        RegisterResponse registerResult = server.Register(registerRequest, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
    }
    //failed register due to the registering an already registered user
    @Test
    //same user
    public void failRegister()
    {
        RegisterResponse registerResult = server.Register(registerRequest, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        RegisterResponse registerResult2 = server.Register(registerRequest, ServerHost, ServerPort);
        assertFalse(registerResult2.isSuccess());
    }


    //login tests
    //successful login
    @Test
    public void successLogin()
    {
        //register the user
        RegisterResponse registerResult = server.Register(registerRequest, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        //log the user in
        LoginResponse loginResult = server.Login(loginRequest, ServerHost, ServerPort);
        assertTrue(loginResult.isSuccess());
        Datacache instance = Datacache.getInstance();
        Person[] person = server.getFamily(ServerHost,ServerPort, loginResult.getAuthtoken()).getData();
        instance.setRootUser(loginResult.getPersonID());
        instance.setPeople(person);
        assertEquals(31,instance.getPeople().size());
    }
    //failed login due to wrong password
    @Test
    //wrong password
    public void failPassLogin()
    {
        //register user
        RegisterResponse registerResult = server.Register(registerRequest, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        //assume the previous test builds on each other
        LoginRequest badRequest = new LoginRequest("TEQ", "wrongPass" );
        LoginResponse loginResult = server.Login(badRequest, ServerHost, ServerPort);
        assertFalse(loginResult.isSuccess());
    }
    //wrong userName
    @Test
    public void failUserLogin()
    {
        //register user
        RegisterResponse registerResult = server.Register(registerRequest, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        //assume the previous test builds on each other
        LoginRequest badRequest = new LoginRequest("wrongUser", "pass" );
        LoginResponse loginResult = server.Login(badRequest, ServerHost, ServerPort);
        assertFalse(loginResult.isSuccess());
    }


    //test for get family method(Retreving the related people)
    //successfully getting the family
    @Test
    public void successGetFamily()
    {
        RegisterRequest newUser = new RegisterRequest("Q", "pass", "haf", "DR", "Q", "m");
        RegisterResponse registerResult = server.Register(newUser, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        PersonResponse result = server.getFamily(ServerHost, ServerPort, registerResult.getAuthtoken());
        assertTrue(result.isSuccess());
        Person[] familyTree = result.getData();
        assertEquals(31,familyTree.length);
    }
    //failing to get the family due to bad authtoken
    @Test
    public void badAuthGetFamily()
    {
        RegisterRequest newUser = new RegisterRequest("Q", "pass", "haf", "DR", "Q", "m");
        RegisterResponse registerResult = server.Register(newUser, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        PersonResponse result = server.getFamily(ServerHost, ServerPort, "hagkjadkgja");
        assertFalse(result.isSuccess());
    }


    //get event method tests(retreving the events related to the person)
    //getting event is successful
    @Test
    public void successGetEvents()
    {
        //register a new user
        RegisterRequest newUser = new RegisterRequest("Killua", "pass", "Killu", "Killua", "Zoldick", "m");
        RegisterResponse registerResult = server.Register(newUser, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        EventResponse result = server.getEvents(ServerHost,ServerPort,registerResult.getAuthtoken());
        assertTrue(result.isSuccess());
    }
    //failed get events due to bad auth token
    @Test
    public void badAuthGetEvents()
    {
        RegisterRequest newUser = new RegisterRequest("Killua", "pass", "Killu", "Killua", "Zoldick", "m");
        RegisterResponse registerResult = server.Register(newUser, ServerHost, ServerPort);
        assertTrue(registerResult.isSuccess());
        EventResponse result = server.getEvents(ServerHost,ServerPort,"afhdasiodjfioasjab");
        assertFalse(result.isSuccess());
    }


    @After
    public void tearDown()
    {
        server.Clear(ServerHost,ServerPort);
    }

}
