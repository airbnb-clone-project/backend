package com.airbnb_clone.detaile.controller;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.service.DetailPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DetailPageController {

    private final DetailPageService detailPageService;

    @GetMapping("api/pin/{pinId}/v1")
    public DetailPageDto getDetailPage(@PathVariable Long pinId) {
        DetailPageDto detailPageDto = detailPageService.getDetailPage(pinId);
        return detailPageDto;
    }
}
