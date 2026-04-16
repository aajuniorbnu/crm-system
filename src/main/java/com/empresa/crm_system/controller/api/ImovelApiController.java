package com.empresa.crm_system.controller.api;

import com.empresa.crm_system.Imovel;
import com.empresa.crm_system.enums.CategoriaImovel;
import com.empresa.crm_system.enums.FinalidadeImovel;
import com.empresa.crm_system.enums.StatusImovel;
import com.empresa.crm_system.service.ImovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) FinalidadeImovel finalidade,
            @RequestParam(required = false) CategoriaImovel categoria,
            @RequestParam(required = false) StatusImovel status,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) Boolean destaque) {
        return imovelService.filtrar(finalidade, categoria, status, cidade, destaque);
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
    public Imovel criar(@RequestBody Imovel imovel) {
        return imovelService.salvar(imovel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Imovel> atualizar(@PathVariable Long id, @RequestBody Imovel imovel) {
        return imovelService.buscarPorId(id)
                .map(imovelExistente -> {
                    imovel.setId(id);
                    return ResponseEntity.ok(imovelService.salvar(imovel));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        imovelService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
