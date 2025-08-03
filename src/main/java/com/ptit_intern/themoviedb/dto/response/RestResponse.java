package com.ptit_intern.themoviedb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RestResponse<T> {
    int statusCode;
    Object error;
    Object message;
    T data;
}
