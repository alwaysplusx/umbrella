package com.harmony.umbrella.log.support;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class BufferedLogWriterTest {

    private static final AtomicInteger total = new AtomicInteger(0);

    private static final int times = 10;
    private static final int count = 100;
    private static final CountDownLatch begin = new CountDownLatch(count);
    private static final CountDownLatch end = new CountDownLatch(count);

    private static LogWriter writer;

    @BeforeClass
    public static void beforeClass() {
        writer = new BufferedLogWriter();
    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < count; i++) {
            new Thread(new TestRunnable()).start();
            begin.countDown();
        }
        end.await();
        System.out.println("total write: " + total.get());
    }

    // private static final List<Object> list = new CopyOnWriteArrayList<>();
    //
    // @Test
    // public void testCopyOnWriteArrayList() throws InterruptedException {
    // for (int i = 0; i < count; i++) {
    // new Thread(new CopyOnWriteArrayListRunable()).start();
    // begin.countDown();
    // }
    // end.await();
    // System.out.println("list size: " + list.size());
    // }
    //
    // private static class CopyOnWriteArrayListRunable implements Runnable {
    //
    // @Override
    // public void run() {
    // for (int i = 0; i < times; i++) {
    // list.add(new Object());
    // System.out.println(Thread.currentThread().getName() + " add " + i);
    // }
    // end.countDown();
    // for (Object object : list.toArray()) {
    // if (object == null) {
    // System.err.println("null object");
    // }
    // }
    // }
    //
    // }

    private static class TestRunnable implements Runnable {

        @Override
        public void run() {
            try {
                begin.await();
                for (int i = 0; i < times; i++) {

                    LogInfo info = LogMessage//
                            .create(Logs.getLog())//
                            .message("a" + i)//
                            .asInfo();
                    writer.write(info);
                }
            } catch (InterruptedException e) {
            } finally {
                end.countDown();
            }
        }

    }

    private static class BufferedLogWriter extends AbstractBufferedLogWriter {

        public BufferedLogWriter() {
            super(5);
        }

        @Override
        protected void writeInternal(LogInfo info) {
            total.incrementAndGet();
            System.out.println(Thread.currentThread().getName() + ": write " + info.getMessage());
        }

        @Override
        protected void writeInternal(List<LogInfo> infos) {
            int size = infos.size();
            total.addAndGet(size);
            for (LogInfo info : infos) {
                if (info == null) {
                    System.err.println(infos);
                } else {
                    info.getMessage();
                }
            }
            int size2 = infos.size();
            if (size2 != size) {
                System.err.println("deff size scope " + size + ", " + size2);
            }
            System.out.println(Thread.currentThread().getName() + ": batch write " + infos.size());
        }

    }

}
