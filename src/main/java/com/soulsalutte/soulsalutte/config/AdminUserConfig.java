package com.soulsalutte.soulsalutte.config;

import com.soulsalutte.soulsalutte.model.Usuario;
import com.soulsalutte.soulsalutte.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserConfig(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Usuario admin = usuarioRepository.findByLogin("soulsalutte@gmail.com");
        if (admin == null) {
            Usuario usuario = new Usuario();
            usuario.setLogin("soulsalutte@gmail.com");
            usuario.setSenha(passwordEncoder.encode("SoulSalutte2024!"));
            usuarioRepository.save(usuario);
        }
    }

}
