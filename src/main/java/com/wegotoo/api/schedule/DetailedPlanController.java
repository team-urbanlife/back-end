package com.wegotoo.api.schedule;

import com.wegotoo.api.ApiResponse;
import com.wegotoo.api.schedule.request.DetailedPlanCreateRequest;
import com.wegotoo.api.schedule.request.DetailedPlanMoveRequest;
import com.wegotoo.application.schedule.DetailedPlanService;
import com.wegotoo.application.schedule.request.DetailedPlanEditRequest;
import com.wegotoo.infra.resolver.auth.Auth;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DetailedPlanController {

    private final DetailedPlanService detailedPlanService;

    @PostMapping("/v1/schedule-details/{scheduleDetailsId}/detailed-plans")
    public ApiResponse<Void> writeDetailedPlan(@PathVariable("scheduleDetailsId") Long scheduleDetailsId,
                                               @RequestBody @Valid DetailedPlanCreateRequest request,
                                               @Auth Long userId) {
        detailedPlanService.writeDetailedPlan(scheduleDetailsId, userId, request.toService());
        return ApiResponse.ok();
    }

    @PatchMapping("/v1/detailed-plans/{detailedPlanId}")
    public ApiResponse<Void> editDetailedPlan(@PathVariable("detailedPlanId") Long detailedPlanId,
                                              @RequestBody @Valid DetailedPlanEditRequest request,
                                              @Auth Long userId) {
        detailedPlanService.editDetailedPlan(detailedPlanId, userId, request.toService());
        return ApiResponse.ok();
    }

    @PatchMapping("/v1/scheduleDetails/{scheduleDetailsId}/detailed-plans/move")
    public ApiResponse<Void> movePlan(@PathVariable("scheduleDetailsId") Long scheduleDetailsId,
                                      @RequestBody List<DetailedPlanMoveRequest> requests,
                                      @Auth Long userId) {
        detailedPlanService.movePlan(scheduleDetailsId, userId, requests);
        return ApiResponse.ok();
    }

    @DeleteMapping("/v1/detailed-plans/{detailedPlanId}")
    public ApiResponse<Void> deleteDetailedPlan(@PathVariable("detailedPlanId") Long detailedPlanId,
                                                @Auth Long userId) {
        detailedPlanService.deleteDetailedPlan(detailedPlanId, userId);
        return ApiResponse.ok();
    }

}
