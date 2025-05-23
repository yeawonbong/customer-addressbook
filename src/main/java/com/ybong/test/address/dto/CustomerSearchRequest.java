package com.ybong.test.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고객 목록/검색 RequestDTO")
public class CustomerSearchRequest {

    @Schema(
            description = "검색어 (이름/주소/전화번호/이메일 전체 대상으로 양방향 like 검색)",
            example = "홍길동"
    )
    private String keyword;

    @Schema(
            description = "검색 필드: name(이름), address(주소), phoneNumber(전화번호), email(이메일), 미입력 시 전체 검색",
            example = "name"
    )
    @Pattern(
            regexp = "|name|address|phoneNumber|email",
            message = "{validation.invalid.filter}"
    )
    private String filter;

    @Schema(
            description = "정렬 기준: name(이름), address(주소), phoneNumber(전화번호), email(이메일)",
            example = "name",
            defaultValue = "name"
    )
    @Pattern(
            regexp = "|name|address|phoneNumber|email",
            message = "{validation.invalid.sortBy}"
    )
    private String sortBy = "name";

    @Schema(
            description = "정렬 방향: asc(오름차순), desc(내림차순)",
            example = "asc",
            defaultValue = "asc"
    )
    @Pattern(
            regexp = "|asc|desc",
            message = "{validation.invalid.sortDir}"
    )
    private String sortDir = "asc";
}
