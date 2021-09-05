package com.example.pi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ConvolutedPiApplication {

    public static final int MAX    = 1000000000;
    public static final int ITER   = 50000000;
    public static final int PARALL = 20;

    public static void main(String[] args) {
        SpringApplication.run(ConvolutedPiApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            StopWatch timer = new StopWatch();
            timer.start();
            List<CompletableFuture<Integer>> futures = new ArrayList<>(PARALL);
            for (int i = 0; i < PARALL; i++) {
                futures.add(CompletableFuture.supplyAsync(() -> countToPi(ThreadLocalRandom.current())));
            }
            long counter = futures.stream().mapToLong(CompletableFuture::join).sum();
            double pi = pi(counter);
            timer.stop();
            double err = Math.abs(pi / Math.PI - 1);
            double time = timer.getTotalTimeSeconds();
            log.info("MAX: {}, ITER: {}, counter: {}, PI: {}, err:{}, time: {}", MAX, ITER * PARALL, counter, pi, err, time);
        };
    }

    private int countToPi(Random generator) {
        int counter = 0;
        for (int i = 0; i < ITER; i++) {
            var a = random(generator);
            var b = random(generator);
            counter += coprime(a, b);
        }
        return counter;
    }

    private int coprime(BigInteger a, BigInteger b) {
        return a.gcd(b).intValueExact() == 1 ? 1 : 0;
    }

    BigInteger random(Random generator) {
        return BigInteger.valueOf(generator.nextInt(MAX) + 1);
    }

    double pi(long counter) {
        return Math.sqrt(6d * ITER * PARALL / counter);
    }

}
