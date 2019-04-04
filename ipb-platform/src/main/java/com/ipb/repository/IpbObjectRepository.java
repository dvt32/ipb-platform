package com.ipb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.entity.IpbObject;

public interface IpbObjectRepository extends JpaRepository<IpbObject, Long> {

}
