package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.Person;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import Response.RegisterResponse;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private Listener listener;
    EditText serverHost,serverPort, userName, password, firstName, lastName, email;
    RadioButton maleButton, femaleButton;
    Button signInButton,  registerButton;
    String holdServerHost,holdServerPort, holdUserName, holdPassword, holdFirstName, holdLastName, holdEmail, holdGender = "";
    private static final String LOGINMESSAGE = "LoginMessage";
    private static final String REGISTERMESSAGE = "RegisterMessage";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private TextWatcher textWatcher = new TextWatcher()
    {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableButtons();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    public void enableButtons()
    {
        holdServerHost = serverHost.getText().toString();
        holdServerPort = serverPort.getText().toString();
        holdUserName = userName.getText().toString();
        holdPassword = password.getText().toString();
        holdFirstName = firstName.getText().toString();
        holdLastName = lastName.getText().toString();
        holdEmail = email.getText().toString();
        if(maleButton.isChecked())
        {
            holdGender = "m";
        }
        else if(femaleButton.isChecked())
        {
            holdGender = "f";
        }
        signInButton.setEnabled((!holdServerHost.isEmpty()) && (!holdServerPort.isEmpty()) && (!holdUserName.isEmpty()) &&(!holdPassword.isEmpty()));
        registerButton.setEnabled((!holdServerHost.isEmpty()) && (!holdServerPort.isEmpty()) && (!holdUserName.isEmpty()) &&(!holdPassword.isEmpty()) &&
                (!holdFirstName.isEmpty()) && (!holdLastName.isEmpty()) && (!holdEmail.isEmpty()) && (!holdGender.isEmpty()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //pointers to all the fields
        serverHost = view.findViewById(R.id.serverHost);
        serverPort = view.findViewById(R.id.serverPort);
        userName = view.findViewById(R.id.userName);
        password = view.findViewById(R.id.password);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        maleButton = view.findViewById(R.id.maleButton);
        femaleButton = view.findViewById(R.id.femaleButton);
        signInButton = view.findViewById(R.id.signIn);
        registerButton = view.findViewById(R.id.register);

        signInButton.setEnabled(false);

        registerButton.setEnabled(false);
        //text watchers
        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        userName.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableButtons();
            }
        });
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableButtons();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(getActivity(), "SignIn Button Pressed", Toast.LENGTH_LONG).show();
                    Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper())
                    {
                        @Override
                        public void handleMessage(Message message) {
                            //TODO: figure out what to do with the returned message
                            Bundle bundle = message.getData();
                            String returnMessage = bundle.getString(LOGINMESSAGE, "");
                            Toast.makeText(getActivity(), returnMessage, Toast.LENGTH_LONG).show();
                            //check if the return message contains an error if it doesn't go to the map fragment
                            if(!returnMessage.contains("Error"))
                            {
                                //notify done
                                listener.notifyDone();
                            }
                        }
                    };

                    //make LoginRequest
                    LoginRequest request = new LoginRequest(holdUserName, holdPassword);
                    LoginTask task = new LoginTask(uiThreadMessageHandler, request, holdServerHost, holdServerPort);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);

            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Register Button Pressed", Toast.LENGTH_LONG).show();
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper())
                {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        String returnMessage = bundle.getString(REGISTERMESSAGE, "");
                        Toast.makeText(getActivity(), returnMessage, Toast.LENGTH_LONG).show();
                        //check if the return message contains an error if it doesn't go to the map fragment
                        if(!returnMessage.contains("Error"))
                        {
                            //notify done
                            listener.notifyDone();
                        }
                    }
                };

                //make RegisterRequest
                RegisterRequest request = new RegisterRequest(holdUserName,holdPassword,holdEmail,holdFirstName,holdLastName,holdGender);
                RegisterTask task = new RegisterTask(uiThreadMessageHandler, request, holdServerHost, holdServerPort);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            }
        });
        return view;
    }
    public LoginFragment() {
        // Required empty public constructor
    }
    public interface Listener {
        void notifyDone();
    }


    public void registerListener(Listener listener)
    {
        this.listener = listener;
    }

    private static class LoginTask implements Runnable
    {
        private final Handler messageHandler;
        private final LoginRequest request;
        private final String serverHost;
        private final String serverPort;
        ServerProxy serverProxy = new ServerProxy();
        public LoginTask(Handler messageHandler, LoginRequest request, String serverHost, String serverPort)
        {
            this.messageHandler = messageHandler;
            this.request = request;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }

        @Override
        public void run() {

            LoginResponse response = serverProxy.Login(request, serverHost,serverPort);
            sendMessage(response);
        }
        private void sendMessage(LoginResponse response) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            if(!(response.isSuccess()))
            {
                messageBundle.putString(LOGINMESSAGE, response.getMessage());
            }
            else
            {
                //make the family map and the events map
                PersonResponse personResponse = serverProxy.getFamily(serverHost,serverPort, response.getAuthtoken());
                EventResponse eventResponse = serverProxy.getEvents(serverHost,serverPort, response.getAuthtoken());
                Datacache instance = Datacache.getInstance();
                instance.setRootUser(response.getPersonID()); //get the root user personId
                //set them in datacache
                instance.setPeople(personResponse.getData());
                instance.setEvents(eventResponse.getData());
                instance.setColorCoded(eventResponse.getData());
                Person user = instance.findPerson(response.getPersonID());
                String name = "Welcome back! " + user.getFirstName() + " " + user.getLastName();
                messageBundle.putString(LOGINMESSAGE, name);

            }
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
    private static class RegisterTask implements Runnable
    {
        private final Handler messageHandler;
        private final RegisterRequest request;
        private final String serverHost;
        private final String serverPort;
        ServerProxy serverProxy = new ServerProxy();

        public RegisterTask(Handler messageHandler, RegisterRequest request, String serverHost, String serverPort)
        {
            this.messageHandler = messageHandler;
            this.request = request;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }

        @Override
        public void run()
        {
            RegisterResponse response = serverProxy.Register(request, serverHost,serverPort);
            //set the data cache with what it needs


            sendMessage(response);
        }
        private void sendMessage(RegisterResponse response) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            if(!(response.isSuccess()))
            {
                messageBundle.putString(REGISTERMESSAGE, response.getMessage());

            }
            else
            {
                //make the family map and the events map
                PersonResponse personResponse = serverProxy.getFamily(serverHost,serverPort, response.getAuthtoken());
                EventResponse eventResponse = serverProxy.getEvents(serverHost,serverPort, response.getAuthtoken());
                Datacache instance = Datacache.getInstance();
                instance.setRootUser(response.getPersonID()); //get the root users personId
                //set them in datacache
                instance.setPeople(personResponse.getData());
                instance.setEvents(eventResponse.getData());
                instance.setColorCoded(eventResponse.getData());
                String name = "Welcome! " + request.getFirstName() + " " + request.getLastName();
                messageBundle.putString(REGISTERMESSAGE, name);

            }
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }





}