package com.vsilva.minhasFinancas.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.Example;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.*;

import com.vsilva.minhasFinancas.model.entity.Lancamento;
import com.vsilva.minhasFinancas.model.entity.Usuario;
import com.vsilva.minhasFinancas.model.enums.StatusLancamento;
import com.vsilva.minhasFinancas.model.enums.Tipo_lancamento;
import com.vsilva.minhasFinancas.model.repository.LancamentoRepository;
import com.vsilva.minhasFinancas.model.repository.LancamentoRepositoryTest;
import com.vsilva.minhasFinancas.service.exception.RegraNegocioException;
import com.vsilva.minhasFinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {

		// cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);

		// verificacao
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		// execucao e verificacao
		catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {

		// cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// execucao
		service.atualizar(lancamentoSalvo);

		// verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {

		// cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// execucao e verificacao
		catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}

	@Test
	public void deveDeletarUmLancamento() {
		// cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		// execucao
		service.deletar(lancamento);

		// verificacao
		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {

		// cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// execucao
		catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		// verificacao
		Mockito.verify(repository, Mockito.never()).delete(lancamento);

	}

	/*
	 * @Test public void deveFiltrarLancamentos() { 
	 * // cenario 
	 * Lancamento lancamento = LancamentoRepositoryTest.criarLancamento(); lancamento.setId(1l);
	 * 
	 * List<Lancamento> lista = Arrays.asList(lancamento);
	 * Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista
	 * );
	 * 
	 * // execucao List<Lancamento> resultado = service.buscar(lancamento);
	 * 
	 * // verificacao
	 * assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	 * 
	 * }
	 */

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// execucao
		service.atualizarStatus(lancamento, novoStatus);

		// verificacao
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		// cenario

		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// execucao
		Optional<Lancamento> resultado = service.obterPorId(id);

		// verificacao
		assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExiste() {
		// cenario

		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// execucao
		Optional<Lancamento> resultado = service.obterPorId(id);

		// verificacao
		assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void LancarErroAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();

		Throwable erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Infelizmente não entendemos a sua descrição. Informe uma descrição válida.");

		lancamento.setDescricao("");

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Infelizmente não entendemos a sua descrição. Informe uma descrição válida.");

		lancamento.setDescricao("Salario");

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Mês inválido. Por favor, informe um Mês válido");

		lancamento.setAno(0);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Mês inválido. Por favor, informe um Mês válido");

		lancamento.setAno(13);
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Mês inválido. Por favor, informe um Mês válido");

		lancamento.setMes(1);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Ano inválido. Por favor, informe um Ano válido");
		lancamento.setAno(202);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Ano inválido. Por favor, informe um Ano válido");
		lancamento.setAno(2000);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Por favor, informe um Usuário válido");

		lancamento.setAno(2000);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Por favor, informe um Usuário válido");

		lancamento.setUsuario(new Usuario());
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Por favor, informe um Usuário válido");

		lancamento.getUsuario().setId(1l);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Valor inválido. Por favor, informe um Valor válido");
		lancamento.setValor(BigDecimal.ZERO);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Você informou um Valor inválido. Por favor, informe um Valor válido");

		lancamento.setValor(BigDecimal.valueOf(1));

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Por favor, informe um Tipo de Lancamento");

	}
}
