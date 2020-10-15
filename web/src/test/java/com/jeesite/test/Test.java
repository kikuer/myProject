package com.jeesite.test;

import com.jeesite.common.lang.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: jeesite4
 * @Package: com.jeesite.test
 * @ClassName: Test
 * @Author: Zigzag
 * @Description: ${description}
 * @Date: 2020/10/10 21:36
 */
public class Test {
    public static void main(String[] args) {
        int a = 0;

        String[] attr = new String[5];
        for (int i=0; i<attr.length; i++){
            attr[i] = "attr" + String.valueOf(i);
        }
        for (String s : attr) {
            System.out.println(s);
        }

    }
}
