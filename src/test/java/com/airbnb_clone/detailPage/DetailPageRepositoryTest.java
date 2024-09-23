package com.airbnb_clone.detailPage;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.repository.DetailPageRepository;
import com.airbnb_clone.pin.domain.Pin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
//@WebMvcTest
@ActiveProfiles("local")
@Rollback(false)
//@JdbcTest
//@Transactional
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DetailPageRepositoryTest {


    @Autowired
    DetailPageRepository detailPageRepository;


    @Test
    public void findPageById() {

        //테스트 데이터 삽입
//        Long testPinId = 1L;

        Pin newPin = new Pin( 1L, "Image URL", "Title", "Description", "Link", 1L, true, 3);
        Pin savedPin = detailPageRepository.save(newPin);

        //테스트 실행
        DetailPageDto result = detailPageRepository.findPageById(savedPin.getNo());

        //결과검증
        Assertions.assertNotNull(result);
        Assertions.assertEquals("expected Title", result.getTitle());


    }


}
