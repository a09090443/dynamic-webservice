package com.dynamicwebservice.converter;

import com.dynamicwebservice.enums.JarFileStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JarFileStatusConverter implements AttributeConverter<JarFileStatus, String> {

    @Override
    public String convertToDatabaseColumn(JarFileStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getStatus();
    }

    @Override
    public JarFileStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return JarFileStatus.fromStatus(dbData);
    }
}
