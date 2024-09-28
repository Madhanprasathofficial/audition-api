package com.audition;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class BaseTest {

    private static final Random RANDOM = new Random();

    protected BaseTest() {
    }

    protected int randomInt() {
        return RANDOM.nextInt();
    }

    protected String randomString() {
        return UUID.randomUUID().toString();
    }
}
