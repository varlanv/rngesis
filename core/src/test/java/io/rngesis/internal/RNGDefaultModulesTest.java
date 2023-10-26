package io.rngesis.internal;

import io.rngesis.test.BaseStatelessUnitTest;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

class RNGDefaultModulesTest extends BaseStatelessUnitTest {

    @Test
    void all_default_modules_should_generate_non_null() {
        val random = new Random();
        val rnGesus = new RNGesus();
        RNGDefaultModules.all().forEach((type, module) -> {
            val actual = module.next(rnGesus, random);
            Assertions.assertThat(actual).isNotNull();
        });
    }
}
