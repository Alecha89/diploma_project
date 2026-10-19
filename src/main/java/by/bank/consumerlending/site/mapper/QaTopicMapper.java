package by.bank.consumerlending.site.mapper;

import by.bank.consumerlending.site.dto.resp.QaTopicResponseDto;
import by.bank.consumerlending.site.entity.QaTopic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QaTopicMapper {

    QaTopicResponseDto toDto(QaTopic entity);
}