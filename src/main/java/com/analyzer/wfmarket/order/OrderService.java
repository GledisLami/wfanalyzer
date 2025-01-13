package com.analyzer.wfmarket.order;

import com.analyzer.wfmarket.frame.Frame;
import com.analyzer.wfmarket.util.CustomMailSender;
import com.analyzer.wfmarket.util.FileService;
import com.analyzer.wfmarket.util.HttpClientService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @PostConstruct
    public void init() {
        try {
            collectDataForAllFrames();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Logger logger = org.slf4j.LoggerFactory.getLogger(OrderService.class);
    private final String url;
    private final HttpClientService httpClientService;
    private final CustomMailSender mailSender;
    private final List<String> parts;
    private List<Frame> frames;
    private List<Frame> validFrames;
    private final String mailCc;

    public OrderService(HttpClientService httpClientService,
                        CustomMailSender mailSender,
                        @Value("${mail.cc}") String mailCc) {
        this.frames = new ArrayList<>();
        this.validFrames = new ArrayList<>();
        this.mailCc = mailCc;
        this.url = "http://api.warframe.market/v1";
        this.httpClientService = httpClientService;
        this.mailSender = mailSender;
        this.parts = List.of("blueprint", "neuroptics_blueprint", "chassis_blueprint", "systems_blueprint");
    }

    public void collectDataForAllFrames() throws InterruptedException {
        // Init list of frames
        this.frames = getFrames();
        StringBuilder csvFile = prepareCsvBody();
        validFrames = new ArrayList<>();
        processFrames(frames);
        if (validFrames.isEmpty()) {
            System.out.println("No valid frames found");
            return;
        }
        sortFrames(validFrames);
        buildCsvFile(csvFile, validFrames);
        //sendMail(csvFile);
        saveFile("analysis.csv", csvFile.toString());
        Thread.sleep(750);
    }

    private void processFrames(List<Frame> frames) throws InterruptedException {
        for (Frame frame : frames) {
            processFrame(frame);
        }
    }

    private void processFrame(Frame frame) throws InterruptedException {
        logger.info("Getting data for frame: {}", frame.getName());
        // Retrieve the orders for each part
        int partsPrice = 0;
        String anomalities = "";
        for (String element : parts) {
            //Remove orders that have status != ingame
            logger.info("Getting data for part: {}", element);
            List<Order> topOrders = filterAndSortOrderList(getOrder(frame.getName() + element).getPayload().getOrders());
            if (topOrders.size() < 5) {
                logger.info("Not enough orders for part: {}", element);
                return;
            }
            // get the average price
            int elementAvgPrice = topOrders.stream().mapToInt(Order::getPlatinum).sum() / topOrders.size();

            // Check if first price is anomaly
            if (topOrders.get(1).getPlatinum() - topOrders.get(0).getPlatinum() >= 5) {
                anomalities += ("~" + frame.getAnomalies() + element + ":" + topOrders.getFirst().getPlatinum() + "/" + topOrders.get(1).getPlatinum());
            }
            partsPrice += elementAvgPrice;
            Thread.sleep(150);
        }
        logger.info("Total parts price: {}", partsPrice);

        // After getting each part, get the set
        List<Order> setOrders = filterAndSortOrderList(
                getOrder(frame.getName() + "set").getPayload().getOrders()
        );

        if (setOrders.size() < 5) {
            logger.info("Not enough orders for set: {}", frame.getName());
            return;
        }

        double setPrice = (double) setOrders.stream().mapToInt(Order::getPlatinum).sum() / setOrders.size();
        logger.info("Total set price: {}",setPrice);

        String profitMargin = String.format(
                "%+,.2f%%",
                ((setPrice - partsPrice) / partsPrice) * 100
        );

        double platDifference = setPrice - partsPrice;

        Frame f = Frame.builder()
                .name(frame.getName())
                .anomalies(anomalities)
                .partsPrice(partsPrice)
                .setPrice(setPrice)
                .profitMargin(profitMargin)
                .platDifference(platDifference)
                .build();

        validFrames.add(f);
        logger.info("Done with frame: {}\n", frame.getName());
    }

    private static StringBuilder prepareCsvBody() {
        return new StringBuilder().append("Warframe name,Set Price, Total Parts Price,Profit Margin,Plat difference,Price Anomalities\n");
    }

    private void saveFile(String filePath, String content) {
        FileService.writeFile(filePath, content);
    }


    private void sendMail(StringBuilder csvFile) throws InterruptedException {
        logger.info("Sending mail with csv attachment");
        mailSender.sendMail(
                "warframeanalyzer@gmail.com",
                "Warframe Market Analysis",
                "",
                mailCc,
                csvFile.toString().getBytes()
        );
        Thread.sleep(1000);
    }

    public List<Frame> getFrames() {
        return FileService.readFile("frames.txt").stream().map(frame -> frame.toLowerCase() + "_prime_").map(Frame::new).toList();
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

        List<Order> filteredOrders = orders.stream()
                .filter(order1 -> order1.getUser().getStatus().equals("ingame")
                        && order1.getOrder_type().equals("sell"))
                .collect(Collectors.toList());  // Collect to a mutable list


        filteredOrders.sort(Comparator.comparingInt(Order::getPlatinum));

        if (filteredOrders.size() < 5) {
            return filteredOrders;
        }
        return filteredOrders.subList(0, 5);
    }

    public void sortFrames(List<Frame> frames) {
        frames.sort(Comparator.comparingDouble(Frame::getPlatDifference).reversed());
    }

    public void buildCsvFile(StringBuilder csvFile, List<Frame> frames) {
        for (Frame frame : frames) {
            csvFile.append(frame);
        }
    }
}
