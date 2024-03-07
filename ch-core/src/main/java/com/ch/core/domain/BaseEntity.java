package com.ch.core.domain;

import com.ch.core.utils.BooleanToYNConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@ToString
public abstract class BaseEntity extends BaseTimeEntity implements Serializable {

    @Column(name = "create_id", updatable = false, columnDefinition = "BINARY(16)")
    protected UUID createId;

    @Column(name = "update_id", columnDefinition = "BINARY(16)")
    protected UUID updateId;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "del_flag")
    protected boolean delFlag;

}
