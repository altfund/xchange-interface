package org.altfund.xchangeinterface.util;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;

import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;

@Slf4j
public class KWayMerge {


    public KWayMerge() {
    }

    //TODO don't need ArrayList the whole time, normal list would suffice
    public List<LimitOrder> mergeKLists(ArrayList<List<LimitOrder>> newBooks) {
        if (newBooks == null || newBooks.size() == 0){
            log.debug("new books null or of size 0");
            return null;
        }

        ArrayList<LimitOrderNode> books = new ArrayList<LimitOrderNode>();
        for (int i = 0; i < newBooks.size(); i++){
            books.add(new LimitOrderNode(newBooks.get(i)));
            log.debug("Adding exchange: {}", newBooks.get(i));
        }

        PriorityQueue<LimitOrderNode> queue = new PriorityQueue<>(books.size(), new KComparator());
        LimitOrder order;
        LimitOrderNode orderList;

        for (int i = 0; i < books.size(); i++) {
            orderList = books.get(i);
            if (orderList != null)
                queue.offer(orderList);
        }

        //ListNode head = new ListNode(0);
        //ListNode lastNode = head;

        List<LimitOrder> sorted = new ArrayList<LimitOrder>();

        while (!queue.isEmpty()) {
            LimitOrderNode node = queue.poll();

            sorted.add(node.getCurrent());

            if (!node.isEmpty())
                queue.offer(node.next());
        }
        return sorted;
    }

}

class LimitOrderNode{
    int index = 0;
    List<LimitOrder> list;
    public LimitOrderNode(List<LimitOrder> list) {
        this.list = list;
    }
    public LimitOrderNode next() {
        index = index + 1;
        return this;
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
