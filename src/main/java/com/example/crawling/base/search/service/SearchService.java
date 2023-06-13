package com.example.crawling.base.search.service;

import com.example.crawling.base.search.entity.Search;
import com.example.crawling.base.search.entity.SearchKeyword;
import com.example.crawling.base.search.repository.SearchKeywordRepository;
import com.example.crawling.base.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {
    private final SearchRepository searchRepository;
    private final SearchKeywordRepository searchKeywordRepository;

    // 660 19분전
    public void searchHelloAll(WebDriver driver){
        List<WebElement> webElementList;

        List<Search> searchList = new ArrayList<>();

        String url = "https://www.hellomarket.com/search?q=&sort=current";
        try{
            driver.get(url);

            for (int i = 0; i < 10; i++) {
                scrollDown(driver);
                sleep(2000);
            }
            sleep(2000);

            String elementListCssSelector = "[class=\"Item__Wrapper-sc-17ycp52-0 hScXrO\"]";

            waitPageLoading(driver, elementListCssSelector);

            webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

            if (!webElementList.isEmpty()) {
                for (WebElement webElement : webElementList) {
                    String siteLink = webElement.findElement(By.cssSelector("[class=\"Item__ThumbnailBox-sc-17ycp52-1 liZtWH\"] a")).getAttribute("href");
                    String imgLink = webElement.findElement(By.cssSelector("[class=\"Item__ThumbnailBox-sc-17ycp52-1 liZtWH\"] a img")).getAttribute("src");

                    String siteProduct = siteLink.substring(33, 42);

                    WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"Item__TextBox-sc-17ycp52-5 ivArQS\"]"));

                    String price = webElementDetail.findElement(By.cssSelector("[class=\"Item__Text-sc-17ycp52-4 fUCHku\"]")).getText();
                    String title = webElementDetail.findElement(By.cssSelector("[class=\"Item__Text-sc-17ycp52-4 cuyRaw\"]")).getText();
                    String date = webElementDetail.findElement(By.cssSelector("[class=\"Item__TimeTag-sc-17ycp52-9 fxCGUZ\"]")).getText();

                    Search search = createSearch(price, "", imgLink, siteLink, "헬로마켓", siteProduct, date, title);

                    searchList.add(search);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("헬로마켓 전체 크롤링 에러");
        }
        save(searchList);
    }

    public void searchJoongnaAll(WebDriver driver){
        List<WebElement> webElementList;

        List<Search> searchList = new ArrayList<>();

        // 1페이지당 80개 26페이지 = 19분전   2080개
        for (int i = 1; i < 27 ; i++){
            String url = "https://web.joongna.com/search?sort=RECENT_SORT&page=" + i;
            try{
                driver.get(url);

                String ElementListCssSelector = "[class=\"group box-border overflow-hidden flex rounded-md cursor-pointer pe-0 pb-2 lg:pb-3 flex-col items-start transition duration-200 ease-in-out transform hover:-translate-y-1 md:hover:-translate-y-1.5 hover:shadow-product bg-white\"]";

                waitPageLoading(driver, ElementListCssSelector);

                // 게시글
                webElementList = driver.findElements(By.cssSelector(ElementListCssSelector));

                if (!webElementList.isEmpty()){
                    for (WebElement webElement : webElementList){
                        // 게시글 사이트 링크
                        String siteLink = webElement.getAttribute("href");

                        // 광고가 아닌 실제 게시글만 필터링
                        if (!siteLink.startsWith("https://web.joongna")) {
                            continue;
                        }

                        String siteProduct;

                        if (siteLink.contains("detail")) {
                            siteProduct = siteLink.substring(45);
                        } else {
                            siteProduct = siteLink.substring(32);
                        }

                        String imgLink = webElement.findElement(By.cssSelector("[class=\"relative w-full rounded-md overflow-hidden pt-[100%] mb-3 md:mb-3.5\"] img")).getAttribute("src");

                        // 게시글이 올라온 시간과 상품 가격, 게시글 제목, 지역
                        WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"w-full overflow-hidden p-2 md:px-2.5 xl:px-4\"]"));

                        String date;
                        String area;
                        // 중고나라는 registInfo가 2개, 1번이 지역, 2번이 시간
                        List<WebElement> registInfoList = webElementDetail.findElements(By.cssSelector("[class=\"text-sm text-gray-400\"]"));

                        date = registInfoList.get(1).getText();
                        area = registInfoList.get(0).getText();

                        String price = webElementDetail.findElement(By.cssSelector("[class=\"font-semibold space-s-2 mt-0.5 text-heading lg:text-lg lg:mt-1.5\"]")).getText();
                        String title = webElementDetail.findElement(By.cssSelector("[class=\"line-clamp-2 text-sm md:text-base text-heading\"]")).getText();

                        Search search = createSearch(price, area, imgLink, siteLink, "중고나라", siteProduct, date, title);

                        searchList.add(search);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("중고나라 전체 크롤링 에러");
            }
        }
        save(searchList);
    }

    public void crawlingDaangnKeywords(WebDriver driver) {
        List<WebElement> webElementList;
        List<SearchKeyword> searchKeywordList = new ArrayList<>();

        try {
            driver.get("https://www.daangn.com/top_keywords");

            String keywordsList = "#top-keywords-list";
            waitPageLoading(driver, keywordsList);
            WebElement keywordsListElement = driver.findElement(By.cssSelector(keywordsList));

            String keywordCssSelector = "[class=\"keyword-text\"]";

            waitPageLoading(driver, keywordCssSelector);

            webElementList = keywordsListElement.findElements(By.cssSelector(keywordCssSelector));

            if (!webElementList.isEmpty()) {
                searchKeywordRepository.deleteAll();
                for (WebElement webElement : webElementList) {
                    String name = webElement.getText();
                    SearchKeyword searchKeyword = SearchKeyword.builder().name(name).build();
                    searchKeywordList.add(searchKeyword);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchKeywordRepository.saveAll(searchKeywordList);
    }

    public void searchDaangn(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;
        List<Search> searchList = new ArrayList<>();

        // 1키워드당 40개씩
        for (int j = 0; j < keywords.size(); j++) {
            String url = "https://www.daangn.com/search/" + keywords.get(j);
            try {
                driver.get(url);

                String elementListCssSelector = "[class=\"flea-market-article flat-card\"] a";
                String moreBtn = "[class=\"more-btn\"]";

                waitPageLoading(driver, moreBtn);
                WebElement moreBtnElement = driver.findElement(By.cssSelector(moreBtn));
                while (true) {
                    moreBtnElement.click();
                    waitPageLoading(driver, moreBtn);
                    if (moreBtnElement.getAttribute("data-page").equals("3")) {
                        break;
                    }
                }
                waitPageLoading(driver, elementListCssSelector);
                webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

                if (!webElementList.isEmpty()) {
                    for (WebElement webElement : webElementList) {
                        String siteLink = webElement.getAttribute("href");

                        String imgLink = webElement.findElement(By.cssSelector("[class=\"card-photo\"] img")).getAttribute("src");

                        String title = webElement.findElement(By.cssSelector("[class=\"article-title\"]")).getText();

                        String siteProduct = siteLink.substring(32);

                        String price;
                        try {
                            // .replace("원", "").replace(",", "").replace("만", "0000");
                            price = webElement.findElement(By.cssSelector("[class=\"article-price \"]")).getText();
                        } catch (NoSuchElementException e) {
                            price = "-1";
                        }

                        String area = webElement.findElement(By.cssSelector("[class=\"article-region-name\"]")).getText();
                        // 게시글 내용
                        // String content = webElement.findElement(By.cssSelector("[class=\"article-content\"]")).getText();

                        // sellDate는 해당 게시글 페이지로 이동해서 가져옴
                        String sellDate = getDaangnSellDate(driver, siteLink);

                        Search search = createSearch(price, area, imgLink, siteLink, "당근마켓", siteProduct, sellDate, title);

                        searchList.add(search);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("당근마켓 크롤링 에러");
            }
        }

        save(searchList);
    }

    public void searchHello(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;

        List<Search> searchList = new ArrayList<>();

        // 1 키워드당 30개씩
        for (int j = 0; j < keywords.size(); j++) {
            String url = "https://www.hellomarket.com/search?q=" + keywords.get(j) + "&sort=current";
            try {
                driver.get(url);

                String elementListCssSelector = "[class=\"Item__Wrapper-sc-17ycp52-0 hScXrO\"]";

                waitPageLoading(driver, elementListCssSelector);

                webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

                if (!webElementList.isEmpty()) {
                    for (WebElement webElement : webElementList) {
                        String siteLink = webElement.findElement(By.cssSelector("[class=\"Item__ThumbnailBox-sc-17ycp52-1 liZtWH\"] a")).getAttribute("href");
                        String imgLink = webElement.findElement(By.cssSelector("[class=\"Item__ThumbnailBox-sc-17ycp52-1 liZtWH\"] a img")).getAttribute("src");

                        String siteProduct = siteLink.substring(33, 42);

                        WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"Item__TextBox-sc-17ycp52-5 ivArQS\"]"));

                        String price = webElementDetail.findElement(By.cssSelector("[class=\"Item__Text-sc-17ycp52-4 fUCHku\"]")).getText();
                        String title = webElementDetail.findElement(By.cssSelector("[class=\"Item__Text-sc-17ycp52-4 cuyRaw\"]")).getText();
                        String date = webElementDetail.findElement(By.cssSelector("[class=\"Item__TimeTag-sc-17ycp52-9 fxCGUZ\"]")).getText();

                        if (date.contains("개월")) {
                            continue;
                        } else if (date.contains("년")) {
                            continue;
                        }

                        Search search = createSearch(price, "", imgLink, siteLink, "헬로마켓", siteProduct, date, title);

                        searchList.add(search);
                    }
                }
            } catch (Exception e) {
                System.out.println("헬로마켓 크롤링 에러");
                e.printStackTrace();
            }
        }
        save(searchList);
    }

    public void searchBunjang(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;

        List<Search> searchList = new ArrayList<>();
        // 키워드 1개 1페이지 100개
        for (int j = 0; j < keywords.size(); j++) {
            for (int i = 1; i < 3; i++) {
                String url = "https://m.bunjang.co.kr/search/products?order=date&page=" + i + "&q=" + keywords.get(j);
                try {
                    driver.get(url);

                    // 게시글 요소 css Selector
                    String elementListCssSelector = "[class=\"sc-kcDeIU WTgwo\"] a";

                    waitPageLoading(driver, elementListCssSelector);

                    webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

                    if (!webElementList.isEmpty()) {
                        for (WebElement webElement : webElementList) {
                            String siteLink = webElement.getAttribute("href");

                            String siteProduct = siteLink.substring(33, 42);

                            String imgLink = webElement.findElement(By.cssSelector("[class=\"sc-hgHYgh ieNgVs\"] img")).getAttribute("src");

                            String title = webElement.findElement(By.cssSelector("[class=\"sc-gtfDJT brQSgh\"]")).getText();

                            String area = webElement.findElement(By.cssSelector("[class=\"sc-hzDEsm eaWqIm\"]")).getText();

                            String price;

                            // 가격이 적혀있는 글은 가격을 가져오고, 가격에 연락요망 이라고 해놓은 글은 연락요망을 가져온다.
                            try {
                                price = webElement.findElement(By.cssSelector("div [class=\"sc-fOICqy ikGLLE\"]")).getText();
                            } catch (NoSuchElementException e) {
                                price = "-1";
                            }

                            String date = webElement.findElement(By.cssSelector("div [class=\"sc-jtRlXQ kjBXGS\"] span")).getText();

                            if (date.contains("개월")) {
                                continue;
                            }

                            Search search = createSearch(price, area, imgLink, siteLink, "번개장터", siteProduct, date, title);

                            searchList.add(search);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("번개장터 크롤링 에러");
                    e.printStackTrace();
                }
            }
        }
        save(searchList);
    }


    public void searchJoongna(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;
        List<Search> searchList = new ArrayList<>();

        // 키워드 1개 1페이지 80개
        for (int j = 0; j < keywords.size(); j++) {
            for (int i = 1; i < 3; i++) {
                String url = "https://web.joongna.com/search/" + keywords.get(j) + "?sort=RECENT_SORT&page=" + i;

                try {
                    driver.get(url);

                    String ElementListCssSelector = "[class=\"group box-border overflow-hidden flex rounded-md cursor-pointer pe-0 pb-2 lg:pb-3 flex-col items-start transition duration-200 ease-in-out transform hover:-translate-y-1 md:hover:-translate-y-1.5 hover:shadow-product bg-white\"]";

                    waitPageLoading(driver, ElementListCssSelector);

                    // 게시글
                    webElementList = driver.findElements(By.cssSelector(ElementListCssSelector));

                    if (!webElementList.isEmpty()) {
                        for (WebElement webElement : webElementList) {
                            // 게시글 사이트 링크
                            String siteLink = webElement.getAttribute("href");

                            // 광고가 아닌 실제 게시글만 필터링
                            if (!siteLink.startsWith("https://web.joongna")) {
                                continue;
                            }

                            String siteProduct;

                            if (siteLink.contains("detail")) {
                                siteProduct = siteLink.substring(45);
                            } else {
                                siteProduct = siteLink.substring(32);
                            }

                            String imgLink = webElement.findElement(By.cssSelector("[class=\"relative w-full rounded-md overflow-hidden pt-[100%] mb-3 md:mb-3.5\"] img")).getAttribute("src");

                            // 게시글이 올라온 시간과 상품 가격, 게시글 제목, 지역
                            WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"w-full overflow-hidden p-2 md:px-2.5 xl:px-4\"]"));

                            String date;
                            String area;
                            // 중고나라는 registInfo가 2개, 1번이 지역, 2번이 시간
                            List<WebElement> registInfoList = webElementDetail.findElements(By.cssSelector("[class=\"text-sm text-gray-400\"]"));

                            date = registInfoList.get(1).getText();
                            area = registInfoList.get(0).getText();

                            String price = webElementDetail.findElement(By.cssSelector("[class=\"font-semibold space-s-2 mt-0.5 text-heading lg:text-lg lg:mt-1.5\"]")).getText();
                            String title = webElementDetail.findElement(By.cssSelector("[class=\"line-clamp-2 text-sm md:text-base text-heading\"]")).getText();


                            Search search = createSearch(price, area, imgLink, siteLink, "중고나라", siteProduct, date, title);

                            searchList.add(search);
                        }
                    }

                } catch (Exception e) {
                    System.out.println("중고나라 크롤링 에러");
                    e.printStackTrace();
                }
            }
        }
        save(searchList);
    }

    public String getDaangnSellDate(WebDriver driver, String siteLink) {
        String originalHandle = driver.getWindowHandle();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.open('" + siteLink + "', '_blank');");

        // 새로 열린 탭으로 전환
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        String sellDate1 = "[id=\"article-description\"] p time";
        waitPageLoading(driver, sellDate1);

        String sellDate = driver.findElement(By.cssSelector(sellDate1)).getText();

        if (sellDate.contains("끌올")) {
            sellDate = sellDate.replace("끌올", "").trim();
        }

        driver.close(); // 새 탭 닫기
        driver.switchTo().window(originalHandle); // 원래 탭으로 전환

        return sellDate;
    }

    public synchronized List<SearchKeyword> getKeywords() {
        List<SearchKeyword> searchKeywords = searchKeywordRepository.findAll();
        return searchKeywords;
    }

    public synchronized void deleteAllKeywords() {
        searchKeywordRepository.deleteAll();
    }

    // 데이터를 가공하고 빌드 역할을 하는 메서드
    public Search createSearch(String price, String area, String imageLink, String link, String provider, String siteProduct, String sellDate, String title) {

        LocalDateTime dateTime = LocalDateTime.now();
        // 판매 시간 가공
        if (sellDate.contains("초")) {
            int time = Integer.parseInt(sellDate.substring(0, sellDate.indexOf("초")));
            dateTime = dateTime.minusSeconds(time);
        } else if (sellDate.contains("분")) {
            int time = Integer.parseInt(sellDate.substring(0, sellDate.indexOf("분")));
            dateTime = dateTime.minusMinutes(time);
        } else if (sellDate.contains("시간")) {
            int time = Integer.parseInt(sellDate.substring(0, sellDate.indexOf("시")));
            dateTime = dateTime.minusHours(time);
        } else if (sellDate.contains("일")) {
            int time = Integer.parseInt(sellDate.substring(0, sellDate.indexOf("일")));
            dateTime = dateTime.minusDays(time);
        } else if (sellDate.equals("")) {
            dateTime = null;
        }

        // siteProduct 가공
        if (provider.equals("중고나라")) {
            siteProduct = "JG_" + siteProduct;
        } else if (provider.equals("당근마켓")) {
            siteProduct = "DG_" + siteProduct;
        } else if (provider.equals("번개장터")) {
            siteProduct = "BJ_" + siteProduct;
        } else if (provider.equals("헬로마켓")) {
            siteProduct = "HM_" + siteProduct;
        }

        // 가격 데이터 가공
        price = price.replace("원", "").replace(",", "");

        if (price.contains("나눔")) {
            price = "-1";
        } else if (price.contains("요망")) {
            price = "0";
        } else if (price.contains("억")) {
            List<String> priceName = List.of(price.split(" "));
            if (priceName.size() == 1) {
                price = price.replace("억", "00000000");
            } else if (priceName.size() == 2) {
                if (price.contains("만")) {
                    price = price.replace("억 ", "");
                } else {
                    price = price.replace("억 ", "0000");
                }
            } else if (priceName.size() == 3) {
                price = price.replace("억 ", "").replace("만 ", "");
            }
        } else if (price.contains("만")) {
            List<String> priceName = List.of(price.split(" "));
            if (priceName.size() == 1) {
                price = price.replace("만", "0000");
            } else {
                price = price.replace("만 ", "");
            }
        }

        // Search 빌드
        Search search = Search.builder()
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .link(link)
                .sellDate(dateTime)
                .price(Integer.parseInt(price))
                .title(title)
                .area(area)
                .siteProduct(siteProduct)
                .imageLink(imageLink)
                .provider(provider)
                .build();

        return search;
    }


    public synchronized void save(List<Search> searchList) {
        for (Search s : searchList) {
            Search existData = searchRepository.findBySiteProduct(s.getSiteProduct());
            if (existData != null) {
                continue;
            }
            searchRepository.save(s);
        }
    }

    // css selector가 나타날 때까지 페이지 로딩 기다리기(3초)
    private void waitPageLoading(WebDriver driver, String selector) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> driver1.findElement(By.cssSelector(selector)).isDisplayed();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        wait.until(pageLoadCondition);
    }

    private static void scrollDown(WebDriver driver){
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
