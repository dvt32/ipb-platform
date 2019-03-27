package com.ipb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.entity.IpbLandmark;

public interface IpbLandmarkRepository extends JpaRepository<Long, IpbLandmark> {

}
