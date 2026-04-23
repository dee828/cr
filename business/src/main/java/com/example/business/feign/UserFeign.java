package com.example.business.feign;

import com.example.common.request.TicketRequest;
import com.example.common.response.R;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user", url = "http://127.0.0.1:8080")
public interface UserFeign {

    @GetMapping("/ticket/saveByFeign")
    public R<Boolean> saveByFeign(@RequestBody @Valid TicketRequest request);

}
