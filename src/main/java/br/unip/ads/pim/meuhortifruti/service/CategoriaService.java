package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.CategoriaRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.CategoriaResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Categoria;
import br.unip.ads.pim.meuhortifruti.repository.CategoriaRepository;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;


    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Integer id) {
        Categoria categoria = buscarCategoriaPorId(id);
        return converterParaDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        if (categoriaRepository.existsByNome(dto.getNome())) {
            throw new RecursoDuplicadoException("Categoria", "nome", dto.getNome());
        }

        Categoria categoria = Categoria.builder()
                .nome(dto.getNome())
                .build();

        categoria = categoriaRepository.save(categoria);
        return converterParaDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Integer id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarCategoriaPorId(id);

        categoriaRepository.findByNome(dto.getNome()).ifPresent(categoriaExistente -> {
            if (!categoriaExistente.getIdCategoria().equals(id)) {
                throw new RecursoDuplicadoException("Categoria", "nome", dto.getNome());
            }
        });

        categoria.setNome(dto.getNome());
        categoria = categoriaRepository.save(categoria);
        return converterParaDTO(categoria);
    }

    @Transactional
    public void excluir(Integer id) {
        Categoria categoria = buscarCategoriaPorId(id);
        categoriaRepository.delete(categoria);
    }

    private Categoria buscarCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", id));
    }

    private CategoriaResponseDTO converterParaDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nome(categoria.getNome())
                .build();
    }
}