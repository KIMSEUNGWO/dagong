package project.study.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyType {

    TYPE1("홍보/상업적 광고 등 (스팸 메세지 등)"),
    TYPE2("고의적인 대화방해 (텍스트 도배 등)"),
    TYPE3("미풍양속을 해치는 행위 (음란/욕설 등)"),
    TYPE4("운영자 사칭"),
    TYPE5("개인정보 침해, 아이디 도용"),
    TYPE6("기타");

    private String notifyType;

    public String getNotifyType() {
        return notifyType;
    }


}
