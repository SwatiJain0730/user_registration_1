/**
 * 
 */
package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Temperature;
import com.example.repository.TemperatureRespository;

/**
 * @author Princy
 *
 */
@Service("temperatureService")
public class TemperatureService {
	
	@Autowired
	private TemperatureRespository temperatureRespository;
	
	public Temperature findByPincode(int pincode){
		return temperatureRespository.findByPincode(pincode);
	}

}
