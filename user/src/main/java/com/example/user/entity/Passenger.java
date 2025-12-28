package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Passenger extends BaseEntity {
    /**
     * 会员id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 乘客类型
     */
    @TableField("`type`")
    private String type;

}