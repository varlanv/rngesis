package io.rngesis.internal;

import java.util.Random;

public class RNGDefaultTypes {

    private static final char[] ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BYTE_MASK = 0x3F; // This mask will limit the value between 0 and 63, since 2^6 = 64

    public Integer nextInt(Random random) {
        return random.nextInt();
    }

    public Long nextLong(Random random) {
        return random.nextLong();
    }

    public String nextString(Random random) {
        return Adjectives.ADJECTIVES.get(random.nextInt(Adjectives.ADJECTIVES.size())) + Nouns.NOUNS.get(random.nextInt(Nouns.NOUNS.size()));
    }
}
