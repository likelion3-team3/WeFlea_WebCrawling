package com.example.crawling.base.search.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public abstract class File extends BaseEntity {
    private String name;

    //확장자 (jpg, png ....)
    private String file_type;

    private String path;

    private int type_code;

}