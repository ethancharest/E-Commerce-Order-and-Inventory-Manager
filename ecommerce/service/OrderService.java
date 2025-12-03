package ecommerce.service;
//this class should handle order processing for customers

import ecommerce.model.Order;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

public class OrderService {

    public String generateOrderId() {
        // Logic to generate a unique order ID
        return "ORD" + System.currentTimeMillis();
    }

    public void processOrder(Order order) throws IOException {
        // Logic to process the order
        FileWriter orderWriter = new FileWriter("ecommerce/data/orders.csv", true);
        FileWriter orderProductsWriter = new FileWriter("ecommerce/data/orderProducts.csv", true);
        orderWriter.append("\n").append(order.getOrderId()).append(",")
                .append(order.getCustomerId()).append(",")
                .append(String.valueOf(order.getTotal())).append(",")
                .append(order.getCreatedAt()).append(",")
                .append(order.getStatus().toString());
        orderWriter.flush();
        orderWriter.close();
        StringJoiner joiner = new StringJoiner(",");
        joiner.add("\n" + order.getOrderId());
        for (var item : order.getItems()) {
            joiner.add(item.getProduct().getId())
                    .add(String.valueOf(item.getQuantity()));
        }
        orderProductsWriter.append(joiner.toString());
        orderProductsWriter.flush();
        orderProductsWriter.close();
    }

}
