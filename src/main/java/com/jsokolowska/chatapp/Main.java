package com.jsokolowska.chatapp;

public class Main {

    public static void main(String[] args) {
        String text = "[Joanna]: \\create private";

        int index = text.indexOf("\\");
        String groupName = text.substring(index + 1);
        System.out.println(groupName);
    }
}
