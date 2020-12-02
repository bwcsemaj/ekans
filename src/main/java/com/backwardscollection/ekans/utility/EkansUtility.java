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
    public final static Random RANDOM = new Random();
  
    // low inclusive : high exclusive
    
    /**
     * Generates random integer with in a range
     * @param high : exclusive end of range
     * @param low : inclusive start of range
     * @return random integer between low-high
     */
    public static int generateRandomIntInRange(int high, int low){
        return EkansUtility.RANDOM.nextInt(high - low) + low;
    }
}
