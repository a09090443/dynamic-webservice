package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.ControllerDTO;
import com.dynamicwebservice.service.DynamicControllerService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class DynamicLoadController {

    private final DynamicControllerService dynamicControllerService;

    public DynamicLoadController(DynamicControllerService dynamicControllerService) {
        this.dynamicControllerService = dynamicControllerService;
    }

    @PostMapping("/registerController")
    public Result<String> registerController(@RequestBody ControllerDTO controllerDTO) {
        try {
            dynamicControllerService.register(controllerDTO);
            return Result.success("");
        } catch (Exception e) {
            return Result.failure(ResultStatus.BAD_REQUEST);
        }
    }

}
