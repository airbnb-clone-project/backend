package com.airbnb_clone.pin.scheduler;

import com.airbnb_clone.pin.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.airbnb_clone.pin.scheduler
 * fileName       : PinScheduler
 * author         : ipeac
 * date           : 24. 9. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 10.        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
public class PinScheduler {
    private final PinService pinService;

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteTempPins() {
        pinService.cacheAllPinsToRedis();
    }
}
