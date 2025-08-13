package com.ptit_intern.themoviedb.dto.dtoClass;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class BaseDTO implements Serializable {
    protected Instant created_at;
    protected Instant updated_at;
    protected String created_by;
    protected String updated_by;
}
