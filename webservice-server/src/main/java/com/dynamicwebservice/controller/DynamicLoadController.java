package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.ControllerDTO;
import com.dynamicwebservice.dto.ControllerResponseDTO;
import com.dynamicwebservice.service.DynamicControllerService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200")
public class DynamicLoadController {

    private final DynamicControllerService dynamicControllerService;

    public DynamicLoadController(DynamicControllerService dynamicControllerService) {
        this.dynamicControllerService = dynamicControllerService;
    }

    @GetMapping("/getControllers")
    public Result<List<ControllerResponseDTO>> getControllers() {
        List<ControllerDTO> controllerDTOS = dynamicControllerService.getControllers();
        List<ControllerResponseDTO> controllerResponseDTOList = controllerDTOS.stream().map(controllerDTO -> {
            ControllerResponseDTO response = new ControllerResponseDTO();
            BeanUtils.copyProperties(controllerDTO, response);
            return response;
        }).toList();
        return Result.success(controllerResponseDTOList);
    }

    @PostMapping("/saveController")
    public Result<ControllerResponseDTO> saveController(@RequestBody ControllerDTO request) {
        log.info("Save web service: {}", request);

        if (StringUtils.isBlank(request.getPublishUri())) {
            log.error("PublishUri is blank");
        }
        if (StringUtils.isBlank(request.getClassPath())) {
            log.error("ClassPath is blank");
        }

        if (StringUtils.isBlank(request.getPublishUri()) ||
                StringUtils.isBlank(request.getClassPath())) {
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        try {
            dynamicControllerService.saveController(request);
        } catch (Exception e) {
            log.error("Register controller failed", e);
            return Result.failure(ResultStatus.BAD_REQUEST);
        }
        ControllerResponseDTO response = new ControllerResponseDTO();
        BeanUtils.copyProperties(request, response);

        return Result.success(response);
    }

}
