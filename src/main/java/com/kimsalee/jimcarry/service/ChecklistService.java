package com.kimsalee.jimcarry.service;

import com.kimsalee.jimcarry.dto.ChecklistItemDto;
import com.kimsalee.jimcarry.dto.ChecklistResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChecklistService {

    private final AIService aiService;
    private List<ChecklistItemDto> savedChecklist = new ArrayList<>();

    public ChecklistService(AIService aiService) {
        this.aiService = aiService;
    }

    // AI 체크리스트 생성
    public ChecklistResponseDto generateChecklistFromSurvey(List<ChecklistItemDto> surveySelections) {
        List<ChecklistItemDto> generated = aiService.generateChecklist(surveySelections);
        savedChecklist = generated;
        return new ChecklistResponseDto(savedChecklist);
    }

    // 저장된 체크리스트 조회
    public ChecklistResponseDto getUserChecklists() {
        return new ChecklistResponseDto(savedChecklist);
    }

    // 체크리스트 항목 완료 처리
    public ChecklistItemDto completeChecklistItem(String itemId) {
        for (ChecklistItemDto item : savedChecklist) {
            if (item.getItemId().equals(itemId)) {
                item.setSelected(true);
                return item;
            }
        }
        return null;
    }
}