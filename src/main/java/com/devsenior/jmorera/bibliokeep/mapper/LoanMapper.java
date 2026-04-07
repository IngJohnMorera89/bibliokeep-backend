package com.devsenior.jmorera.bibliokeep.mapper;

import com.devsenior.jmorera.bibliokeep.model.dto.loans.CreateLoanRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.LoanDto;
import com.devsenior.jmorera.bibliokeep.model.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanMapper {

	Loan toEntity(CreateLoanRequest request);

	LoanDto toDto(Loan loan);
}

