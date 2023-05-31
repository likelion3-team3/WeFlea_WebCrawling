package com.example.crawling.base.search.repository;

import com.example.crawling.base.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {
}
