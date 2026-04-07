package com.devsenior.jmorera.bibliokeep.service.loan;

import com.devsenior.jmorera.bibliokeep.model.dto.loans.CreateLoanRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.LoanDto;
import java.util.List;

public interface LoanService {

    LoanDto createLoan(CreateLoanRequest request);

    LoanDto returnLoan(Long loanId);

    LoanDto findById(Long loanId);

    List<LoanDto> findAll();
}
