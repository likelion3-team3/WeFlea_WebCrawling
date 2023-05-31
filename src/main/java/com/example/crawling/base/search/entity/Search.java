package com.example.crawling.base.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Search extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String title;

    private String price;

    private String sellDate;

    private String link;

    private String imageLink;

    @OneToMany(mappedBy = "search")
    private List<SearchImage> searchImages = new ArrayList<>();

}
