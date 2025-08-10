package com.ptit_intern.themoviedb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultPagination {
    private MetaInfo metaInfo;
    private Object results;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaInfo {
        private int page;
        private int size;
        private int totalPages;
        private Long total;
    }
}
