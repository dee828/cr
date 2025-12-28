package com.example.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter
@Setter
public class PassengerRequest {

    private Long id;

    @NotNull(message = "会员id不能为空")
    private Long userId;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 18)
    private String name;

    @NotBlank(message = "身份证号不能为空")
    @Size(max = 18)
    @Size(min = 18, max = 18, message = "【身份证号】必须是18位")
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 11)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "【手机号】格式不正确")
    private String mobile;

    @NotBlank(message = "乘客类型不能为空")
    private String type;

}
