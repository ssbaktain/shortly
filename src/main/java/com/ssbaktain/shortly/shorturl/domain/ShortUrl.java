package com.ssbaktain.shortly.shorturl.domain;

import com.ssbaktain.shortly.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_url")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_key", nullable = false, unique = true, length = 10)
    private String shortKey;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "password_hash", length = 60)
    private String passwordHash;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private ShortUrl(String shortKey, String originalUrl, Member member,
                     String passwordHash, LocalDateTime expiresAt) {
        this.shortKey = shortKey;
        this.originalUrl = originalUrl;
        this.member = member;
        this.passwordHash = passwordHash;
        this.expiresAt = expiresAt;
    }

    public void assignShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    public boolean isExpired() {
        if (this.expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isPasswordProtected() {
        return this.passwordHash != null;
    }

    public void increaseClickCount() {
        this.clickCount++;
    }
}
