package com.ipb.platform.services.impl;

import com.ipb.platform.persistance.DatesNotWorkingRepository;
import com.ipb.platform.services.DatesNotWorkingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DatesNotWorkingServiceImpl implements DatesNotWorkingService {

	private DatesNotWorkingRepository repository;

	@Override
	public boolean deleteByObjectId(Long objectId) {
		this.repository.deleteByObjectId(objectId);
		return true;
	}

}
