package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.ControllerDTO;
import com.dynamicwebservice.dto.ControllerResponseDTO;
import com.dynamicwebservice.service.CommonService;
import com.dynamicwebservice.service.DynamicControllerService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200")
public class DynamicLoadController {

    private final DynamicControllerService dynamicControllerService;

    private final CommonService commonService;

    public DynamicLoadController(DynamicControllerService dynamicControllerService,
                                 CommonService commonService) {
        this.dynamicControllerService = dynamicControllerService;
        this.commonService = commonService;
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

        StringBuilder errorMessage = new StringBuilder();

        if (StringUtils.isBlank(request.getPublishUri())) {
            errorMessage.append("PublishUri is blank; ");
        }
        if (StringUtils.isBlank(request.getClassPath())) {
            errorMessage.append("ClassPath is blank; ");
        }

        if (!errorMessage.isEmpty()) {
            log.error(errorMessage.toString());
            return Result.failure(ResultStatus.BAD_REQUEST);
        }
        dynamicControllerService.saveController(request);
        ControllerResponseDTO response = new ControllerResponseDTO();
        BeanUtils.copyProperties(request, response);

        return Result.success(response);
    }

    @PostMapping("/updateController")
    public Result<ControllerResponseDTO> updateController(@RequestBody ControllerDTO request) {
        log.info("Update controller: {}", request);
        ControllerDTO newControllerDTO = new ControllerDTO();
        try {
            ControllerDTO originalDTO = dynamicControllerService.getController(request.getId());
            dynamicControllerService.disabledController(originalDTO.getPublishUri(), false);

            BeanUtils.copyProperties(request, newControllerDTO);
            newControllerDTO.setIsActive(false);
            dynamicControllerService.updateController(newControllerDTO);

            if (!originalDTO.getPublishUri().equals(newControllerDTO.getPublishUri())) {
                dynamicControllerService.removeController(originalDTO.getPublishUri());
                commonService.updateMockResponse(originalDTO.getPublishUri(), newControllerDTO.getPublishUri());
            }

        } catch (Exception e) {
            log.error("Update web service failed:{}", e.getMessage(), e);
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        ControllerResponseDTO response = new ControllerResponseDTO();
        BeanUtils.copyProperties(newControllerDTO, response);
        return Result.success(response);
    }

    @DeleteMapping("/removeController")
    public Result<String> removeWebService(@RequestBody String[] publishUris) {
        for (String publishUri : publishUris) {
            dynamicControllerService.removeController(publishUri);
        }
        return Result.success(StringUtils.EMPTY);
    }

    @GetMapping("/switchController")
    public Result<String> switchWebService(@RequestParam String publishUri, @RequestParam Boolean isActive) {
        if (StringUtils.isNotBlank(publishUri) && isActive != null) {
            if (isActive) {
                dynamicControllerService.enabledController(publishUri);
            } else {
                dynamicControllerService.disabledController(publishUri, false);
            }
        }
        return Result.success(StringUtils.EMPTY);
    }

}
