package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "defaultOfficeName", source = "defaultOffice.name")
    UserDto mapUserEntityToDto(UserEntity userEntity);

}
