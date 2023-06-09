package com.tag.prietag.repository.log;

import com.tag.prietag.model.log.CustomerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerLogRepository extends JpaRepository<CustomerLog, Long> {

    // 해당 유저의 일정날짜 Log조회
    @Query("select l from CustomerLog l where l.userId=:userId and l.createdAt between :startDate AND :endDate")
    Optional<List<CustomerLog>> findByBetweenDateUserId(@Param("userId") Long userId, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);
}
