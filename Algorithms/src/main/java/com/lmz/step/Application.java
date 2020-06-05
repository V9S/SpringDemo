package com.lmz.step;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZLM
 * @date 2020/06/03
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println(1.6%2147483647);
        System.out.println(step(5));
    }
    /**
     * 爬楼梯算法，假设你正在爬楼梯。需要 n 阶你才能到达楼顶。每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
     * @param n
     * @return
     */
    private static int step(int n) {
        if (n <= 2) {
            return n;
        } else {
            int resutl = 0;
            int one = 1%2147483647;
            int two = 2%2147483647;
            for (int i = 2; i < n; i++) {
                resutl = one + two;
                one = two;
                two = resutl;
            }
            return resutl;
        }

    }

}
