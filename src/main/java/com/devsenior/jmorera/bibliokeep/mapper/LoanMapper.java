package com.devsenior.jmorera.bibliokeep.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.devsenior.jmorera.bibliokeep.model.dto.loans.CreateLoanRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.LoanDto;
import com.devsenior.jmorera.bibliokeep.model.entity.Loan;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "returned", constant = "false")
	Loan toEntity(CreateLoanRequest request);

	LoanDto toDto(Loan loan);
}

