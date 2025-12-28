package com.example.business.request;

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
public class StationRequest {

    private Long id;

    @NotBlank(message = "站名不能为空")
    @Size(max = 20)
    private String name;

    @NotBlank(message = "站名拼音不能为空")
    @Size(max = 50)
    private String namePinyin;

    @NotBlank(message = "拼音首字母不能为空")
    @Size(max = 50)
    private String namePy;

}
