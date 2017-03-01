package com.tomtom.places.misc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterruptibleRegexExample
{

    public static void main(String[] args) throws InterruptedException
    {
        final Pattern pattern = Pattern.compile("(0*)*A");
        final String input = "00000000000000000000000000";

        Runnable runnable = new Runnable() {

            @Override
            public void run()
            {
                long startTime = System.currentTimeMillis();
                Matcher interruptableMatcher = pattern.matcher(new InterruptibleCharSequence(input));
                interruptableMatcher.find(); // runs for a long time!
                System.out.println("Regex took:" + (System.currentTimeMillis() - startTime) + "ms");
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Thread.sleep(500);
        thread.interrupt();
    }
}

class InterruptibleCharSequence implements CharSequence {

    CharSequence inner;

    public InterruptibleCharSequence(CharSequence inner) {
        super();
        this.inner = inner;
    }

    @Override
    public char charAt(int index) {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Interrupted!");
        }
        return inner.charAt(index);
    }

    @Override
    public int length() {
        return inner.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new InterruptibleCharSequence(inner.subSequence(start, end));
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}