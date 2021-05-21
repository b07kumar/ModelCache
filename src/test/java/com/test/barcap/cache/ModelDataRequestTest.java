package com.test.barcap.cache;

import com.test.barcap.cache.pojo.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ModelDataRequestTest {

    @BeforeEach
    public void init()
    {
        ModelCache.clear();
    }
    @Test
    @Order(1)
    void testRetrieveSingleModel() throws InterruptedException {
        ModelDataService modelDataService = new ModelDataService();
        modelDataService.updateModel(Arrays.asList(new Model("1 HK", 100.00), new Model("5 HK", 200.00)));
        assertAll(
                () -> assertEquals( Arrays.asList(new Model("NA HK", null)), modelDataService.request(Arrays.asList("NA HK")))
                ,() -> assertEquals( Arrays.asList(new Model("1 HK", 100.00)), modelDataService.request(Arrays.asList("1 HK")))
        );
    }

    @Test
    @Order(2)
    public void testRetrieveMultipleModel() throws InterruptedException {
        ModelDataService modelDataService = new ModelDataService();
        modelDataService.updateModel(Arrays.asList(new Model("1 HK", 100.00)
                                                    , new Model("5 HK", 200.00)
                                                    ,new Model("7 HK", 300.00)));
        assertAll(
                () -> assertEquals( Arrays.asList(new Model("NA HK", null),new Model("1 HK", 100.00)), modelDataService.request(Arrays.asList("NA HK","1 HK")))
                ,() -> assertEquals( Arrays.asList(new Model("1 HK", 100.00),new Model("5 HK", 200.00)), modelDataService.request(Arrays.asList("1 HK","5 HK")))
        );
    }

    @Test
    @Order(3)
    public void testRetrieveMultipleModelWhileUpdating() throws InterruptedException {
        ModelDataService modelDataService = new ModelDataService();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        try {
            Model model4 = new Model("1 HK", 200.00);
            Model model5 = new Model("5 HK", 300.00);
            modelDataService.updateModel(Arrays.asList(model4, model5));
            CountDownLatch latch = new CountDownLatch(100);
            Runnable updatetask = () -> {
                try {
                    Model model1 = new Model("1 HK", ThreadLocalRandom.current().nextDouble(100.00, 105.00));
                    Model model2 = new Model("5 HK", ThreadLocalRandom.current().nextDouble(100.00, 105.00));
                    modelDataService.updateModel(Arrays.asList(model1, model2));
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            executorService.scheduleAtFixedRate(updatetask, 0, 2, TimeUnit.MILLISECONDS);
            latch.await();
            assertAll(
                    () -> assertTrue(modelDataService.request(Arrays.asList("NA HK")).get(0).getTarget() == null)
                    , () -> assertTrue(modelDataService.request(Arrays.asList("1 HK", "5 HK")).stream().filter(x -> x.getTarget() <= 105).count() == 2)
            );
        }
        finally {
            executorService.shutdown();
        }
    }
}
