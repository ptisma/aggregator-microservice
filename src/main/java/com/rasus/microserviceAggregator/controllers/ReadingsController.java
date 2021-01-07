package com.rasus.microserviceAggregator.controllers;

import com.rasus.microserviceAggregator.model.UserResponse;
import com.rasus.microserviceAggregator.services.MeasurementsService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RefreshScope
public class ReadingsController {


    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private Environment environment;

    @Autowired
    private MeasurementsService measurementsService;

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/readings")
    public ResponseEntity<UserResponse> getReadings() throws JSONException {
        UserResponse userResponse = new UserResponse();
        //JSONObject userResponse = new JSONObject();
        String TemperatureBaseUrl = loadBalancerClient.choose("TEMPERATURE-MICROSERVICE").getUri().toString();
        String HumidityBaseUrl = loadBalancerClient.choose("HUMIDITY-MICROSERVICE").getUri().toString();

        System.out.println(TemperatureBaseUrl);
        System.out.println(HumidityBaseUrl);

        //String temperature = restTemplate.getForObject(baseUrl + "/current-reading", String.class);
        ResponseEntity<Integer> response = restTemplate.getForEntity(TemperatureBaseUrl + "/current-reading", Integer.class);

        double temperature = response.getBody();

        response = restTemplate.getForEntity(HumidityBaseUrl + "/current-reading", Integer.class);
        int humidity = response.getBody();
        userResponse.setHumidity(humidity);



        String measurementUnit = environment.getProperty("measurementUnit");
        userResponse.setUnit(measurementUnit);
        System.out.println(measurementUnit);
       if (measurementUnit.equals("C")){
           userResponse.setTemperature(temperature);
       }
       else if (measurementUnit.equals("K")) {
           temperature = measurementsService.CelsiusToKelvin(temperature);
           userResponse.setTemperature(temperature);
       }




       return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);


    }



}
