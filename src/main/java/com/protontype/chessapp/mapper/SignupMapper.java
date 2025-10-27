package com.protontype.chessapp.mapper;

import com.protontype.chessapp.model.dto.response.SignupResponse;
import com.protontype.chessapp.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignupMapper {
    SignupResponse toResponse(User user);
}

