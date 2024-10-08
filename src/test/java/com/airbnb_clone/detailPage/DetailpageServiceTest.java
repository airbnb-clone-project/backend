package com.airbnb_clone.detailPage;

import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.dto.PinLikeDto;
import com.airbnb_clone.detaile.repository.DetailPageRepository;
import com.airbnb_clone.detaile.service.DetailPageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class DetailpageServiceTest {

    @Mock
    private DetailPageRepository detailPageRepository;

    @InjectMocks
    private DetailPageService detailPageService;

    @Test
    @DisplayName("핀 조회 성공")
    void getDetailPage_success() {
        Long pinId = 1L;
        DetailPageDto expectedDetailPage = new DetailPageDto(
                pinId,
                "img_url",
                "title",
                "description",
                "link",
                1L,
                "firstname1",
                "lastname1",
                "progile_img_url"
        );
        given(detailPageRepository.findPageById(pinId)).willReturn(expectedDetailPage);
        DetailPageDto actualDetailPage = detailPageService.getDetailPage(pinId);
        assertThat(actualDetailPage).isEqualTo(expectedDetailPage);
        then(detailPageRepository).should().findPageById(pinId);
    }

    @Test
    void getPinLikes_success() {
        Long pinId = 1L;
        List<PinLikeDto> expectedPinLikes = Arrays.asList(
                new PinLikeDto(1L, pinId, 1, 2L, "firstuser1", "Lastuser1", "user1.jpg", null),
                new PinLikeDto(2L, pinId, 2, 3L, "firstuser2", "Lastuser2", "user2.jpg", null)
        );
        given(detailPageRepository.findPinlikesById(pinId)).willReturn(expectedPinLikes);
        List<PinLikeDto> actualPinLikes = detailPageService.getPinLikes(pinId);
        assertThat(actualPinLikes).isEqualTo(expectedPinLikes);
        then(detailPageRepository).should().findPinlikesById(pinId);
    }


}
