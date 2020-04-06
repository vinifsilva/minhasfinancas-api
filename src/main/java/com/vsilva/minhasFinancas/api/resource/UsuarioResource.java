package com.vsilva.minhasFinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsilva.minhasFinancas.api.dto.UsuarioDTO;
import com.vsilva.minhasFinancas.model.entity.Usuario;
import com.vsilva.minhasFinancas.service.UsuarioService;
import com.vsilva.minhasFinancas.service.exception.ErroAutentiacao;

@RestController
@RequestMapping("api/usuarios")//todas as requisições ira entrar neste controller
public class UsuarioResource {

	private UsuarioService service;

	public UsuarioResource(UsuarioService service) {
		this.service =service;
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar( @RequestBody UsuarioDTO dto) {
		try {
		Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
		return ResponseEntity.ok(usuarioAutenticado);
		}catch (ErroAutentiacao e) {
		return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity salvar( @RequestBody UsuarioDTO dto) {

		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha()).build();
		try {
			
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
