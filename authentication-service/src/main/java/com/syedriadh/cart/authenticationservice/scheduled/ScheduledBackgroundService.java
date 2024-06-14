package com.syedriadh.cart.authenticationservice.scheduled;

import com.syedriadh.cart.authenticationservice.repository.auth.UserTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Log4j2
@Transactional("authTransactionManager")
@Service
public class ScheduledBackgroundService {
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public ScheduledBackgroundService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    protected void purgeExpiredTokens() {
        this.userTokenRepository.deleteExpiredTokens();
        log.info("Purged expired tokens.");
    }
}
