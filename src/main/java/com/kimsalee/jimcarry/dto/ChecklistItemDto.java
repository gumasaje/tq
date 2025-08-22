package com.kimsalee.jimcarry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistItemDto {
    private String itemId;
    private String itemName;
    private boolean selected;
    private String category;
}