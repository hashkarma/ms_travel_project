package com.mytravel.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytravel.plans.ServiceProvider;
import com.mytravel.service.TravelService;
import com.mytravel.service.providers.AAmericanAirlines;
import com.mytravel.service.providers.ADelta;
import com.mytravel.service.providers.ASouthWest;
import com.mytravel.service.providers.CCars;
import com.mytravel.service.providers.CKayak;
import com.mytravel.service.providers.HCheapHotels;
import com.mytravel.service.providers.HHotelsDotCom;
import com.mytravel.vo.TripPlan;
/**
 * @author Deepank Sharma
 *
 */
@RestController
public class TravelController {	
	/**
	 * Method to fetch the best price quote amongst a list of Service Providers for airlines
	 *
	 */
	@RequestMapping(value = "/v1/air/")
	public ResponseEntity<Object> getAirlineQuotes(){		
		return ResponseEntity.ok().body(
				service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"));						
	}
	
	/**
	 * Method to fetch the best price quote amongst a list of Service Providers for airlines and hotels
	 *
	 */
	@RequestMapping(value = "/v1/airhotel/")
	public ResponseEntity<Object> getAirlineAndHotelQuotes(){		
		return ResponseEntity.ok().body(
				CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"),EXECUTOR_SERVICE)
				.thenCombine(
						CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeHotels(), "Dallas", "Miami"),EXECUTOR_SERVICE)
						, (air,hotels) -> service.combine(air,hotels)).join());				
	}
	
	
	/**
	 * Method to fetch the best price quote amongst a list of Service Providers for airlines and hotels
	 *
	 */
	@RequestMapping(value = "/v1/airhotelcar/")
	public ResponseEntity<Object> getAirlineWithHotelandCarQuotes(){		
		return ResponseEntity.ok().body(
				CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"),EXECUTOR_SERVICE)
				.thenCombine(
						CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeHotels(), "Dallas", "Miami"),EXECUTOR_SERVICE)
						,(air,hotels) -> service.combine(air,hotels))
				.thenCompose(
						p->CompletableFuture.supplyAsync(()->service.addCarHire(initializeCars(), p),EXECUTOR_SERVICE)).join());				
	}
	
	/**
	 * Method to fetch the best price quote amongst a list of Service Providers for airlines and hotels and get Car quotes based on the previous two quotes
	 *
	 */
	@RequestMapping(value = "/v2/airhotelscar/")
	public ResponseEntity<Object> getAirlineAndHotelandcarQuotes(){	
		List<CompletableFuture<TripPlan>> lsTp = new ArrayList<>();
		CompletableFuture<TripPlan> airLinePlan = CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"),EXECUTOR_SERVICE);
		CompletableFuture<TripPlan> hotelPlan = CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeHotels(), "Dallas", "Miami"),EXECUTOR_SERVICE);
		CompletableFuture<TripPlan> carPlan = CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeCars(), "Dallas", "Miami"),EXECUTOR_SERVICE);
		lsTp.add(hotelPlan);
		lsTp.add(airLinePlan);
		lsTp.add(carPlan);
		return ResponseEntity.ok().body(
				CompletableFuture.allOf(airLinePlan,hotelPlan,carPlan)
				.thenApplyAsync(
						ignoredVoid -> lsTp.stream().map(plan -> plan.join()).collect(Collectors.toList()))
				.thenApplyAsync(plans->service.combine(plans)).join());				
	}
	
	
	
	private List<ServiceProvider> initializeAirlines(){
		List<ServiceProvider> hotels = new ArrayList<>();
		hotels.add(american);
		hotels.add(delta);
		hotels.add(sWest);
		return hotels;
	}
	
	private List<ServiceProvider> initializeHotels(){
		List<ServiceProvider> hotels = new ArrayList<>();
		hotels.add(cHotel);
		hotels.add(hotelC);
		return hotels;
	}
	
	private List<ServiceProvider> initializeCars(){
		List<ServiceProvider> hotels = new ArrayList<>();
		hotels.add(kayak);
		hotels.add(cars);
		return hotels;
	}
	
	@Autowired TravelService service;
	@Autowired AAmericanAirlines american;
	@Autowired ADelta delta;
	@Autowired ASouthWest sWest;
	@Autowired CCars cars;
	@Autowired CKayak kayak;
	@Autowired HCheapHotels cHotel;
	@Autowired HHotelsDotCom hotelC;
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
}
