package com.vsilva.minhasFinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vsilva.minhasFinancas.model.entity.Usuario;
import com.vsilva.minhasFinancas.model.repository.UsuarioRepository;
import com.vsilva.minhasFinancas.service.UsuarioService;
import com.vsilva.minhasFinancas.service.exception.ErroAutentiacao;
import com.vsilva.minhasFinancas.service.exception.RegraNegocioException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;

	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);

		if (!usuario.isPresent()) {
			throw new ErroAutentiacao("Ops, Usuario não encontrado para o email informado!");
		}
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutentiacao("Senha Invalida");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(long id) {
		return repository.findById(id);
	}

}