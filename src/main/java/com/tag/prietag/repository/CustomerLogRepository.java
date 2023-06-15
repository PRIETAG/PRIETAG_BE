package com.tag.prietag.repository;

import com.tag.prietag.model.log.CustomerLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerLogRepository extends JpaRepository<CustomerLog, Long> {
}
