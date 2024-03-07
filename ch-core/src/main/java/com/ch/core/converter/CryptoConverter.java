package com.ch.core.converter;

import com.ch.core.code.Errors;
import com.ch.core.exception.CommonAccessException;
import com.ch.core.utils.AesUtil;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    private final AesUtil aesUtil;

    /**
     * 데이터베이스에 요청을 보낼 때 (암호화)
     * @param attribute  the entity attribute value to be converted
     * @return String - 암호화
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        try {
            return aesUtil.encrypt(attribute);
        } catch ( Exception e ) {
            throw new CommonAccessException(Errors.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 값을 읽어올 때 (복호화)
     * @param data  the data from the database column to be converted
     * @return String - 복호화
     */
    @Override
    public String convertToEntityAttribute(String data) {
        if ( data == null ) {
            return null;
        }
        try {
            return aesUtil.decrypt(data);
        } catch ( Exception e ) {
            throw new CommonAccessException(Errors.INTERNAL_SERVER_ERROR);
        }
    }
}
