package com.example.crawling.base.search.repository;

import com.example.crawling.base.search.entity.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
}
