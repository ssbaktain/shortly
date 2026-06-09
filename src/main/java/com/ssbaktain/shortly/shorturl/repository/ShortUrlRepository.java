package com.ssbaktain.shortly.shorturl.repository;

import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortKey(String shortKey);

    boolean existsByShortKey(String shortKey);

    Page<ShortUrl> findByMemberId(Long memberId, Pageable pageable);

    @Modifying
    @Query("UPDATE ShortUrl s " +
            "SET s.clickCount = s.clickCount + 1 " +
            "WHERE s.id = :id")
    void incrementClickCount(@Param("id") Long id);
}
