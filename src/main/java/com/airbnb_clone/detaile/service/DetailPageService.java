package com.airbnb_clone.detaile.service;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.dto.PinLikeDto;
import com.airbnb_clone.detaile.repository.DetailPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailPageService {
    private final DetailPageRepository detailPageRepository;

    //특정 핀 상세정보
    public DetailPageDto getDetailPage(Long pinId) {
        return detailPageRepository.findPageById(pinId);
    }

    //특정 핀 반응목록
    public List<PinLikeDto> getPinLikes(Long pinId) {
        return detailPageRepository.findPinlikesById(pinId);
    }
}
