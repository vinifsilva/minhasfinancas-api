package com.vsilva.minhasFinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.vsilva.minhasFinancas.model.entity.Usuario;
import com.vsilva.minhasFinancas.model.repository.UsuarioRepository;
import com.vsilva.minhasFinancas.service.exception.ErroAutentiacao;
import com.vsilva.minhasFinancas.service.exception.RegraNegocioException;
import com.vsilva.minhasFinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;


	@Test (expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("teste@gmail.com")
				.senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());


		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");;
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("teste@gmail.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");

	}
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "teste@gmail.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email) ).thenReturn(Optional.of(usuario));

		//acao
		Usuario result = service.autenticar(email, senha);

		//verificacao
		Assertions.assertThat(result).isNotNull();
	}

	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailCadastrado() {
		//cenario
		String email = "teste@gmail.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

		//acao
		service.salvarUsuario(usuario);

		//verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastrado() {

		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("teste@gmail.com", "senha"));
		Assertions.assertThat(exception)
		.isInstanceOf(ErroAutentiacao.class)
		.hasMessage("Ops, Usuario não encontrado para o email informado!");

	}

	@Test
	public void deveLancarErroQuandoSenhaIncorreta() {
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("teste@teste.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@teste.com", "1234") );
		Assertions.assertThat(exception).isInstanceOf(ErroAutentiacao.class).hasMessage("Senha Invalida");

	}


	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// acao
		service.validarEmail("teste@email.com");

	}

	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// acao
		service.validarEmail("teste@email.com");
	}

}
