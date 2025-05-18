package com.hyundai.test.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고객 정보 업데이트 RequestDTO")
public class CustomerUpdateRequest extends CustomerRequest {

    @Schema(description = "고객ID", example = "1")
    @NotNull(message = "{validation.required.id}")
    private Long id;

}
