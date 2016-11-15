package com.mytravel.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
	@Autowired
	TravelService service;
	
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
	@RequestMapping(value = "/v1/airhotels/")
	public ResponseEntity<Object> getAirlineAndHotelQuotes(){		
		return ResponseEntity.ok().body(
				CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"))
				.thenCombine(
						CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeHotels(), "Dallas", "Miami"))
						, (air,hotels) -> service.combine(air,hotels)).join());				
	}
	
	/**
	 * Method to fetch the best price quote amongst a list of Service Providers for airlines and hotels and get Car quotes based on the previous two quotes
	 *
	 */
	@RequestMapping(value = "/v1/airhotelscar/")
	public ResponseEntity<Object> getAirlineAndHotelandcarQuoteswithAlliance(){		
		return ResponseEntity.ok().body(
				CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"))
				.thenCombine(
						CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeHotels(), "Dallas", "Miami"))
						, (air,hotels) -> service.combine(air,hotels))
				.thenCompose(
						p->CompletableFuture.supplyAsync(()->service.addCarHire(initializeCars(), p))).join());				
	}
	
	
	/**
	 * Method to fetch the best price quote amongst a list of Service Providers for airlines and hotels and get Car quotes based on the previous two quotes
	 *
	 */
	@RequestMapping(value = "/v2/airhotelscar/")
	public ResponseEntity<Object> getAirlineAndHotelandcarQuotes(){	
		List<CompletableFuture<TripPlan>> lsTp = new ArrayList<>();
		CompletableFuture<TripPlan> airLinePlan = CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeAirlines(), "Dallas", "Miami"));
		CompletableFuture<TripPlan> hotelPlan = CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeHotels(), "Dallas", "Miami"));
		CompletableFuture<TripPlan> carPlan = CompletableFuture.supplyAsync(()->service.selectBestTripPlan(initializeCars(), "Dallas", "Miami"));
		lsTp.add(hotelPlan);
		lsTp.add(airLinePlan);
		lsTp.add(carPlan);
		return ResponseEntity.ok().body(
				CompletableFuture.allOf(airLinePlan,hotelPlan,carPlan)
				.thenApply(
						ignoredVoid -> lsTp.stream().map(plan -> plan.join()).collect(Collectors.toList()))
				.thenApply(plans->service.combine(plans)).join());				
	}
	
	
	
	private static List<ServiceProvider> initializeAirlines(){
		List<ServiceProvider> hotels = new ArrayList<>();
		hotels.add(new AAmericanAirlines());
		hotels.add(new ADelta());
		hotels.add(new ASouthWest());
		return hotels;
	}
	
	private static List<ServiceProvider> initializeHotels(){
		List<ServiceProvider> hotels = new ArrayList<>();
		hotels.add(new HCheapHotels());
		hotels.add(new HHotelsDotCom());
		return hotels;
	}
	
	private static List<ServiceProvider> initializeCars(){
		List<ServiceProvider> hotels = new ArrayList<>();
		hotels.add(new CKayak());
		hotels.add(new CCars());
		return hotels;
	}
}
