package com.mytravel.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mytravel.plans.ServiceProvider;
import com.mytravel.service.TravelService;
import com.mytravel.vo.TripPlan;

/**
 * @author Deepank Sharma
 *
 */
@Component
public class TravelServiceImpl implements TravelService {
	
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

	@Override
	public TripPlan selectBestTripPlan(List<ServiceProvider> providers, String origin, String destination) {
		List<CompletableFuture<TripPlan>> tripPlans = providers.stream()
				.map(provider -> CompletableFuture.supplyAsync(() -> provider.createPlan(origin,destination),EXECUTOR_SERVICE)) // For each provider invoke createPlan
				.collect(Collectors.toList());
		return tripPlans.stream()
				.min(Comparator.comparing(plan -> plan.join().getPrice())) // Pass each plan to the method
				.get().join();
	}
	
	
	public TripPlan selectBestTripPlan(List<ServiceProvider> providers, String origin, String destination, String alliance) {
		List<CompletableFuture<TripPlan>> tripPlans = providers.stream()
				.filter(provider -> alliance==null||provider.getAlliance().equals(alliance))
				.map(provider -> CompletableFuture.supplyAsync(() -> provider.createPlan(origin,destination),EXECUTOR_SERVICE)) // For each provider invoke createPlan
				.collect(Collectors.toList());

		return tripPlans.stream()
				.min(Comparator.comparing(plan -> plan.join().getPrice())) // Pass each plan to the method
				.get().join();
	}

	@Override
	public TripPlan combine(TripPlan planA, TripPlan planB) {
		TripPlan plan = TripPlan.builder()
				.Price(planA.getPrice()+planB.getPrice())
				.origin(planA.getOrigin()).destination(planA.getDestination())
				.serviceProvider(planA.getServiceProvider() + " "+ planB.getServiceProvider()).build();
		if(planA.getAlliance().equalsIgnoreCase(planB.getAlliance())){
			plan.setAlliance(planA.getAlliance());
		}
		return plan;
	}

	@Override
	public TripPlan addCarHire(List<ServiceProvider> providers,TripPlan p) {
		return combine(selectBestTripPlan(providers,p.getOrigin(),p.getDestination(),p.getAlliance()),p);		
	}


	@Override
	public TripPlan combine(List<TripPlan> plans) {
		return plans.stream().reduce(new TripPlan(),(p1,p2)->{
			p1.setPrice(p1.getPrice()+p2.getPrice());
			p1.setOrigin(p2.getOrigin());
			p1.setDestination(p2.getDestination());
			p1.setServiceProvider(p1.getServiceProvider()+":"+p2.getServiceProvider());
			p1.setAlliance(p2.getAlliance());
			return p1;
		});
	}






}
