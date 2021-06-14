package com.rafsan.inventory.ipos;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.awt.*;

public class IPOSTransaction {

    public JSONObject makeTransaction(JSONObject request){
        // create request body
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:9111/api/requests";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
        String answer = restTemplate.postForObject(url, entity, String.class);
        System.out.println(answer);
        return new JSONObject(answer);
    }
}
