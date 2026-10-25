package com.edu.checkcode.dto;

import com.edu.checkcode.model.Location;

public record LocationResponse(
		AddressResponse address,
		String city,
		String state
) {
	public static LocationResponse from(Location location) {
		return new LocationResponse(
				AddressResponse.from(location.getAddress()),
				location.getCity(),
				location.getState()
		);
	}
}
