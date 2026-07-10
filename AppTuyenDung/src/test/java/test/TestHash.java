package test;

import utils.PasswordUtil;

public class TestHash {
    public static void main(String[] args) {
        String hash = PasswordUtil.hashPassword("123456");
        System.out.println(hash);
    }
}