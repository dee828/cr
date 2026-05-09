package com.example.common.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class PageResponse<T> implements Serializable {
    private Long total;
    private List<T> list;
}
