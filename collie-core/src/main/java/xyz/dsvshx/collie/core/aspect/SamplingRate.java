package xyz.dsvshx.collie.core.aspect;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author dongzhonghua
 * Created on 2021-05-22
 */
public class SamplingRate {
    private final static LongAdder SAMPLING_RATE = new LongAdder();
    private static int CUR_SAMPLING_RATE = 10;
    private static int BASE_SAMPLING_RATE = 100;

    public static boolean needSampling() {
        if (SAMPLING_RATE.intValue() >= BASE_SAMPLING_RATE) {
            SAMPLING_RATE.reset();
        }
        SAMPLING_RATE.increment();
        return SAMPLING_RATE.intValue() <= CUR_SAMPLING_RATE;
    }
}
