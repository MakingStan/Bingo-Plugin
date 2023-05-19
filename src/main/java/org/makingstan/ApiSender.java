package org.makingstan;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class ApiSender {
    String domain;

    public ApiSender(String domain)
    {
        this.domain = domain;
    }

    public void sendPostRequest(String directory, HashMap<String, Object> body)
    {
        URL url = null;
        try {
            url = new URL(domain+directory);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;

            Gson gson = new Gson();
            byte[] jsonBody = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
            int length = jsonBody.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.setDoOutput(true);
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(jsonBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendGetRequest(String directory)
    {
        URL url = null;
        try {
            url = new URL(domain+directory);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("GET");
            http.setRequestProperty("accept", "application/json");
            http.setDoOutput(true);
            http.connect();

            System.out.println(directory);
            int responseCode = http.getResponseCode();
            System.out.println(responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                System.out.println("success");
                // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine+" ");
                }
                in.close();

                String responseString = response.toString();

                return responseString;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
