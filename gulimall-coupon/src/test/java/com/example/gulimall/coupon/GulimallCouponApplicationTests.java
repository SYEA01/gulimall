package com.example.gulimall.coupon;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
//@SpringBootTest
//@RunWith(SpringRunner.class)
public class GulimallCouponApplicationTests {

    @Test
    public void contextLoads() {
        LocalDate now = LocalDate.now();
        LocalDate plus1 = now.plusDays(1);
        LocalDate plus2 = now.plusDays(2);
        System.out.println("now = " + now);
        System.out.println("plus1 = " + plus1);
        System.out.println("plus2 = " + plus2);

        System.out.println("=============================");
        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;
        System.out.println("min = " + min);
        System.out.println("max = " + max);


        System.out.println("=============================");
        LocalDateTime start = LocalDateTime.of(now, min);
        System.out.println("LocalDateTime.of(now,min) = " + start);
        LocalDateTime end = LocalDateTime.of(plus2, max);
        System.out.println("LocalDateTime.of(plus2,max) = " + end);
    }

}
