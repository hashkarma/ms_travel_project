package com.mytravel.service;

import java.util.List;

import com.mytravel.plans.ServiceProvider;
import com.mytravel.vo.TripPlan;
/**
 * @author Deepank Sharma
 *
 */
public interface TravelService {
	public TripPlan selectBestTripPlan(List<ServiceProvider> providers, String origin, String destination);
	public TripPlan combine(TripPlan planA, TripPlan planB);
	public TripPlan addCarHire(List<ServiceProvider> providers,TripPlan p);
	TripPlan combine(List<TripPlan> plans);
}
