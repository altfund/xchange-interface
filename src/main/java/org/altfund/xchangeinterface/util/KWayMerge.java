package org.altfund.xchangeinterface.util;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.knowm.xchange.dto.marketdata.OrderBook;

public class KWayMerge {


    public KWayMerge() {
    }

    //TODO don't need ArrayList the whole time, normal list would suffice
    public List<LimitOrder> mergeKLists(ArrayList<List<LimitOrder>> newBooks) {
        if (newBooks == null || newBooks.size() == 0)
            return null;

        ArrayList<LimitOrderList> books = null;
        for (int i = 0; i < books.size(); i++){
            books.add(new LimitOrderList(newBooks.get(i)));
        }

        PriorityQueue<LimitOrderList> queue = new PriorityQueue<>(books.size(), new KComparator());
        LimitOrder order;

        for (int i = 0; i < books.size(); i++) {
            order = books.next();
            if (order != null)
                queue.offer(order);
        }

        ListNode head = new ListNode(0);
        ListNode lastNode = head;

        List<LimitOrder> sorted = new LinkedList<LimitOrder>();
        List<LimitOrder> lastNode = sorted;

        int i = 0;
        while (!queue.isEmpty()) {
            List<LimitOrder> node = queue.poll();
            lastNode.next = node;

            sorted = queue.poll();

            sorted.add(queue.poll());

            if (node.next != null)
                queue.offer(node.next);

            lastNode = node;
            i++;
        }
        return head.next;
    }

}

class LimitOrderList{
    int index = 0;
    List<LimitOrder> list;
    public LimitOrderList(List<LimitOrder> list) {
        this.list = list;
    }
    public LimitOrder next() {
        index = index + 1;
        return list.get(index);
    }
}

static class KComparator implements Comparator<List<LimitOrder>> {
    public int compare(LimitOrder L1, LimitOrder L2) {
        return L1.compareTo(L2);
    }
}
}
