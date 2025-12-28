package com.example.scheduler.feign;

import com.example.common.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@FeignClient(name = "business", url = "http://localhost:8081")
public interface BusinessFeign {
    @GetMapping("hi")
    String hi();

    @GetMapping("admin/daily-train/gen-daily/{date}")
    R<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);
}
