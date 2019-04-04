package com.ipb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.entity.IpbCategory;

public interface IpbCategoryRepository extends JpaRepository<IpbCategory, Long> {

}
