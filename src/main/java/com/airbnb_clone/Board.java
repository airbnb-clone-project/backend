package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.Getter;

@Getter
public class Board extends BaseTime {
    /**
     * 보드 고유번호
     */
    private Long no;
    
    /**
     * 보드 이름
     */
    private String name;
    
    /**
     * 유저고유번호 (FK)
     */
    private Long userNo;
}