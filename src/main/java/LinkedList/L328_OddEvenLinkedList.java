package LinkedList;

import Utils.Helpers.ListNode;

import static Utils.Helpers.*;

/*
* Odd Even Linked List
*
* - Given a singly linked list, group all odd-index nodes together followed by the even-index nodes.
*   注意索引从1开始（即第一个元素的索引为1）。
* */

public class L328_OddEvenLinkedList {
    public static ListNode oddEvenList(ListNode head) {
        ListNode odd = new ListNode(), oddHead = odd;
        ListNode even = new ListNode(), evenHead = even;
        ListNode curr = head;

        for (int i = 0; curr != null; i++, curr = curr.next) {
            if (i % 2 == 0) {
                odd.next = curr;
                odd = odd.next;
            } else {
                even.next = curr;
                even = even.next;
            }
        }

        even.next = null;
        odd.next = evenHead.next;
        return oddHead.next;
    }

    public static void main(String[] args) {
        ListNode l1 = createLinkedListFromArray(new int[]{1, 2, 3, 4, 5});
        printLinkedList(oddEvenList(l1));  // expects 1->3->5->2->4->NULL

        ListNode l2 = createLinkedListFromArray(new int[]{2, 1, 3, 5, 6, 4, 7});
        printLinkedList(oddEvenList(l2));  // expects 2->3->6->7->1->5->4->NULL
    }
}
