package com.airbnb_clone.detailPage;

import com.airbnb_clone.auth.repository.UserRepository;
import com.airbnb_clone.auth.service.UserService;
import com.airbnb_clone.detaile.repository.DetailPageRepository;
import com.airbnb_clone.detaile.service.DetailPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class DetailpageServiceTest {

    @Autowired
    private DetailPageRepository detailPageRepository;

    @Autowired
    private DetailPageService detailPageService;

    @Test
    void  핀_조회_성공() {
//        pinDetail pindetail = detailPageRepository.


    }


}
