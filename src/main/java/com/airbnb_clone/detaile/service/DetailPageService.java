package com.airbnb_clone.detaile.service;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.repository.DetailPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailPageService {
    private final DetailPageRepository detailPageRepository;


    public DetailPageDto getDetailPage(Long pinId) {
        //리포지토리에서 데이터 가져옴
        DetailPageDto detailPageDto = detailPageRepository.findPageById(pinId);


        return detailPageDto;


    }
}
