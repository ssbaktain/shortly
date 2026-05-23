package com.ssbaktain.shortly.shorturl.repository;

import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortKey(String shortKey);

    boolean existsByShortKey(String shortKey);
}
