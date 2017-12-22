package org.altfund.xchangeinterface.util;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;


import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;

import org.altfund.xchangeinterface.xchange.model.LimitOrderExchange;

@Slf4j
public class KWayMerge {


    public KWayMerge() {
    }

    //TODO don't need ArrayList the whole time, normal list would suffice
    public List<LimitOrderExchange> mergeKLists(ArrayList<List<LimitOrder>> newBooks, ArrayList<String> exchanges) {
        if (newBooks == null || newBooks.size() == 0){
            log.warn("new books null or of size 0");
            return null;
        }

        ArrayList<LimitOrderNode> books = new ArrayList<LimitOrderNode>();
        for (int i = 0; i < newBooks.size(); i++){
            books.add(new LimitOrderNode(newBooks.get(i), exchanges.get(i)));
            //log.debug("Adding exchange: {}", newBooks.get(i));
        }

        PriorityQueue<LimitOrderNode> queue = new PriorityQueue<>(books.size(), new KComparator());
        LimitOrder order;
        LimitOrderNode orderList;

        for (int i = 0; i < books.size(); i++) {
            orderList = books.get(i);
            if (orderList != null)
                queue.offer(orderList);
        }

        List<LimitOrderExchange> sorted = new ArrayList<LimitOrderExchange>();
        LimitOrder lo = null;
        String exchange = "";

        while (!queue.isEmpty()) {
            LimitOrderNode node = queue.poll();

            lo = node.getCurrent();
            exchange = node.getExchange();
            LimitOrderExchange loe = new LimitOrderExchange(exchange, lo);

            sorted.add(loe);

            if (!node.isEmpty())
                queue.offer(node.next());
        }

        log.debug("done sorting: {} exchanges", newBooks.size());
        return sorted;
    }

}

class LimitOrderNode{
    int index = 0;
    List<LimitOrder> list;
    String exchange;
    public LimitOrderNode(List<LimitOrder> list, String exchange) {
        this.list = list;
        this.exchange = exchange;
    }
    public LimitOrderNode next() {
        index = index + 1;
        return this;
    }
    public String getExchange(){
        return exchange;
    }
    public LimitOrder getCurrent(){
        return list.get(index);
    }
    public boolean isEmpty() {
        return list.size() == index + 1;
    }
    public int compareTo(LimitOrderNode limitOrderNode) {
        return getCurrent().compareTo(limitOrderNode.getCurrent());
    }
}

class KComparator implements Comparator<LimitOrderNode> {
    public int compare(LimitOrderNode L1, LimitOrderNode L2) {
        return L1.compareTo(L2);
    }
}
