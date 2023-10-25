package io.rngesis.internal;

import io.rngesis.test.BaseStatelessUnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

class RNGDefaultModulesTest extends BaseStatelessUnitTest {

    @Test
    void all_default_modules_should_generate_non_null() {
        Random random = new Random();
        RNGesus rnGesus = new RNGesus();
        RNGDefaultModules.all().forEach((type, module) -> {
            Object actual = module.next(rnGesus, random, null);
            Assertions.assertThat(actual).isNotNull();
        });
    }
}
