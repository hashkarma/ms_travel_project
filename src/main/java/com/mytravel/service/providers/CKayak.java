package com.mytravel.service.providers;

import java.util.Random;

import com.mytravel.plans.ServiceProvider;
import com.mytravel.vo.TripPlan;

public class CKayak implements ServiceProvider {

	@Override
	public TripPlan createPlan(String origin, String destination) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int price = new Random().nextInt(300);
		TripPlan plan = TripPlan.builder().origin(origin).destination(destination).serviceProvider("Kayak.com")
				.Price(price).alliance(getAlliance()).build();
		System.out.println("Car from Kayak :: $" + price);
		return plan;
	}

	@Override
	public String getAlliance() {
		return "Star";
	}

}
