package com.example.crawling.base.search.service;

import com.example.crawling.base.search.entity.Search;
import com.example.crawling.base.search.repository.SearchImageRepository;
import com.example.crawling.base.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final SearchRepository searchRepository;

    public void searchJoongna(WebDriver driver, String keyword){
        //List<Search> searchList = new ArrayList<>();
        List<WebElement> webElementList;

        // 5페이지까지 크롤링: 1페이지당 20개
        for (int i = 1; i < 6; i++) {
            String url = "https://web.joongna.com/search/" + keyword + "?page=" + i;
            try {
                driver.get(url);

                System.out.println(i + "페이지 시작");

                // 리스트 요소 한칸(게시글 링크, 사진, 가격, 시간, 제목)
                webElementList = driver.findElements(By.cssSelector("[class=\"ant-col col css-t7ixlq e312bqk0\"] a"));

                if (!webElementList.isEmpty()) {
                    for (WebElement webElement : webElementList) {
                        // 게시글 사이트 링크
                        String siteLink = webElement.getAttribute("href");

                        // 광고가 아닌 실제 게시글만 필터링
                        if (!siteLink.startsWith("https://web.joongna")) {
                            continue;
                        }

                        // 게시글이 올라온 시간과 상품 가격, 게시글 제목, 게시글 사진
                        WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"css-1kiruf2\"]"));

                        // 중고나라는 registInfo가 2개인 경우도 있다. 1개인 경우는 시간만, 2개인 경우는 지역 + 시간
                        List<WebElement> registInfoList= webElementDetail.findElements(By.cssSelector("[class=\"registInfo\"] span"));
                        String date = "";

                        if (registInfoList.size() == 1){
                            date = registInfoList.get(0).getText();
                        } else {
                            date = registInfoList.get(1).getText();
                        }

                        String price = webElementDetail.findElement(By.cssSelector("[class=\"priceTxt\"]")).getText();
                        String title = webElementDetail.findElement(By.cssSelector("[class=\"css-5uwdmz\"]")).getText();
                        String imgLink = webElementDetail.findElement(By.cssSelector("[class=\"css-jib2h7\"] img")).getAttribute("src");

                        Search search = Search
                                .builder()
                                .link(siteLink)
                                .sellDate(date)
                                .price(price)
                                .title(title)
                                .imageLink(imgLink)
                                .provider("중고나라")
                                .build();

                        //searchList.add(search);

                        searchRepository.save(search);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //return searchList;
    }
    public static boolean isFirstCharacterDigit(String str) {
        if (str != null && !str.isEmpty()) {
            char firstChar = str.charAt(0);
            return Character.isDigit(firstChar);
        }
        return false;
    }
}
