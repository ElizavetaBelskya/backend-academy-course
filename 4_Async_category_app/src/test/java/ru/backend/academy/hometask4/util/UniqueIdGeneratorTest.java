package ru.backend.academy.hometask4.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UniqueIdGeneratorTest {
    private UniqueIdGenerator uniqueIdGenerator  = new UniqueIdGenerator();

    @ParameterizedTest
    @ValueSource(ints = {10, 30, 50, 100})
    public void test_concurrent_unique_id_generation(int threadCount) throws Exception {
        Set<String> generatedIds = new HashSet<>();
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startSignal.await();
                    String uniqueId = uniqueIdGenerator.generateUniqueId();
                    generatedIds.add(uniqueId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneSignal.countDown();
                }
            });
        }
        startSignal.countDown();
        doneSignal.await();
        assert generatedIds.size() == threadCount;
    }

}
