package com.devsenior.jmorera.bibliokeep.mapper;

import com.devsenior.jmorera.bibliokeep.model.dto.auth.RegisterRequest;
import com.devsenior.jmorera.bibliokeep.model.entity.User;
import java.util.HashSet;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	@Mapping(target = "preferences", source = "preferences")
	@Mapping(target = "annualGoal", source = "annualGoal")
	User toEntity(RegisterRequest request);

	default Set<String> map(Set<String> preferences) {
		return preferences == null ? new HashSet<>() : new HashSet<>(preferences);
	}

	default Integer map(Integer annualGoal) {
		return annualGoal == null ? 12 : annualGoal;
	}
}

