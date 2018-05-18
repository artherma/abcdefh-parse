package main.util;

import main.AppInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析google play 上的 html页面
 */
public class ParseUtil {

    /**
     * itemprop
     * @param html
     * @return
     */
    public static AppInfo parse(String html){
        AppInfo appInfo = new AppInfo();

        if(html != null && !html.isEmpty()){
            org.jsoup.nodes.Document document = Jsoup.parse(html);

//            app文件在Google Play上展示的URL
            String url = document.select("#body-content > meta").first().attr("content");
            appInfo.setPlayUrl(ContenUtils.getFormatUrl(url));
            appInfo.setId(ContenUtils.getAppId(url));

//            app文件的icon图片 链接 URL
            String imageUrl = document.select("#body-content > div > div > div.main-content > div:nth-child(1) > div > div.details-info > div > div.cover-container > img").first().attr("src");
            imageUrl = ContenUtils.getFormatUrl(imageUrl);
            appInfo.setCoverImageUrl(imageUrl);

//            app 名称（附带少量介绍文字）
            String idAppTitle = document.select("div.id-app-title").first().text().trim();
            appInfo.setIdAppTitle(idAppTitle);

//            app开发者 和 类别 [社交、游戏、工具等]
            Element authorEle = document.select("div.body-content>div.outer-container>div>div.main-content>div>div>div.details-info>div>div>div>div.left-info").first();
            String author = "";
            String genre = "";
            if(authorEle != null && authorEle.select("a").size() > 1){
                author = authorEle.select("a").get(0).select("span[itemprop=name]").first().text().trim();
                String devErUrl = authorEle.select("a").get(0).attr("href");
                System.out.println("dev url >> "+"https://play.google.com"+devErUrl);
                genre = authorEle.select("a").get(1).select("span[itemprop=genre]").first().text().trim();
            }
            appInfo.setAuthor(author);
            appInfo.setGenre(genre);

            // @TODO 注意，app是否免费，价格多少
            boolean isFreeApp = true;
            String offerPriceStr = document.select("#body-content > div > div > div.main-content > div:nth-child(1) > div > div.details-info > div > div.info-box-bottom > div > div.details-actions-right > span > span > button > span:nth-child(3)").first().text().replace(",","");
            System.out.println("offerPriceStr >> "+offerPriceStr);
            Double offerPrice = 0d;
            if(offerPriceStr != null && offerPriceStr.equalsIgnoreCase("install")){
                offerPrice = 0d;
            }else {
                isFreeApp = false;
//            CN¥26.70 Buy @TODO 未区分货币种类，一般是$
//                $4.99 Buy
                String priceNum = ContenUtils.getRegContent(offerPriceStr,"(\\d+\\.\\d+)",1);
                if(offerPriceStr.contains("$")){
                    offerPrice  = Double.parseDouble(priceNum);
                }else if(offerPriceStr.contains("¥")){
                    offerPrice  = Double.parseDouble(priceNum);
                }
                System.out.println("offerPrice >> "+offerPrice);
            }
            appInfo.setFree(isFreeApp);
            appInfo.setOffersPrice(offerPrice);

//            屏幕截图
            boolean hasScreenShot = true;
            Element screenShotEle = document.select("div.body-content > div.outer-container > div.inner-container > div.main-content > div>div>div.details-section.screenshots>div.details-section-contents>div.details-section-body.expandable>div.thumbnails-wrapper>div.thumbnails").first();
            List<String> scrrenShotList = new ArrayList<String>();
            int screenShotSizeReal = 0;
            if(screenShotEle != null ){
                screenShotSizeReal = screenShotEle.select("img.screenshot").size();
                if(screenShotSizeReal == 0){
                    hasScreenShot = false;
                }
                for (int i = 0; i < screenShotSizeReal; i++) {
                    Element tmpScreenImg = screenShotEle.select("img.screenshot").get(i);
                    String tmpStr = ContenUtils.getFormatUrl(tmpScreenImg.attr("src").trim());
                    scrrenShotList.add(tmpStr);
                    System.out.println("shot >> " +tmpStr);
                }
            }else {
                hasScreenShot = false;
            }

            appInfo.setHasScreenShot(hasScreenShot);

//            app 详细介绍
            Element descriptionEle = document.select("div.body-content > div.outer-container > div.inner-container > div.main-content > div>div>div>div>div>div[itemprop=description]>div").first();
            String decriptionStr = "";
            if(descriptionEle != null){
                decriptionStr = descriptionEle.text().trim();
            }
            appInfo.setDescription(decriptionStr);

//            评分信息，包括4.0111,投票数量，最后显示多少4.0
//            5星评论数量、4星评论、3星、2星、1星数量
            Element aggregateRatingEle = document.select("#body-content > div > div > div.main-content > div.details-wrapper.apps > div.details-section.reviews > div.details-section-contents > div.rating-box").first();
            Element histogram = null;
            Double ratingValue = 0d;
            int ratingCount = 0;
            Double score = 0d;
            int ratingBarFive = 0;
            int ratingBarFour = 0;
            int ratingBarThree = 0;
            int ratingBarTwo = 0;
            int ratingBarOne = 0;
            if(aggregateRatingEle != null){
                ratingValue = Double.parseDouble(aggregateRatingEle.select("div.score-container").first().select("meta").get(0).attr("content").trim().replace(",",""));
                ratingCount = Integer.parseInt(aggregateRatingEle.select("div.score-container").first().select("meta").get(1).attr("content").trim().replace(",",""));
                score = Double.parseDouble(aggregateRatingEle.select("div.score-container").first().select("div.score").first().text().trim().replace(",",""));

                histogram = aggregateRatingEle.select("div.rating-histogram").first();
            }
            appInfo.setRatingValue(ratingValue);
            appInfo.setRatingCount(ratingCount);
            appInfo.setScore(score);

            if(aggregateRatingEle != null && histogram != null){
//          5星评论
                String ratingBarFiveStr = histogram.select("div.rating-bar-container.five")
                        .first().select("span.bar-number").first().text().trim();
                ratingBarFive = Integer.parseInt(ratingBarFiveStr.replace(",",""));

                String ratingBarFourStr = histogram.select("div.rating-bar-container.four").
                        first().select("span.bar-number").first().text().trim();
                ratingBarFour = Integer.parseInt(ratingBarFourStr.replace(",",""));

                String ratingBarThreeStr = histogram.select("div.rating-bar-container.three")
                         .first().select("span.bar-number").first().text().trim();
                ratingBarThree = Integer.parseInt(ratingBarThreeStr.replace(",",""));

                String ratingBarTwoStr = histogram.select("div.rating-bar-container.two")
                        .first().select("span.bar-number").first().text().trim();
                ratingBarTwo = Integer.parseInt(ratingBarTwoStr.replace(",",""));

                String ratingBarTOneStr = histogram.select("div.rating-bar-container.one")
                        .first().select("span.bar-number").first().text().trim();
                ratingBarOne = Integer.parseInt(ratingBarTOneStr.replace(",",""));
            }

            appInfo.setRatingCountFive(ratingBarFive);
            appInfo.setRatingCountFour(ratingBarFour);
            appInfo.setRatingCountThree(ratingBarThree);
            appInfo.setRatingCountTwo(ratingBarTwo);
            appInfo.setRatingCountOne(ratingBarOne);

//            what's new 新版变化
            String whatsNewStrResult = "";
            StringBuilder whatsNewStr = new StringBuilder();
            Element whatsNewEle = document.select("#body-content > div > div > div.main-content > div:nth-child(3) > div").first();
            if(whatsNewEle != null ){
                Elements recent_changes = whatsNewEle.select("div.details-section-contents.show-more-container").first().select("div.recent-change");
                if(recent_changes != null){
                    for (Element tmpRecentChanges: recent_changes) {
                        whatsNewStr.append(tmpRecentChanges.text().trim());
                    }
                }
            }
            whatsNewStrResult = whatsNewStr.toString();
            appInfo.setWhatsNew(whatsNewStrResult);

//          @其他信息，注意，有的app没有
            String datePublished = "";
            String fileSize = "";
            long numDownloadsLeft = 0,numDownloadsRight = 0;
            String numDownloads = "";
            String softwareVersion = "";
            String operatingSystems = "";
            String contentRating ="";
            String interactiveElementStr = "";
            String offeredBy = "";
            String priceInAppStr = "";
            Element developersInfoEle = null;
            Element additionalInformationEle = document.select("#body-content > div > div > div.main-content > div.details-wrapper.apps-secondary-color > div > div.details-section-contents").first();
            if(additionalInformationEle != null){
                Elements divMetaInfos = additionalInformationEle.select("div.meta-info");
                developersInfoEle = divMetaInfos.select("div>div:contains(Developer)").next().first();
//                必须循环
                for (Element tmpDivMetaInfo : divMetaInfos) {
                    String title = tmpDivMetaInfo.select("div.title").first().text();
                    if(title.contains("Updated")){
        //            发行|发布 日期 March 21, 2018
                        datePublished = tmpDivMetaInfo.select("div[itemprop=datePublished]").first().text().trim();
                        datePublished = ContenUtils.getAppPublishDate(datePublished);
                    } else if(title.contains("Current Version")){
        //            版本号 ]Varies with device
                        softwareVersion = tmpDivMetaInfo.select("div[itemprop=softwareVersion]").first().text().trim();
                    } else if(title.contains("Requires Android")){
        //            操作系统要求 ]Varies with device
                        operatingSystems = tmpDivMetaInfo.select("div[itemprop=operatingSystems]").first().text().trim();
                    }else if(title.contains("Content Rating")){
        //            内容分级 ]Everyone
                        contentRating = tmpDivMetaInfo.select("div[itemprop=contentRating]").first().text().trim();
                    }else if(title.contains("Installs")){
        //            下载次数]1,000,000,000 - 5,000,000,000
                        numDownloads = tmpDivMetaInfo.select("div[itemprop=numDownloads]").first().text().trim().replace(",","").replaceAll("\\s","");
                    }else if(title.contains("Permissions")){
                        // @TODO
        //            权限隐私 @TODO
                        System.out.println("权限隐私 >> "+" where ?? ");
                    }else if(title.contains("Offered By")){
//                    offered by ]Facebook
                        offeredBy = divMetaInfos.select("div>div:contains(Offered By)").next().text().trim();
                        System.out.println("offered by >> "+offeredBy);
                    }else if(title.contains("Developer")){

                    }else if(title.contains("Interactive Elements")){
//                    交互元素 ]Users Interact, Shares Info, Shares Location
                        interactiveElementStr = divMetaInfos.select("div>div:contains(Interactive Elements)").next().text().trim();
                        System.out.println("interactiveElementStr >> "+interactiveElementStr);
                    }else if(title.equalsIgnoreCase("Size")){
//                        文件大小 @TODO app给出的文件大小
                        fileSize = tmpDivMetaInfo.select("div[itemprop=fileSize]").first().text().trim();
                        System.out.println("fileSize >> "+fileSize);
                    }else if(title.equalsIgnoreCase("In-app Products")){
//                        商店内购项目价格
                        priceInAppStr = tmpDivMetaInfo.select("div.content").text().trim();
                        System.out.println("In-app Products >> "+ priceInAppStr);
                    }

                }

            }
            appInfo.setDatePublished(datePublished);
            appInfo.setSoftwareVersion(softwareVersion);
            appInfo.setOperatingSystem(operatingSystems);
            appInfo.setContentRatingTitle(contentRating);
            appInfo.setInteractive(interactiveElementStr);
            appInfo.setPriceInApp(priceInAppStr);

            if(numDownloads.contains("-")){
                String[] ranges = numDownloads.split("-");
                numDownloadsLeft = Long.parseLong(ContenUtils.getFormatInteger(ranges[0]));
                numDownloadsRight = Long.parseLong(ContenUtils.getFormatInteger(ranges[1]));
            }
            appInfo.setNumDownloadsLeft(numDownloadsLeft);
            appInfo.setNumDownloadsRight(numDownloadsRight);

//            Developer
            String devWebSiteUrl = "";
            String devEmail = "";
            String privacyPolicy = "";
            String devAddress = "";
            if(developersInfoEle != null){
//                https://www.google.com/url?q=http://www.facebook.com/apps/application.php?id%3D256002347743983&sa=D&usg=AFQjCNFWPjchjyv4a5zZErJcQy-hDTEIgA
                if(developersInfoEle.text().contains("Visit website")){
                    devWebSiteUrl = developersInfoEle.select("div>a:contains(Visit website)").first().attr("href");
                }
                if(developersInfoEle.text().contains("Email")){
    //                mailto:android-support@fb.com
                    devEmail = developersInfoEle.select("div>a:contains(Email)").first().attr("href");
                    devEmail = ContenUtils.getMail(devEmail);
                }
                if(developersInfoEle.text().contains("Privacy Policy")){
    //                https://www.google.com/url?q=https://m.facebook.com/policy.php&sa=D&usg=AFQjCNGOyB7C649RUYna8HUqeqWjDLg0zA
                    privacyPolicy = developersInfoEle.select("div>a:contains(Privacy Policy)").first().attr("href");
                }
//                1 Hacker Way Menlo Park, CA 94025
                devAddress = developersInfoEle.select("div.content.physical-address").first().text();
            }

            appInfo.setDevWebSiteUrl(devWebSiteUrl);
            appInfo.setDevEmail(devEmail);
            appInfo.setPrivacyPolicyUrl(privacyPolicy);
            appInfo.setDevAddress(devAddress);

            return appInfo;
        }else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception{

//        String html = ContenUtils.readAsString("E:\\bhtml.html");
        String html = ContenUtils.readAsString("E:\\uhtml.html");
        AppInfo appinfo = parse(html);
        if(appinfo != null){
            System.out.println(appinfo.toString());
        }


    }

}
