package com.devsenior.jmorera.bibliokeep.controller;

import com.devsenior.jmorera.bibliokeep.model.dto.loans.CreateLoanRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.loans.LoanDto;
import com.devsenior.jmorera.bibliokeep.service.loan.LoanService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        var created = loanService.createLoan(request);
        return ResponseEntity.created(URI.create("/api/loans/" + created.id())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<LoanDto>> findAll() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }
}
