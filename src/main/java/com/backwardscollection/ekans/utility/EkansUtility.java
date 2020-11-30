package com.backwardscollection.ekans.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class EkansUtility {
    
    // Attributes
    public static int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    public final static Random RANDOM = new Random();
    // public static ExecutorService EXECUTOR = Executors.newUnboundedVirtualThreadExecutor();
    // public static ScheduledExecutorService SCHEDULED_EXECUTOR =
    // Executors.newScheduledThreadPool(Integer.MAX_VALUE, Thread.builder().virtual().factory());
    public static ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    public static ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(10);
    
    /** Write the object to a Base64 string. */
    public static String toString(Serializable o) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    
    /**
     * Write an object to file
     * https://examples.javacodegeeks.com/core-java/io/fileoutputstream/how-to-write-an-object-to-file-in-java/
     * @throws IOException
     */
    
    public static boolean containsAnyTrues(boolean... values){
        for(boolean value : values){
            if(value){
                return true;
            }
        }
        return false;
    }
    
    public static int round(double i, double v){
        return (int) (Math.round(i / v) * v);
    }
    
    public static <T> T[] concatAll(T[] first, T[]... rest){
        int totalLength = first.length;
        for(T[] array : rest){
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for(T[] array : rest){
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
    
    public static boolean isDouble(String s){
        try{
            Double.parseDouble(s);
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    public static boolean isInteger(String s){
        return isInteger(s, 10);
    }
    
    public static String removeLastCharOptional(String s){
        return Optional.ofNullable(s).filter(str -> str.length() != 0)
                .map(str -> str.substring(0, str.length() - 1)).orElse(s);
    }
    
    public static boolean isInteger(String s, int radix){
        if(s.isEmpty())
            return false;
        for(int i = 0; i < s.length(); i++){
            if(i == 0 && s.charAt(i) == '-'){
                if(s.length() == 1)
                    return false;
                else
                    continue;
            }
            if(Character.digit(s.charAt(i), radix) < 0)
                return false;
        }
        return true;
    }
    
    public static int mid(int length){
        if(length == 1){
            return 0;
        }
        
        int midIndex = length;
        if(length % 2 == 0){
            midIndex = length / 2;
        } else{
            midIndex = (length / 2) + 1;
        }
        return midIndex;
    }
    
    // low inclusive : high exclusive
    public static int generateRandomIntInRange(int high, int low){
        return EkansUtility.RANDOM.nextInt(high - low) + low;
    }
    
    public static long generateRandomLongInRange(long high, long low){
        return ThreadLocalRandom.current().nextLong(high - low) + low;
    }
    
    public static double generateRandomDoubleInRange(double high, double low){
        return ThreadLocalRandom.current().nextDouble(high - low) + low;
    }
}
