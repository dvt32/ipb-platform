package com.ipb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.entity.IpbEvent;

public interface IpbEventRepository extends JpaRepository<IpbEvent, Long> {
	
}
