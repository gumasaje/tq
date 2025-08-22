package com.kimsalee.jimcarry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistResponseDto {
    private List<ChecklistItemDto> checklists;
}
