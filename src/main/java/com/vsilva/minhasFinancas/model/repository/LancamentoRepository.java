package com.vsilva.minhasFinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vsilva.minhasFinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
