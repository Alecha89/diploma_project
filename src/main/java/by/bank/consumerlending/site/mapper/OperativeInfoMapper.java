package by.bank.consumerlending.site.mapper;

import by.bank.consumerlending.site.dto.req.OperativeInfoRequestDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoResponseDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoShortResponseDto;
import by.bank.consumerlending.site.entity.OperativeInfo;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class OperativeInfoMapper {

    public abstract OperativeInfoResponseDto toDto(@Nullable OperativeInfo entity);

    public abstract OperativeInfoShortResponseDto toShortDto(@Nullable OperativeInfo entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "contentHtml", ignore = true)
    public abstract OperativeInfo toEntity(OperativeInfoRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "contentHtml", ignore = true)
    public abstract void updateEntityFromDto(OperativeInfoRequestDto dto, @MappingTarget OperativeInfo entity);
}