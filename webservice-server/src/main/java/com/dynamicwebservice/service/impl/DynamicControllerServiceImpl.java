package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dto.ControllerDTO;
import com.dynamicwebservice.exception.ControllerException;
import com.dynamicwebservice.jdbc.ControllerJDBC;
import com.dynamicwebservice.model.ControllerModel;
import com.dynamicwebservice.repository.ControllerRepository;
import com.dynamicwebservice.service.DynamicControllerService;
import com.zipe.enums.ResourceEnum;
import com.zipe.jdbc.criteria.Conditions;
import com.zipe.util.classloader.CustomClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DynamicControllerServiceImpl implements DynamicControllerService {

    private final ApplicationContext context;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final ControllerRepository controllerRepository;

    private final ControllerJDBC controllerJDBC;

    public DynamicControllerServiceImpl(ApplicationContext context,
                                        RequestMappingHandlerMapping requestMappingHandlerMapping,
                                        ControllerRepository controllerRepository, ControllerJDBC controllerJDBC) {
        this.context = context;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.controllerRepository = controllerRepository;
        this.controllerJDBC = controllerJDBC;
    }

    @Override
    public void saveController(ControllerDTO controllerDTO) {
        String jarPath = "file:" + context.getEnvironment().getProperty("jar.file.dir") + controllerDTO.getJarFileName();

        try (CustomClassLoader loader = new CustomClassLoader(new URL[]{new URL(jarPath)}, this.getClass().getClassLoader())) {
            Class<?> loadedClass = loader.loadClass(controllerDTO.getClassPath());
            if (loadedClass.isAnnotationPresent(RestController.class)) {
                Object controllerInstance = loadedClass.getDeclaredConstructor().newInstance();
                RequestMapping classRequestMapping = loadedClass.getAnnotation(RequestMapping.class);
                String classPath = classRequestMapping != null ? classRequestMapping.value()[0] : "";

                if (!classPath.split("/")[1].equals(controllerDTO.getPublishUri())) {
                    throw new ControllerException("Controller 註冊服務失敗，路徑不一致");
                }

                for (Method method : loadedClass.getDeclaredMethods()) {
                    RequestMappingInfo mappingInfo = getMappingForMethod(method, classPath);
                    Optional.ofNullable(mappingInfo).ifPresent(info -> requestMappingHandlerMapping.registerMapping(info, controllerInstance, method));
                }
            }
        } catch (Exception e) {
            log.error("Controller 註冊服務:{}, 失敗", controllerDTO.getPublishUri(), e);
            throw new ControllerException("Controller 註冊服務失敗");
        }
    }

    @Override
    public List<ControllerDTO> getControllers() {

        ResourceEnum resource = ResourceEnum.SQL.getResource(ControllerJDBC.SQL_SELECT_CONTROLLER_RELATED_JAR_FILE);
        List<ControllerModel> controllerModelList = controllerJDBC.queryForList(resource, new Conditions(), ControllerModel.class);

        return controllerModelList.stream().map(controller -> {
            ControllerDTO dto = new ControllerDTO();
            BeanUtils.copyProperties(controller, dto);
            return dto;
        }).toList();
    }

    private RequestMappingInfo getMappingForMethod(Method method, String classLevelPath) {
        RequestMappingInfo.Builder mappingBuilder = null;

        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            mappingBuilder = RequestMappingInfo
                    .paths(combine(classLevelPath, methodMapping.value()))
                    .methods(methodMapping.method())
                    .params(methodMapping.params())
                    .headers(methodMapping.headers())
                    .consumes(methodMapping.consumes())
                    .produces(methodMapping.produces());
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping methodMapping = method.getAnnotation(GetMapping.class);
            mappingBuilder = RequestMappingInfo
                    .paths(combine(classLevelPath, methodMapping.value()))
                    .methods(RequestMethod.GET);
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping methodMapping = method.getAnnotation(PostMapping.class);
            mappingBuilder = RequestMappingInfo
                    .paths(combine(classLevelPath, methodMapping.value()))
                    .methods(RequestMethod.POST);
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping methodMapping = method.getAnnotation(PutMapping.class);
            mappingBuilder = RequestMappingInfo
                    .paths(combine(classLevelPath, methodMapping.value()))
                    .methods(RequestMethod.PUT);
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping methodMapping = method.getAnnotation(DeleteMapping.class);
            mappingBuilder = RequestMappingInfo
                    .paths(combine(classLevelPath, methodMapping.value()))
                    .methods(RequestMethod.DELETE);
        }

        return mappingBuilder != null ? mappingBuilder.build() : null;
    }

    private String[] combine(String classPath, String[] methodPaths) {
        if (methodPaths.length == 0) {
            return new String[]{classPath};
        }
        String[] result = new String[methodPaths.length];
        for (int i = 0; i < methodPaths.length; i++) {
            result[i] = classPath + methodPaths[i];
        }
        return result;
    }
}
