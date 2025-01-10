package com.analyzer.wfmarket.order;

import com.analyzer.wfmarket.frame.Frame;
import com.analyzer.wfmarket.util.CustomMailSender;
import com.analyzer.wfmarket.util.FileReaderService;
import com.analyzer.wfmarket.util.HttpClientService;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(OrderService.class);
    private final String url;
    private final HttpClientService httpClientService;
    private final CustomMailSender mailSender;
    private final List<String> parts;
    private final List<Frame> frames;
    private final double requiredProfitMarginMailAlert;
    private final String mailCc;

    public OrderService(HttpClientService httpClientService,
                        CustomMailSender mailSender,
                        @Value("${required.profit.margin.mail.alert}") String requiredProfitMarginMailAlert,
                        @Value("${mail.cc}") String mailCc) {
        this.frames = getFrames();
        this.requiredProfitMarginMailAlert = Double.parseDouble(requiredProfitMarginMailAlert);
        this.mailCc = mailCc;
        this.url = "http://api.warframe.market/v1";
        this.httpClientService = httpClientService;
        this.mailSender = mailSender;
        this.parts = List.of("blueprint", "neuroptics_blueprint", "chassis_blueprint", "systems_blueprint");
    }

    public void collectData() throws InterruptedException {
        boolean willSendHighProfitMail = false;
        boolean willSendCsvReportMail = false;
        StringBuilder highProfitMailBody = prepareMailBody();
        StringBuilder csvFile = prepareCsvBody();

        for (Frame frame : frames) {
            logger.info("Getting data for frame: {}", frame.getName());
            boolean notEnoughParts = false;
            double platDifference;
            // Retrieve the orders for each part
            for (String element : parts) {
                //Remove orders that have status != ingame
                logger.info("Getting data for part: {}", element);
                List<Order> topOrders = filterAndSortOrderList(getOrder(frame.getName() + element).getPayload().getOrders());

                if (topOrders.size() < 5) {
                    logger.info("Not enough orders for part: {}", element);
                    notEnoughParts = true;
                    break;
                }
                // get the average price
                int averagePrice = topOrders.stream().mapToInt(Order::getPlatinum).sum() / topOrders.size();
                logger.info("Average price for part: {} is: {}", element, averagePrice);
                // todo: check if first price is anomaly
                frame.setPartsPrice(frame.getPartsPrice()+averagePrice);
                Thread.sleep(250);
            }

            if (notEnoughParts) {
                logger.info("Not enough parts for frame: {}", frame.getName());
                // Continue to the next frame
                continue;
            }

            logger.info("Total parts price: {}", frame.getPartsPrice());

            // After getting each part, get the set
            List<Order> setOrders = filterAndSortOrderList(getOrder(frame.getName() + "set").getPayload().getOrders());

            if (setOrders.size() < 5) {
                logger.info("Not enough orders for set: {}", frame.getName());
                continue;
            }

            frame.setSetPrice((double) setOrders.stream().mapToInt(Order::getPlatinum).sum() / setOrders.size());
            logger.info("Set: {}, price: {}", frame.getName(), frame.getSetPrice());
            double profitMarginD = (frame.getSetPrice() - frame.getPartsPrice()) / frame.getPartsPrice();
            String profitMargin = String.format("%+,.2f%%", profitMarginD * 100);
            logger.info("Profit margin: {}", profitMargin);
            platDifference = (int) frame.getSetPrice() - frame.getPartsPrice();
            String csvText = frame.getName() + "," + frame.getSetPrice() + "," + frame.getPartsPrice() + "," + profitMargin + "," + platDifference +"\n";
            csvFile.append(csvText);
            willSendCsvReportMail = true;

            if (profitMarginD > requiredProfitMarginMailAlert || profitMarginD < requiredProfitMarginMailAlert * -1) {
                logger.info("High profit margin detected for frame: {}", frame.getName());
                willSendHighProfitMail = true;
                highProfitMailBody.append(" <tr> <td>").append(frame.getName()).append("</td> <td>").append(frame.getSetPrice()).append("</td> <td>").append(frame.getPartsPrice()).append("</td> <td>").append(profitMargin).append("</td> <td>").append(LocalDateTime.now()).append("</td> <td>").append(platDifference).append(" </tr>");
            }
            logger.info("Done with frame: {}\n", frame.getName());
        }

        sendMails(willSendHighProfitMail, highProfitMailBody, willSendCsvReportMail, csvFile);
        Thread.sleep(1000);
    }

    private static StringBuilder prepareCsvBody() {
        StringBuilder csvFile = new StringBuilder().append("Warframe name,Set Price, Total Parts Price,Profit Margin,Plat difference\n");
        return csvFile;
    }

    private static StringBuilder prepareMailBody() {
        StringBuilder highProfitMailBody = new StringBuilder().append("""
                <table border="1" style="border-collapse: collapse; width: 100%;">
                    <tr>
                        <th>Warframe name</th>
                        <th>Set price</th>
                        <th>Total parts price</th>
                        <th>Profit margin</th>
                        <th>Time of detection</th>
                        <th>Plat difference</th>
                    </tr>
                    """
        );
        return highProfitMailBody;
    }

    private void sendMails(boolean willSendHighProfitMail, StringBuilder highProfitMailBody, boolean willSendCsvReportMail, StringBuilder csvFile) throws InterruptedException {
        // If there are frames found in the analysis, willSendCsvReportMail will be true.
        // If there are frames with high profit margin, willSendHighProfitMail will be true.
        // willSendHighProfitMail cannot be true without willSendCsvReportMail being true.
        String mailBody = "";
        if (willSendHighProfitMail){
            mailBody = "<h2>High profit Opportunity:</h2><br>" + Jsoup.parse(highProfitMailBody.toString()).outerHtml();
        }

        if (willSendCsvReportMail) {
            logger.info("Sending mail with csv attachment");
            mailSender.sendMail(
                    "warframeanalyzer@gmail.com",
                    "Warframe Market Analysis",
                    mailBody,
                    mailCc,
                    csvFile.toString().getBytes()
            );
        }
        Thread.sleep(1000);
    }

    public List<Frame> getFrames() {
        return FileReaderService.readFile("frames.txt").stream().map(frame -> frame.toLowerCase() + "_prime_").map(Frame::new).toList();
    }

    public OrderResponse getOrder(String partName) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Platform", "pc");
        // headers.put("Accept-Encoding", "gzip, deflate, br");
        HttpResponse<String> response = httpClientService.sendHttpRequest(url + "/items/" + partName + "/orders", HttpMethod.GET, null, headers);
        return OrderResponse.fromJson(response.body());
    }

    public List<Order> filterAndSortOrderList(List<Order> orders) {
        if (orders.size() < 5) {
            return orders;
        }
        logger.info("Filtering and sorting {} orders", orders.size());

        List<Order> filteredOrders = orders.stream()
                .filter(order1 -> order1.getUser().getStatus().equals("ingame"))
                .collect(Collectors.toList());  // Collect to a mutable list

        logger.info("Filtered orders: {}", filteredOrders.size());

        filteredOrders.sort(Comparator.comparingInt(Order::getPlatinum));

        if (filteredOrders.size() < 5) {
            return filteredOrders;
        }
        return filteredOrders.subList(0, 5);
    }
}
