package com.airbnb_clone.detaile.controller;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.dto.PinLikeDto;
import com.airbnb_clone.detaile.service.DetailPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DetailPageController {

    private final DetailPageService detailPageService;

    @GetMapping("api/pin/{pinId}/v1")
    public DetailPageDto getDetailPage(@PathVariable Long pinId) {
        DetailPageDto detailPageDto = detailPageService.getDetailPage(pinId);
        return detailPageDto;
    }

    @GetMapping("api/pin/like/{pinId}/v1")
    public List<PinLikeDto> getDetailPageLike(@PathVariable Long pinId) {
        List<PinLikeDto> pinLikes = detailPageService.getPinLikes(pinId);
        return pinLikes;
    }
}
