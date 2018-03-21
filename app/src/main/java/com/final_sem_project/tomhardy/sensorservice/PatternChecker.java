package com.final_sem_project.tomhardy.sensorservice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternChecker {

    private static Pattern namePattern = Pattern.compile("^[a-zA-ZàáâäãåacceèéêëeiìíîïlnòóôöõøùúûüuuÿýzzñçcšžÀÁÂÄÃÅACCEEÈÉÊËÌÍÎÏILNÒÓÔÖÕØÙÚÛÜUUŸÝZZÑßÇŒÆCŠŽ?ð ,.'-]+$");
    private static Pattern addressPattern = Pattern.compile("^[a-zA-Z0-9àáâäãåacceèéêëeiìíîïlnòóôöõøùúûüuuÿýzzñçcšžÀÁÂÄÃÅACCEEÈÉÊËÌÍÎÏILNÒÓÔÖÕØÙÚÛÜUUŸÝZZÑßÇŒÆCŠŽ?ð ,:\\-;.'-]+$");
    private static Pattern datePattern = Pattern.compile("^[a-zA-Z ]+$");
    private static Pattern stringPattern = Pattern.compile("^[a-zA-Z]+$");
    private static Pattern contactPattern = Pattern.compile("^[0-9\\+\\-()]+$");
    private static Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    private static Pattern passwordPattern = Pattern.compile("[\\w\\d`~!@#$%^&\\+\\*\\-\\/=]{6,}");
    private static Matcher matcher;

    public static Boolean isName(String name) {
        matcher = namePattern.matcher(name);
        return matcher.matches();
    }

    public static Boolean isAddress(String address) {
        matcher = addressPattern.matcher(address);
        return matcher.matches();
    }

    public static Boolean isDate(String date) {
        matcher = datePattern.matcher(date);
        return matcher.matches();
    }

    public static Boolean isString(String s) {
        matcher = stringPattern.matcher(s);
        return matcher.matches();
    }

    public static Boolean isContact(String contact) {
        matcher = contactPattern.matcher(contact);
        return matcher.matches();
    }

    public static Boolean isEmail(String email) {
        matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public static Boolean isPassword(String password) {
        matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }
}