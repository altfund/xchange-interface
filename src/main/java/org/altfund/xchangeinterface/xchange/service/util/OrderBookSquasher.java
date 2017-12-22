package org.altfund.xchangeinterface.xchange.service.util;

import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.marketdata.OrderBook;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Slf4j
public class OrderBookSquasher {

    public static OrderBook byPrice(OrderBook orderBook) {
        List<LimitOrder> asks = orderBook.getAsks();
        List<LimitOrder> bids = orderBook.getBids();
        Date timestamp = orderBook.getTimeStamp();

        return new OrderBook(timestamp,
                            aggregateLimitOrders(asks),
                            aggregateLimitOrders(bids));
    }

    private static List<LimitOrder> aggregateLimitOrders(List<LimitOrder> orders) {
        List<LimitOrder> aggregatedOrders = new ArrayList<LimitOrder>();
        LimitOrder currLo = null;
        boolean firstAccum = true;
        aggregatedOrders.add(orders.get(0));

        int size = 0;
        for (int i = 1; i < orders.size(); i++) {
            aggregatedOrders.add(orders.get(i));
            size = aggregatedOrders.size();

            if (aggregatedOrders.get(size - 2).getLimitPrice().compareTo(aggregatedOrders.get(size - 1).getLimitPrice()) == 0){
                aggregatedOrders = combineConsecutivePriceMatch(aggregatedOrders, size - 1);
            }
        }

        return aggregatedOrders;
    }

    public static List<LimitOrder> combineConsecutivePriceMatch(List<LimitOrder> orders, int i) {
        int firstOrder = i - 1;
        int secondOrder = i;
        LimitOrder order1 = orders.get(firstOrder);
        LimitOrder order2 = orders.get(secondOrder);
        orders.remove(secondOrder);
        orders.remove(firstOrder);
        orders.add(
                    new LimitOrder.Builder(order1.getType(), order1.getCurrencyPair())
                    .originalAmount(order1.getOriginalAmount().add(order2.getOriginalAmount()))
                    .limitPrice(order1.getLimitPrice())
                    .build()
                );
        return orders;
    }
}
