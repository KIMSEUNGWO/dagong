package project.study.repository;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.component.CustomMvcResult;
import project.study.controller.RoomController;
import project.study.domain.MockRoom;
import project.study.domain.Room;
import project.study.dto.login.responsedto.Error;
import project.study.dto.login.responsedto.ErrorList;
import project.study.enums.PublicEnum;
import project.study.exceptions.authority.joinroom.FullRoomException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static project.study.component.CustomMvcResult.HttpMethodType.POST;

@SpringBootTest
@Transactional
@DisplayName("RoomRepository")
class RoomRepositoryTest {

    @Autowired
    private MockRoom mockRoom;
    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("방 제목 검증로직 2~10자 이내")
    void validRoomTitle() {
        // given
        ErrorList errorList = new ErrorList();
        String testTitle1 = null;
        String testTitle2 = "하";
        String testTitle3 = "12345678901";
        String testTitle4 = "정상적인 길이테스트";

        // when
        roomRepository.validRoomTitle(errorList, testTitle1);
        roomRepository.validRoomTitle(errorList, testTitle2);
        roomRepository.validRoomTitle(errorList, testTitle3);
        roomRepository.validRoomTitle(errorList, testTitle4);

        // then
        List<Error> list = errorList.getErrorList();

        assertThat(errorList.hasError()).isTrue();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("방 소개글 검증로직")
    void validRoomIntro() {
        // given
        ErrorList errorList = new ErrorList();
        String intro1 = null;
        String intro2 = "정상범위값".repeat(10);
        String intro3 = "12345".repeat(11);

        // null 가능
        roomRepository.validRoomIntro(errorList, intro1);
        assertThat(errorList.hasError()).isFalse();

        // 50자 이하
        roomRepository.validRoomIntro(errorList, intro2);
        assertThat(errorList.hasError()).isFalse();

        // 50자 이상
        roomRepository.validRoomIntro(errorList, intro3);
        assertThat(errorList.hasError()).isTrue();
    }

    @Test
    @DisplayName("최대인원수 2-6명 확인")
    void validRoomMaxPerson() {
        // given
        ErrorList errorList = new ErrorList();
        String max1 = "2"; // 정상 값
        String max2 = "6"; // 정상 값
        String[] maxArr = {null, "a", "a1", "1", "7"};

        // when
        roomRepository.validRoomMaxPerson(errorList, max1);
        roomRepository.validRoomMaxPerson(errorList, max2);
        assertThat(errorList.hasError()).isFalse();
        assertThat(errorList.getErrorList().size()).isEqualTo(0);

        for (String maxStr : maxArr) {
            roomRepository.validRoomMaxPerson(errorList, maxStr);
        }
        assertThat(errorList.hasError()).isTrue();
        assertThat(errorList.getErrorList().size()).isEqualTo(maxArr.length);
    }

    @Test
    @DisplayName("방 공개여부 및 비밀번호 4-6자 확인")
    void validPublic() {
        // given
        ErrorList errorList = new ErrorList();

        // 정상흐름
        roomRepository.validPublic(errorList, PublicEnum.PUBLIC, null);
        roomRepository.validPublic(errorList, PublicEnum.PRIVATE, "1234");
        roomRepository.validPublic(errorList, PublicEnum.PRIVATE, "123456");
        assertThat(errorList.hasError()).isFalse();

        // 에러
        roomRepository.validPublic(errorList, PublicEnum.PUBLIC, "abcd");
        roomRepository.validPublic(errorList, PublicEnum.PUBLIC, "11");
        roomRepository.validPublic(errorList, PublicEnum.PRIVATE, "1");
        roomRepository.validPublic(errorList, PublicEnum.PRIVATE, "asdfaefawefae");
        roomRepository.validPublic(errorList, PublicEnum.PRIVATE, null);
        roomRepository.validPublic(errorList, null, "1234");
        roomRepository.validPublic(errorList, null, "");
        roomRepository.validPublic(errorList, null, null);
        assertThat(errorList.getErrorList().size()).isEqualTo(8);

    }

    @Test
    @DisplayName("방 설정변경 인원확인")
    void validRoomEditMaxPerson() {
        // given
        ErrorList errorList = new ErrorList();

        // 정상흐름
        roomRepository.validRoomEditMaxPerson(errorList, 1, "2");
        roomRepository.validRoomEditMaxPerson(errorList, 1, "6");
        roomRepository.validRoomEditMaxPerson(errorList, 3, "5");
        roomRepository.validRoomEditMaxPerson(errorList, 6, "6");
        assertThat(errorList.hasError()).isFalse();

        // 예외
        roomRepository.validRoomEditMaxPerson(errorList, 1, "1");
        assertThat(errorList.getErrorList().size()).isEqualTo(1);
        roomRepository.validRoomEditMaxPerson(errorList, 2, "1");
        assertThat(errorList.getErrorList().size()).isEqualTo(2);
        roomRepository.validRoomEditMaxPerson(errorList, 4, "3");
        assertThat(errorList.getErrorList().size()).isEqualTo(3);
        roomRepository.validRoomEditMaxPerson(errorList, 3, "a");
        assertThat(errorList.getErrorList().size()).isEqualTo(4);
        roomRepository.validRoomEditMaxPerson(errorList, 3, "a4");
        assertThat(errorList.getErrorList().size()).isEqualTo(5);
        roomRepository.validRoomEditMaxPerson(errorList, 3, "7");
        assertThat(errorList.getErrorList().size()).isEqualTo(6);
        roomRepository.validRoomEditMaxPerson(errorList, 3, "100");
        assertThat(errorList.getErrorList().size()).isEqualTo(7);
        roomRepository.validRoomEditMaxPerson(errorList, 3, null);
        assertThat(errorList.getErrorList().size()).isEqualTo(8);
        roomRepository.validRoomEditMaxPerson(errorList, 5, "4");
        assertThat(errorList.getErrorList().size()).isEqualTo(9);
    }

    @Test
    @DisplayName("태그는 최대 5개까지 가능하다")
    void validTagList() {
        // given
        ErrorList errorList = new ErrorList();
        List<String> tagList1 = List.of("asdf", "123", "asdfaef", "efefse", "seff");
        List<String> tagList2 = List.of("asdf");
        List<String> tagList3 = Collections.emptyList();
        List<String> tagList4 = null;

        // 정상흐름
        roomRepository.validTagList(errorList, tagList1);
        roomRepository.validTagList(errorList, tagList2);
        roomRepository.validTagList(errorList, tagList3);
        roomRepository.validTagList(errorList, tagList4);
        assertThat(errorList.hasError()).isFalse();

        // 예외
        roomRepository.validTagList(errorList, List.of("1","2","3","4","5","6"));
        assertThat(errorList.hasError()).isTrue();
        assertThat(errorList.getErrorList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("방이 가득차지 않았을 때 방에 참가")
    void validFullRoom1() {
        // given
        Room room = mockRoom.createRoom().setMaxPerson(3).addJoinRoom(2).build();


        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestJoinRoomDto data = new RequestJoinRoomDto(null, room, response, null);

        // 예외가 발생하지 않음
        assertThatCode(() -> roomRepository.validFullRoom(data))
            .doesNotThrowAnyException();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("방이 가득찼을 때 방에 참가")
    void validFullRoom2() {
        // given
        Room room = mockRoom.createRoom().setMaxPerson(3).addJoinRoom(3).build();

        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestJoinRoomDto data = new RequestJoinRoomDto(null, room, response, null);

        // 예외가 발생함
        assertThatThrownBy(() -> {
            roomRepository.validFullRoom(data);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()); // BAD_REQUEST 검증
        })
            .isInstanceOf(FullRoomException.class) // 예외가 발생하는지 검증
            .hasFieldOrPropertyWithValue("alertMessage", "방이 가득찼습니다."); // 메세지 검증

    }

}