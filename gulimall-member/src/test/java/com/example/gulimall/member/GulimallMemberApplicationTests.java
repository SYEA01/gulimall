package com.example.gulimall.member;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class GulimallMemberApplicationTests {

    public static void main(String[] args) {
//        String md5 = DigestUtils.md5Hex("123456");
//        System.out.println("md5 = " + md5);
//
//        // 盐值加密：+随机值。   加盐：$1$ + 8位字符
//        // $1$gZ0JHwCL$jJg50eJZKvXQ0g6WugGHC1
//        String s = Md5Crypt.md5Crypt("123456".getBytes(),"$1$qqqqqqqq");
//        System.out.println("s = " + s);

        // $2a$10$uAGBI3QVgGP/hClFWFLEMeud3y8qDS5c9AUEuYQJlV2GJdEVzdC6e
        // $2a$10$dii.SNANv1ch2xwREEegHuKJU3apaFIjZRzfUZTsgJ.dAyZeqdpd2
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println("encode = " + encode);

        boolean matches = passwordEncoder.matches("123456", "$2a$10$dii.SNANv1ch2xwREEegHuKJU3apaFIjZRzfUZTsgJ.dAyZeqdpd2");
        System.out.println("matches = " + matches);


    }
    @Test
    void contextLoads() {


    }

}
