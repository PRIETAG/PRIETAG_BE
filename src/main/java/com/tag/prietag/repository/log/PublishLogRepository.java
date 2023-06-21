package com.tag.prietag.repository.log;

import com.tag.prietag.model.log.CustomerLog;
import com.tag.prietag.model.log.PublishLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PublishLogRepository extends JpaRepository<PublishLog, Long> {

    // user의 publishLog조회
    @Query("select l from PublishLog l where l.user.id=:userId order by l.createdAt desc")
    Optional<List<PublishLog>> findByUserId(@Param("userId") Long userId);

    @Query("select l from PublishLog l join fetch l.templatevs where l.user.id=:userId and l.createdAt between :startDate AND :endDate order by l.createdAt asc")
    Page<PublishLog> findByBetweenDateUserId(@Param("userId") Long userId
            , @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate, Pageable pageable);
}
