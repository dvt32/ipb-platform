package com.ipb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.entity.IpbUser;

public interface IpbUserRepository extends JpaRepository<IpbUser, Long> {

}
