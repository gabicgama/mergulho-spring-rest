package com.algaworks.algalog.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algalog.domain.model.Delivery;
import com.algaworks.algalog.domain.model.Occurrence;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RegisterOccurrenceService {

	private FindDeliveryService findDeliveryService;

	@Transactional
	public Occurrence register(Long deliveryId, String description) {
		Delivery entrega = findDeliveryService.findById(deliveryId);
		return entrega.addOccurrence(description);
	}

}
