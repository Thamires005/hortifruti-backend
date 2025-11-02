package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.entity.Compra;
import br.unip.ads.pim.meuhortifruti.dto.CompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> listarTodas(){
        return compraRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompraResponseDTO buscarPorId(Integer id){
        Compra compra = buscarCompraPorId(id);
        return converterParaDTO(compra);
    }

    @Transactional
    public CompraResponseDTO criar(CompraRequestDTO dto){
        if(compraRepository.existsById(dto.getIdCompra())){
            throw new RecursoDuplicadoException("Compra", "id", dto.getIdCompra());
        }
        Compra compra = Compra.builder()
                .itensCompra(dto.getItensCompra())
                .statusCompra(dto.getStatusCompra())
                .build();
        compra = compraRepository.save(compra);
        return converterParaDTO(compra);
    }

    @Transactional
    public CompraResponseDTO atualizar(Integer id, CompraRequestDTO dto){
        Compra compra = buscarCompraPorId(id);

        compraRepository.findById(dto.getIdCompra()).ifPresent(compraExistente -> {
            if(!compraExistente.getIdCompra().equals(id)){
                throw new RecursoDuplicadoException("Compra", "id", dto.getIdCompra());
            }
        });

        compra.setStatusCompra(dto.getStatusCompra());
        compra.setItensCompra(dto.getItensCompra());
        compra = compraRepository.save(compra);
        return converterParaDTO(compra);
    }

    @Transactional
    public void excluir(Integer id){
        Compra compra = buscarCompraPorId(id);
        compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra", "id", id));
    }

    private Compra buscarCompraPorId(Integer id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", id));
    }

    private CompraResponseDTO converterParaDTO(Compra compra) {
        return CompraResponseDTO.builder()
                .idCompra(compra.getIdCompra())
                .statusCompra(compra.getStatusCompra())
                .build();
    }
}

