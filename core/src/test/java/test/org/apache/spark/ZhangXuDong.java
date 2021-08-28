package test.org.apache.spark;

import org.junit.Test;

import java.io.IOException;

public class ZhangXuDong {

    @Test
    public void main() throws IOException {

        System.out.println(Runtime.getRuntime().totalMemory()/1024/1024);
        System.out.println(Runtime.getRuntime().freeMemory()/1024/1024);
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println(Runtime.getRuntime().maxMemory()/1024/1024/1024);

    }
}
