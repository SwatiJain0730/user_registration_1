/**
 * 
 */
package com.example.controller;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.model.Temperature;
import com.example.model.User;
import com.example.response.LoginResponse;
import com.example.response.TemperatureResponse;
import com.example.response.UserInfoResponse;
import com.example.response.UserRegistrationResponse;
import com.example.service.TemperatureService;
import com.example.service.UserService;


/**
 * @author Swati
 *
 */
@RestController
public class LoginController {

	@Autowired
	private UserService userService;

	@Autowired
	private TemperatureService temperatureService;

	/***
	 * 
	 * @param userInfo
	 * @return
	 */
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<UserRegistrationResponse> register(@RequestBody User userInfo){
		String userId = "";
		UserRegistrationResponse response = new UserRegistrationResponse();
		try{

			// Check if user already exists
			User userExists = userService.findByEmail(userInfo.getEmail());
			// Save user details to DB
			if (userExists == null && userInfo != null){
				/*String userId = userInfo.getName()+UUID.randomUUID().toString().substring(0, 5);
			userInfo.setUserId(userId);*/
				User userRegister = userService.saveUser(userInfo);
				//User registration successful - generate user id and save it to database
				//Can be saved before making a call to db , but as per instructions , user id is generated after registration
				userId = userRegister.getName()+UUID.randomUUID().toString().substring(0, 5);
				userInfo.setUserId(userId);
				userService.saveUser(userInfo);
			}
		}
		catch(Exception exception){
			response.setMessage("User registration failed! Please try after sometime");
			return new ResponseEntity<UserRegistrationResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.setMessage("User registration is successful!");
		response.setUserId(userId);
		return new ResponseEntity<UserRegistrationResponse>(response, HttpStatus.CREATED);
	}

	/***
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/login/{userId}", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<LoginResponse> getLogin(@PathVariable("userId") String userId, HttpServletRequest request){

		String loginUrl = "";
		LoginResponse response = new LoginResponse();
		try{
			User userExists = userService.findByUserId(userId);

			if(userExists != null){
				Date date = new Date();
				userExists.setExpiryTime(date);
				userService.saveUser(userExists);
				loginUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/login";
				response.setMessage("Login link sent to your email. Please click the link for login");
				response.setLoginUrl(loginUrl);
			}
			else{
				response.setMessage("User does not exists");				
			}
		}catch(Exception exception){
			return new ResponseEntity<LoginResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
	}
	/***
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/login/details/{userId}", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<UserInfoResponse> getUserLoginDetails(@PathVariable("userId") String userId){
		UserInfoResponse response = new UserInfoResponse();
		try{

			User userExists = userService.findByUserId(userId);
			if(userExists != null){
				Date expiryTime = userExists.getExpiryTime();
				Date currentTime = new Date();
				long diff = currentTime.getTime() - expiryTime.getTime();
				long diffMinutes = diff / (60 * 1000) % 60;
				if(diffMinutes <=15){
					response.setName(userExists.getName());
					response.setEmailId(userExists.getEmail());
					response.setPincode(userExists.getPincode());
				}
				else{
					response.setMessage("Login expired! Please register again");
				}
			}
			else {
				response.setMessage("User not found!");
			}

		}
		catch(Exception exception){
			return new ResponseEntity<UserInfoResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<UserInfoResponse>(response, HttpStatus.OK);

	}
	/***
	 * 
	 * @param pincode
	 */
	@RequestMapping(value="/temperature/{userId}/{pincode}", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<TemperatureResponse> getTemperatureByPinCode(@PathVariable("userId") String userId, @PathVariable("pincode") int pincode){
		TemperatureResponse response = new TemperatureResponse();
		double temp = 0.0d;
		try {
			User user = userService.findByPincodeAndUserId(pincode, userId);
			if (user != null){
				Temperature temperature = user.getTemperature();
				if(temperature == null){
					//No Temperature value available for user
					temp = saveTemp(pincode, user);
					response.setPincode(pincode);
					response.setTemperature(temp);
				}
				else if(temperature != null){ 
					Date tempTime = temperature.getTime();
					Date currentTime =  new Date();
					long diff = currentTime.getTime() - tempTime.getTime();
					long diffSeconds = diff / 1000 % 60;
					if(diffSeconds >=30){
						temp = updateTemp(pincode, user);
						response.setPincode(pincode);
						response.setTemperature(temp);
					}
					else{
						response.setPincode(pincode);
						response.setTemperature(temperature.getTemperature());
					}
				}
			}
			else{
				response.setPincode(pincode);
				response.setTemperature(temp);
				response.setMessage("User not found");
			}

		}
		catch(Exception exception){
			response.setMessage("Error while getting temperature details");
			return new ResponseEntity<TemperatureResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<TemperatureResponse>(response, HttpStatus.OK);
	}
	private double getTemperature(int pincode) throws JSONException{
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> temperatureStr = 
				restTemplate.getForEntity("http://samples.openweathermap.org/data/2.5/weather?zip="+pincode+",us&appid=b1b15e88fa797225412429c1c50c122a1", String.class);
		JSONObject jsonObject = new JSONObject(temperatureStr.getBody());
		double temp = jsonObject.getJSONObject("main").getDouble("temp");
		return temp;
	}
	private double saveTemp(int pincode,User user) throws JSONException{
		double temp = getTemperature(pincode);
		Temperature temperatureObj = new Temperature();
		temperatureObj.setPincode(pincode);
		temperatureObj.setTemperature(temp);
		Date time = new Date();
		temperatureObj.setTime(time);
		user.setTemperature(temperatureObj);
		user = userService.saveUser(user);
		return temp;
	}
	private double updateTemp(int pincode,User user) throws JSONException{
		double temp = getTemperature(pincode);
		Temperature temperatureObj = user.getTemperature();
		temperatureObj.setPincode(pincode);
		temperatureObj.setTemperature(temp);
		Date time = new Date();
		temperatureObj.setTime(time);
		user.setTemperature(temperatureObj);
		user = userService.saveUser(user);
		return temp;
	}
}
