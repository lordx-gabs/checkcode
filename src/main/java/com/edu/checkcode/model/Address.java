package com.edu.checkcode.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	@Column(nullable = false, length = 300)
	private String street;

	@Column(nullable = false, length = 20)
	private String number;

	@Column(length = 100)
	private String complement;

	@Column(name = "zip_code", nullable = false, length = 20)
	private String zipCode;
}
