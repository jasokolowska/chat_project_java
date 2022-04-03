package com.jsokolowska.chatapp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        String text = "[Joanna]: \\create private";

        int index = text.indexOf("\\");
        String groupName = text.substring(index + 1);
        System.out.println(new SimpleDateFormat().format(new Date()));
    }
}
