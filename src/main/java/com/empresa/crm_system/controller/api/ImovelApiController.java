package com.empresa.crm_system.controller.api;

import com.empresa.crm_system.Imovel;
import com.empresa.crm_system.security.CorretorDetails;
import com.empresa.crm_system.enums.CategoriaImovel;
import com.empresa.crm_system.enums.FinalidadeImovel;
import com.empresa.crm_system.enums.StatusImovel;
import com.empresa.crm_system.service.ImovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imoveis")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class ImovelApiController {

    @Autowired
    private ImovelService imovelService;

    @GetMapping
    public List<Imovel> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) FinalidadeImovel finalidade,
            @RequestParam(required = false) CategoriaImovel categoria,
            @RequestParam(required = false) StatusImovel status,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) Boolean destaque,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax,
            @RequestParam(required = false) Double areaMin,
            @RequestParam(required = false) Double areaMax) {
        return imovelService.filtrar(
                tipo,
                finalidade,
                categoria,
                status,
                cidade,
                destaque,
                precoMin,
                precoMax,
                areaMin,
                areaMax);
    }

    @GetMapping("/meus")
    public List<Imovel> listarMeus(@AuthenticationPrincipal CorretorDetails principal) {
        return imovelService.listarPorCorretor(principal.getCorretor().getId());
    }

    @GetMapping("/indicadores")
    public Map<String, Object> indicadores() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalImoveis", imovelService.contarTotal());
        response.put("imoveisVendaDisponiveis", imovelService.contarDisponiveisPorFinalidade(FinalidadeImovel.VENDA));
        response.put("imoveisAluguelDisponiveis", imovelService.contarDisponiveisPorFinalidade(FinalidadeImovel.ALUGUEL));
        response.put("mediaVendaMetroQuadrado", imovelService.calcularValorMedioMetroQuadrado(FinalidadeImovel.VENDA));
        response.put("mediaAluguelMetroQuadrado", imovelService.calcularValorMedioMetroQuadrado(FinalidadeImovel.ALUGUEL));
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Imovel> buscarPorId(@PathVariable Long id) {
        return imovelService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Imovel criar(@RequestBody Imovel imovel, @AuthenticationPrincipal CorretorDetails principal) {
        return imovelService.salvarParaCorretor(imovel, principal.getCorretor());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Imovel> atualizar(
            @PathVariable Long id,
            @RequestBody Imovel imovel,
            @AuthenticationPrincipal CorretorDetails principal) {
        return imovelService.buscarPorId(id)
                .filter(imovelExistente -> imovelExistente.getCorretor() != null
                        && imovelExistente.getCorretor().getId().equals(principal.getCorretor().getId()))
                .map(imovelExistente -> {
                    imovel.setId(id);
                    imovel.setCorretor(principal.getCorretor());
                    return ResponseEntity.ok(imovelService.salvar(imovel));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal CorretorDetails principal) {
        return imovelService.buscarPorId(id)
                .filter(imovel -> imovel.getCorretor() != null
                        && imovel.getCorretor().getId().equals(principal.getCorretor().getId()))
                .map(imovel -> {
                    imovelService.deletar(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
