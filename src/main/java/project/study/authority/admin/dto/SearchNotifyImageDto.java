package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchNotifyImageDto {

    private List<String> notifyImageStoreName;
    private List<String> notifyImageOriginalName;

}

