package com.example.crawling.base.search.controller;


import com.example.crawling.base.search.entity.Search;
import com.example.crawling.base.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/joongna")
    public String joongnaCrawling(Model model) {
        WebDriver driver = setCrawling();

        String keyword = "신발";

        searchService.searchJoongna(driver, keyword);
        //List<Search> searchList = searchService.searchJoongna(driver, keyword);

        driver.quit();

        //model.addAttribute("search_list", searchList);
        return "list";
    }

    public WebDriver setCrawling(){
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");        // 전체 화면으로 실행
        chromeOptions.addArguments("--disable-popup-blocking"); // 팝업 무시
        chromeOptions.addArguments("--headless");               // 브라우저 안띄움
        chromeOptions.addArguments("--disable-gpu");            // GPU를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요
        chromeOptions.addArguments("--no-sandbox");             // Sandbox 프로세스를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요

        WebDriver driver = new ChromeDriver(chromeOptions);

        return driver;
    }

        @GetMapping("/bunjang")
    public String crawlingBunjang(Model model){

        return"list";
    }

    @GetMapping("/daangn")
    public String crawlingDaangn(Model model){

        return"list";
    }

    @GetMapping("/hello")
    public String crawlingHello(Model model){

        return"list";
    }
}
