package com.mytravel.plans;

import com.mytravel.vo.TripPlan;
/**
 * @author Deepank Sharma
 *
 */
public interface ServiceProvider {
	TripPlan createPlan(String origin, String destination);
	String getAlliance();
}
