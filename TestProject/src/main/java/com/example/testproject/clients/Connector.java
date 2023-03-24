package com.example.testproject.clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Connector {
    CookieManager cookieManager = new CookieManager();
    HttpURLConnection con;
    public Connector() {
        CookieHandler.setDefault(cookieManager);
        //CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    public List<String> sendRequest(RequestData requestData) {
        try {
            URL url = new URL(requestData.getUri());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestData.getMethod());
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setUseCaches(false);
            String requestJson = requestData.getRequestJson();
            if (requestJson != null) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = requestJson.getBytes();
                    os.write(input, 0, input.length);
                }
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                List<String> output = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    output.add(line);
                }
                return output;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
