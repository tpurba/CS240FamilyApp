package com.example.myapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Response.ClearResponse;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import Response.RegisterResponse;


public class ServerProxy {
    public static LoginResponse Login(LoginRequest request, String serverHost, String serverPort)
    {
        Gson gson = new Gson();
        try
        {
            //make url
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            //connect
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            //specify send request
            http.setRequestMethod("POST");
            //specify request body
            http.setDoOutput(true);
            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            //request body
            String reqData = gson.toJson(request);
            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                //make it into the response
                LoginResponse response = (LoginResponse) gson.fromJson(respData,LoginResponse.class);
                return response;
            }
            else {

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);
                LoginResponse response = (LoginResponse) gson.fromJson(respData,LoginResponse.class);
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }
    public static RegisterResponse Register(RegisterRequest request, String serverHost, String serverPort)
    {
        Gson gson = new Gson();
        try
        {
            //make url
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            //connect
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            //specify send request
            http.setRequestMethod("POST");
            //specify request body
            http.setDoOutput(true);
            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            //request body
            String reqData = gson.toJson(request);
            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                //make it into the response
                RegisterResponse response = (RegisterResponse) gson.fromJson(respData,RegisterResponse.class);
                return response;
            }
            else {

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);
                RegisterResponse response = (RegisterResponse) gson.fromJson(respData,RegisterResponse.class);
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }
    public static PersonResponse getFamily(String serverHost, String serverPort, String authToken )
    {
        Gson gson = new Gson();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");

            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);


            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", authToken);

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");


            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                PersonResponse response = (PersonResponse) gson.fromJson(respData,PersonResponse.class);
                return response;

            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                PersonResponse response = (PersonResponse) gson.fromJson(respData,PersonResponse.class);
                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return null;
    }
    public static EventResponse getEvents(String serverHost, String serverPort, String authToken )
    {
        Gson gson = new Gson();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");

            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);


            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", authToken);

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");


            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                EventResponse response = (EventResponse) gson.fromJson(respData,EventResponse.class);
                return response;

            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                EventResponse response = (EventResponse) gson.fromJson(respData,EventResponse.class);
                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return null;
    }

    public static ClearResponse Clear(String serverHost, String serverPort)
    {
        Gson gson = new Gson();
        try
        {
            //make url
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/clear");
            //connect
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            //specify send request
            http.setRequestMethod("POST");
            //specify request body
            http.setDoOutput(true);
            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                //make it into the response
                ClearResponse response = (ClearResponse) gson.fromJson(respData, ClearResponse.class);
                return response;
            }
            else {

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();
                // Extract data from the HTTP response body
                String respData = readString(respBody);
                ClearResponse response = (ClearResponse) gson.fromJson(respData,ClearResponse.class);
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

}
