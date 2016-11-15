package com.mytravel.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * @author Deepank Sharma
 *
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPlan {
	@JsonProperty("price")
	int Price;
	@JsonProperty("destination")
	String destination;
	@JsonProperty("origin")
	String origin;
	@JsonProperty("service_provider")
	String serviceProvider;
	@JsonProperty("alliance")
	String alliance;
}
