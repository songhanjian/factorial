package factorial.link;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author bingyuanlang
 * @date 2020-08-29
 */
public class Solution {
    private final long mode = (1L << 32) - 1;
    private static final long THRESHOLD;
    private final String path = "./link_res.txt";

    static {
        long i = 1;
        long mode = (1L << 32) - 1;
        while (i < mode) {
            i *= 10;
        }
        i /= 10;
        THRESHOLD = i;
    }


    public void factorial(int n) {
        long cutTime = System.currentTimeMillis();
        LinkList linkList = new LinkList(n);
        for (int i = n - 1; i >= 2; i--) {
            linkList.mutiple(i);
        }
        System.out.println("using " + (System.currentTimeMillis() - cutTime));
        linkList.print(path);
    }

    public static class Node {
        long val;
        Node pre;
        Node next;
    }


    public class LinkList {
        private Node root;
        private Node rootHigh;

        public LinkList(int val) {
            this.root = new Node();
            root.val = val;
            this.rootHigh = root;
        }

        public void mutiple(int mul) {
            Node before = null;
            Node cur = root;
            long carry = 0;
            while (true) {
                if (cur == null) {
                    if (carry != 0) {
                        Node node = new Node();
                        node.val = carry;
                        before.pre = node;
                        node.next = before;
                        rootHigh = node;
                    }
                    break;
                }
                long tmp = (cur.val & mode) * mul + carry;
                long newVal = 0;
                if (tmp > THRESHOLD) {
                    carry = tmp / THRESHOLD;
                    newVal = tmp % THRESHOLD;
                } else {
                    carry = 0;
                    newVal = tmp;
                }
                tmp = ((cur.val>>32) & mode) * mul + carry;
                if (tmp > THRESHOLD) {
                    carry = tmp / THRESHOLD;
                    newVal |= (tmp % THRESHOLD)<<32;
                } else {
                    carry = 0;
                    newVal |= tmp<<32;
                }
                cur.val = newVal;
                before = cur;
                cur = cur.pre;
            }
        }


        public void print(String path) {
            try {
                long cutTime = System.currentTimeMillis();
                System.out.println("print tart");
                File file = new File(path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fileWriter);
                Node cur = rootHigh;
                long tmp = (cur.val >> 32) & mode;
                if (tmp != 0) {
                    bw.write(Long.toString(tmp));
                }
                tmp = cur.val & mode;
                bw.write(Long.toString(tmp));
                cur = cur.next;
                while (cur != null) {
                    tmp = (cur.val >> 32) & mode;
                    bw.write(Long.toString(tmp));
                    tmp = cur.val & mode;
                    bw.write(Long.toString(tmp));
                    cur = cur.next;
                }
                bw.close();
                System.out.println("print finish using:" + (System.currentTimeMillis() - cutTime));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String factorial2(int n) {
        long num = 1;
        for (long i = n; i >= 2; i--) {
            num *= i;
        }
        return num + "";
    }


    public static void main(String[] args) {
        Solution s = new Solution();
//        System.out.println(s.factorial2(18));
        long val = Long.valueOf(args[0]);
        System.out.println("input value:"+val);
        s.factorial((int)val);
//        System.out.println(s.factorial(10000));
    }

}
