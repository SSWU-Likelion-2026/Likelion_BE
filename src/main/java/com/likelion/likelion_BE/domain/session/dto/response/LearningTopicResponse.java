package com.likelion.likelion_BE.domain.session.dto.response;

import com.likelion.likelion_BE.domain.session.entity.LearningTopic;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LearningTopicResponse {

    private Long learningTopicId;
    private String content;
    private Integer sequenceNum;

    public static LearningTopicResponse from(LearningTopic topic) {
        return LearningTopicResponse.builder()
                .learningTopicId(topic.getId())
                .content(topic.getContent())
                .sequenceNum(topic.getSequenceNum())
                .build();
    }
}
