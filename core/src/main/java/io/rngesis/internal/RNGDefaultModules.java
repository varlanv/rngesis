package io.rngesis.internal;

import io.rngesis.api.RNGesisModule;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RNGDefaultModules {

    private static Map<String, RNGesisModule<?>> modules;

    public static Map<String, RNGesisModule<?>> all() {
        if (modules == null) {
            init();
        }
        return modules;
    }

    private static synchronized void init() {
        if (modules != null) {
            return;
        }
        Map<String, RNGesisModule<?>> tmpMap = new HashMap<>();
        tmpMap.put(Integer.class.getName(), (RNGesisModule<Integer>) (rnGesis, random, moduleState) -> random.nextInt());
        tmpMap.put(Long.class.getName(), (RNGesisModule<Long>) (rnGesis, random, moduleState) -> random.nextLong());
        tmpMap.put(String.class.getName(), (RNGesisModule<String>) (rnGesis, random, moduleState) -> rnGesis.nextString());
        tmpMap.put(Boolean.class.getName(), (RNGesisModule<Boolean>) (rnGesis, random, moduleState) -> random.nextBoolean());
        tmpMap.put(Byte.class.getName(), (RNGesisModule<Byte>) (rnGesis, random, moduleState) -> (byte) random.nextInt());
        tmpMap.put(Short.class.getName(), (RNGesisModule<Short>) (rnGesis, random, moduleState) -> (short) random.nextInt());
        tmpMap.put(Float.class.getName(), (RNGesisModule<Float>) (rnGesis, random, moduleState) -> random.nextFloat());
        tmpMap.put(Double.class.getName(), (RNGesisModule<Double>) (rnGesis, random, moduleState) -> random.nextDouble());
        tmpMap.put(Character.class.getName(), (RNGesisModule<Character>) (rnGesis, random, moduleState) -> (char) random.nextInt());
        tmpMap.put(int.class.getName(), (RNGesisModule<Integer>) (rnGesis, random, moduleState) -> random.nextInt());
        tmpMap.put(long.class.getName(), (RNGesisModule<Long>) (rnGesis, random, moduleState) -> random.nextLong());
        tmpMap.put(boolean.class.getName(), (RNGesisModule<Boolean>) (rnGesis, random, moduleState) -> random.nextBoolean());
        tmpMap.put(byte.class.getName(), (RNGesisModule<Byte>) (rnGesis, random, moduleState) -> (byte) random.nextInt());
        tmpMap.put(short.class.getName(), (RNGesisModule<Short>) (rnGesis, random, moduleState) -> (short) random.nextInt());
        tmpMap.put(float.class.getName(), (RNGesisModule<Float>) (rnGesis, random, moduleState) -> random.nextFloat());
        tmpMap.put(double.class.getName(), (RNGesisModule<Double>) (rnGesis, random, moduleState) -> random.nextDouble());
        tmpMap.put(char.class.getName(), (RNGesisModule<Character>) (rnGesis, random, moduleState) -> (char) random.nextInt());
        tmpMap.put(BigInteger.class.getName(), (RNGesisModule<BigInteger>) (rnGesis, random, moduleState) -> new BigInteger(128, random));
        tmpMap.put(BigDecimal.class.getName(), (RNGesisModule<BigDecimal>) (rnGesis, random, moduleState) -> BigDecimal.valueOf(random.nextDouble()));
        tmpMap.put(Date.class.getName(), (RNGesisModule<Date>) (rnGesis, random, moduleState) -> new Date(random.nextLong()));
        tmpMap.put(Instant.class.getName(), (RNGesisModule<Instant>) (rnGesis, random, moduleState) -> Instant.ofEpochMilli(random.nextLong()));
        tmpMap.put(OffsetDateTime.class.getName(), (RNGesisModule<OffsetDateTime>) (rnGesis, random, moduleState) -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(random.nextLong()), ZoneId.systemDefault()));
        tmpMap.put(Timestamp.class.getName(), (RNGesisModule<Timestamp>) (rnGesis, random, moduleState) -> new Timestamp(random.nextLong()));
        modules = Collections.unmodifiableMap(tmpMap);
    }
}
