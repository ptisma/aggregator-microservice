package com.rasus.microserviceAggregator.services;

import org.springframework.stereotype.Service;

@Service
public class MeasurementsService {


    public double CelsiusToKelvin(double celsius) {
        return celsius + 273;

    }
}
