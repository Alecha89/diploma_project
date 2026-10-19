package by.bank.consumerlending.site.mapper;

import by.bank.consumerlending.site.dto.resp.QaQuestionResponseDto;
import by.bank.consumerlending.site.entity.QaQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QaQuestionMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicLabel", source = "topic.label")
    QaQuestionResponseDto toDto(QaQuestion entity);
}