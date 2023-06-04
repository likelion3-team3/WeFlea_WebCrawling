package com.example.crawling.base.search.repository;

import com.example.crawling.base.search.entity.SearchKeyword;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SearchKeywordRepository {
    private final JdbcTemplate jdbcTemplate;

    public SearchKeywordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveAll(List<SearchKeyword> searchKeywords){
        String sql = "INSERT INTO search_keyword (name)" +
                "VALUES (?)";

        jdbcTemplate.batchUpdate(sql, searchKeywords, searchKeywords.size(),
                (PreparedStatement ps, SearchKeyword searchKeyword) -> {
                    ps.setString(1, searchKeyword.getName());
                });
    }
}
