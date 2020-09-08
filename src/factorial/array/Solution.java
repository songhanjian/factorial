package factorial.array;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bingyuanlang
 * @date 2020-09-07
 */
public class Solution {
    private final String path = "./array_res.txt";
    private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public String factorial(long i) {
        BigInteger integer = BigInteger.valueOf(i);
        long cur = System.currentTimeMillis();
        for (long j = i - 1; j >= 1; j--) {
            integer = integer.multiply(BigInteger.valueOf(j));
        }
        System.out.println("cal finish,using:" + (System.currentTimeMillis() - cur));
        return integer.toString(10);
    }

    public void execute(int index, int curLen,int limit, BigInteger[] firstRound,AtomicInteger integer){
        service.submit(()->{
            firstRound[index] = firstRound[index].multiply(firstRound[curLen - 1 - index]);
            firstRound[curLen-1-index] = null;
            int exe = integer.incrementAndGet();
            if(exe == limit){
                synchronized (this.getClass()){
                    this.getClass().notify();
                }
            }
        });
    }

    public void factorial2(long num) throws Exception{
        long cur = System.currentTimeMillis();
        BigInteger[] firstRound = new BigInteger[(int) num / 2];
        if ((num & 1) == 1) {
            for (int i = 0; i < num / 2; i++) {
                firstRound[i] = BigInteger.valueOf((i + 2) * (num - i));
            }
        } else {
            for (int i = 0; i < num / 2; i++) {
                firstRound[i] = BigInteger.valueOf((i + 1) * (num - i));
            }
        }
        int len = firstRound.length;
        BigInteger tmp = BigInteger.valueOf(1);
        while (len > 1) {
            if((len & 1)==1){
                tmp = tmp.multiply(firstRound[len-1]);
                firstRound[len-1] = null;
                len--;
            }
            int limit = len/2;
            for (int i = 0; i < limit; i++) {
                firstRound[i] = firstRound[i].multiply(firstRound[len - 1 - i]);
                firstRound[len-1-i] = null;
            }
            len = len / 2;
        }
        firstRound[0] = firstRound[0].multiply(tmp);
        System.out.println("cal finish,using:" + (System.currentTimeMillis() - cur));
        print(path,firstRound[0]);

    }

    public void factorial3(long num) throws Exception{
        long cur = System.currentTimeMillis();
        BigInteger[] firstRound = new BigInteger[(int) num / 2];
        if ((num & 1) == 1) {
            for (int i = 0; i < num / 2; i++) {
                firstRound[i] = BigInteger.valueOf((i + 2) * (num - i));
            }
        } else {
            for (int i = 0; i < num / 2; i++) {
                firstRound[i] = BigInteger.valueOf((i + 1) * (num - i));
            }
        }
        int len = firstRound.length;
        BigInteger tmp = BigInteger.valueOf(1);
        while (len > 1) {
            if((len & 1)==1){
                tmp = tmp.multiply(firstRound[len-1]);
                firstRound[len-1] = null;
                len--;
            }
            int limit = len/2;
            AtomicInteger integer = new AtomicInteger(0);
            for (int i = 0; i < limit; i++) {
//                firstRound[i] = firstRound[i].multiply(firstRound[len - 1 - i]);
//                firstRound[len-1-i] = null;
                execute(i,len,limit,firstRound,integer);
            }
            synchronized (this.getClass()){
                int get = integer.get();
                if(get<limit){
                    this.getClass().wait();
                }
            }
            len = len / 2;
        }
        service.shutdownNow();
        firstRound[0] = firstRound[0].multiply(tmp);
        System.out.println("cal finish,using:" + (System.currentTimeMillis() - cur));
        print(path,firstRound[0]);

    }

    public void print(String path, BigInteger bigInteger){
            try {
                long cutTime = System.currentTimeMillis();
                System.out.println("print tart");
                File file = new File(path);
                if(!file.exists()){
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write(bigInteger.toString());
                bw.close();
                System.out.println("print finish using:"+(System.currentTimeMillis()-cutTime));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }



    public static void main(String[] args) throws Exception{
        Solution so = new Solution();
        long mills = System.currentTimeMillis();
//         so.factorial2(1000000);
//         String s = so.factorial(200);
//        System.out.println(s);
//        System.out.println(s1);
        long val = Long.valueOf(args[0]);
        boolean useMultiThread = Boolean.valueOf(args[1]);
        System.out.println("input value:"+val);
        System.out.println("multi thread mode:"+useMultiThread);
        if(!useMultiThread){
            so.factorial2(val);
        } else {
            so.factorial3(val);
        }
        System.out.println("using:" + (System.currentTimeMillis() - mills));
    }
}
