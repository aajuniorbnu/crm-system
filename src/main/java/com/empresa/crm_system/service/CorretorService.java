package com.empresa.crm_system.service;

import com.empresa.crm_system.Corretor;
import com.empresa.crm_system.Imovel;
import com.empresa.crm_system.repository.CorretorRepository;
import com.empresa.crm_system.repository.ImovelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CorretorService extends BaseService<Corretor, Long> {

    private final CorretorRepository corretorRepository;
    private final ImovelRepository imovelRepository;
    private final PasswordEncoder passwordEncoder;
    private final Path uploadDir;

    public CorretorService(
            CorretorRepository corretorRepository,
            ImovelRepository imovelRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.corretorRepository = corretorRepository;
        this.imovelRepository = imovelRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    protected JpaRepository<Corretor, Long> getRepository() {
        return corretorRepository;
    }

    @Override
    protected void beforeSave(Corretor corretor) {
        if (corretor.getId() == null) {
            corretor.setDataCadastro(LocalDateTime.now());
        }

        if (corretor.getAtivo() == null) {
            corretor.setAtivo(Boolean.TRUE);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Corretor> buscarPorEmail(String email) {
        return corretorRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    public Corretor atualizarPerfil(
            Corretor corretor,
            String nome,
            String telefone,
            String creci,
            MultipartFile foto) {
        corretor.setNome(nome != null ? nome.trim() : corretor.getNome());
        corretor.setTelefone(telefone != null ? telefone.trim() : corretor.getTelefone());
        corretor.setCreci(creci != null ? creci.trim() : corretor.getCreci());

        if (foto != null && !foto.isEmpty()) {
            corretor.setFotoUrl(armazenarFoto(corretor.getId(), foto));
        }

        return salvar(corretor);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> montarDashboard(Corretor corretor) {
        List<Imovel> imoveis = imovelRepository.findByCorretorIdOrderByDataCadastroDesc(corretor.getId());
        long destaques = imoveis.stream().filter(Imovel::getDestaque).count();
        double valorPortfolio = imoveis.stream()
                .map(Imovel::getValor)
                .filter(valor -> valor != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("corretor", corretor);
        response.put("imoveis", imoveis);
        response.put("totalImoveis", imoveis.size());
        response.put("destaques", destaques);
        response.put("valorPortfolio", valorPortfolio);
        return response;
    }

    public String encodePassword(String senha) {
        return passwordEncoder.encode(senha);
    }

    private String armazenarFoto(Long corretorId, MultipartFile foto) {
        try {
            Files.createDirectories(uploadDir);
            String originalName = foto.getOriginalFilename() == null ? "foto.jpg" : foto.getOriginalFilename();
            String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = "corretor-" + corretorId + "-" + UUID.randomUUID() + "-" + sanitizedName;
            Path targetFile = uploadDir.resolve(fileName);
            Files.copy(foto.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel salvar a foto do corretor", exception);
        }
    }
}
