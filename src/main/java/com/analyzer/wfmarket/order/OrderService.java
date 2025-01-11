package com.analyzer.wfmarket.order;

import com.analyzer.wfmarket.frame.Frame;
import com.analyzer.wfmarket.util.CustomMailSender;
import com.analyzer.wfmarket.util.FileReaderService;
import com.analyzer.wfmarket.util.HttpClientService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(OrderService.class);
    private final String url;
    private final HttpClientService httpClientService;
    private final CustomMailSender mailSender;
    private final List<String> parts;
    private List<Frame> frames;
    private final String mailCc;

    public OrderService(HttpClientService httpClientService,
                        CustomMailSender mailSender,
                        @Value("${mail.cc}") String mailCc) {
        this.frames = new ArrayList<>();
        this.mailCc = mailCc;
        this.url = "http://api.warframe.market/v1";
        this.httpClientService = httpClientService;
        this.mailSender = mailSender;
        this.parts = List.of("blueprint", "neuroptics_blueprint", "chassis_blueprint", "systems_blueprint");
    }

    public void collectData() throws InterruptedException {
        this.frames = getFrames();
        boolean willSendCsvReportMail = false;
        StringBuilder csvFile = prepareCsvBody();
        List<Frame> validFrames = new ArrayList<>();
        for (Frame frame : frames) {
            logger.info("Getting data for frame: {}", frame.getName());
            boolean notEnoughParts = false;
            // Retrieve the orders for each part
            for (String element : parts) {
                //Remove orders that have status != ingame
                logger.info("Getting data for part: {}", element);
                List<Order> topOrders = filterAndSortOrderList(getOrder(frame.getName() + element).getPayload().getOrders());
                logger.info("Top Orders: {} ", topOrders);
                if (topOrders.size() < 5) {
                    logger.info("Not enough orders for part: {}", element);
                    notEnoughParts = true;
                    break;
                }
                // get the average price
                int elementAvgPrice = topOrders.stream().mapToInt(Order::getPlatinum).sum() / topOrders.size();
                logger.info("Average price for part: {} is: {}", element, elementAvgPrice);

                // Check if first price is anomaly
                if (topOrders.get(0).getPlatinum() < topOrders.get(1).getPlatinum() * 0.7){
                    frame.setAnomalies("~" + frame.getAnomalies()+element + ":" + topOrders.getFirst().getPlatinum() + "/" + topOrders.get(1).getPlatinum());
                }
                frame.setPartsPrice(frame.getPartsPrice()+elementAvgPrice);
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
            frame.setProfitMargin(profitMargin);
            logger.info("Profit margin: {}", profitMargin);
            frame.setPlatDifference(frame.getSetPrice() - frame.getPartsPrice());
            validFrames.add(frame);
            willSendCsvReportMail = true;

            logger.info("Done with frame: {}\n", frame.getName());
        }

        sortFrames(validFrames);
        buildCsvFile(csvFile, validFrames);
        sendMails(willSendCsvReportMail, csvFile);
        Thread.sleep(1000);
    }

    private static StringBuilder prepareCsvBody() {
        return new StringBuilder().append("Warframe name,Set Price, Total Parts Price,Profit Margin,Plat difference,Price Anomalities\n");
    }

    private void sendMails(boolean willSendCsvReportMail, StringBuilder csvFile) throws InterruptedException {
        if (willSendCsvReportMail) {
            logger.info("Sending mail with csv attachment");
            mailSender.sendMail(
                    "warframeanalyzer@gmail.com",
                    "Warframe Market Analysis",
                    "",
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
                .filter(order1 -> order1.getUser().getStatus().equals("ingame")
                        && order1.getOrder_type().equals("sell"))
                .collect(Collectors.toList());  // Collect to a mutable list

        logger.info("Filtered orders: {}", filteredOrders.size());

        filteredOrders.sort(Comparator.comparingInt(Order::getPlatinum));

        if (filteredOrders.size() < 5) {
            return filteredOrders;
        }
        return filteredOrders.subList(0, 5);
    }

    public void sortFrames(List<Frame> frames){
        frames.sort(Comparator.comparingDouble(Frame::getPlatDifference).reversed());
    }

    public void buildCsvFile(StringBuilder csvFile, List<Frame> frames){
        for (Frame frame : frames){
            csvFile.append(frame);
        }
    }
}
