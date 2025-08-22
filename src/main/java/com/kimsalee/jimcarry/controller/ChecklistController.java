package com.kimsalee.jimcarry.controller;

import com.kimsalee.jimcarry.dto.ChecklistItemDto;
import com.kimsalee.jimcarry.dto.ChecklistResponseDto;
import com.kimsalee.jimcarry.service.ChecklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    // AI 체크리스트 생성
    @PostMapping("/complete")
    public ResponseEntity<ChecklistResponseDto> completeSurvey(@RequestBody List<ChecklistItemDto> selections) {
        ChecklistResponseDto response = checklistService.generateChecklistFromSurvey(selections);
        return ResponseEntity.ok(response);
    }

    // 체크리스트 항목 완료 처리
    @PostMapping("/item/{itemId}/complete")
    public ResponseEntity<ChecklistItemDto> completeItem(@PathVariable String itemId) {
        ChecklistItemDto completed = checklistService.completeChecklistItem(itemId);
        if (completed != null) return ResponseEntity.ok(completed);
        else return ResponseEntity.notFound().build();
    }

    // 저장된 체크리스트 조회
    @GetMapping("/my-list")
    public ResponseEntity<ChecklistResponseDto> getChecklist() {
        ChecklistResponseDto response = checklistService.getUserChecklists();
        return ResponseEntity.ok(response);
    }
}