package com.vsilva.minhasFinancas.service;

import java.util.Optional;

import com.vsilva.minhasFinancas.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);

	Usuario salvarUsuario(Usuario usuario);

	void validarEmail(String email);

	Optional<Usuario> obterPorId(long id);
}
