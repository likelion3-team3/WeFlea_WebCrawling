package com.example.crawling.base.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Search extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String title;           // 게시글 제목

    private int price;              // 상품 가격

    private LocalDateTime sellDate; // 판매 시간

    @Column(length = 1000)
    private String link;            // 게시글 링크

    private String siteProduct;     // 게시글 나중에 중복 제거할 때 비교할 컬럼

    @Column(length = 1000)
    private String imageLink;       // 게시글 사진 링크

    private String provider;        // 어느 거래 사이트인지 ex) 당근마켓, 중고나라, ...

    private String area;            // 지역
}
