/**
 * 
 */
package com.example.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Temperature;

/**
 * @author Princy
 *
 */
@Repository("temperatureRepository")
public interface TemperatureRespository extends CrudRepository<Temperature, Long>{
	
	Temperature findByPincode(int pincode);
}
