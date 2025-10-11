package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.FornecedorRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.FornecedorResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Fornecedor;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listarTodos() {
        return fornecedorRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Integer id) {
        Fornecedor fornecedor = buscarFornecedorPorId(id);
        return converterParaDTO(fornecedor);
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> buscarPorNome(String nome) {
        return fornecedorRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FornecedorResponseDTO criar(FornecedorRequestDTO dto) {
        if (fornecedorRepository.existsByCnpj(dto.getCnpj())) {
            throw new RecursoDuplicadoException("Fornecedor", "CNPJ", dto.getCnpj());
        }

        if (fornecedorRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("Fornecedor", "email", dto.getEmail());
        }

        Fornecedor fornecedor = Fornecedor.builder()
                .nome(dto.getNome())
                .cnpj(dto.getCnpj())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .endereco(dto.getEndereco())
                .prodFornecidos(dto.getProdFornecidos())
                .build();

        fornecedor = fornecedorRepository.save(fornecedor);
        return converterParaDTO(fornecedor);
    }

    @Transactional
    public FornecedorResponseDTO atualizar(Integer id, FornecedorRequestDTO dto) {
        Fornecedor fornecedor = buscarFornecedorPorId(id);

        fornecedorRepository.findByCnpj(dto.getCnpj()).ifPresent(fornecedorExistente -> {
            if (!fornecedorExistente.getIdFornecedor().equals(id)) {
                throw new RecursoDuplicadoException("Fornecedor", "CNPJ", dto.getCnpj());
            }
        });

        fornecedorRepository.findByEmail(dto.getEmail()).ifPresent(fornecedorExistente -> {
            if (!fornecedorExistente.getIdFornecedor().equals(id)) {
                throw new RecursoDuplicadoException("Fornecedor", "email", dto.getEmail());
            }
        });

        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setEndereco(dto.getEndereco());
        fornecedor.setProdFornecidos(dto.getProdFornecidos());

        fornecedor = fornecedorRepository.save(fornecedor);
        return converterParaDTO(fornecedor);
    }

    @Transactional
    public void excluir(Integer id) {
        Fornecedor fornecedor = buscarFornecedorPorId(id);
        fornecedorRepository.delete(fornecedor);
    }

    private Fornecedor buscarFornecedorPorId(Integer id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor", "id", id));
    }

    private FornecedorResponseDTO converterParaDTO(Fornecedor fornecedor) {
        return FornecedorResponseDTO.builder()
                .idFornecedor(fornecedor.getIdFornecedor())
                .nome(fornecedor.getNome())
                .cnpj(fornecedor.getCnpj())
                .telefone(fornecedor.getTelefone())
                .email(fornecedor.getEmail())
                .endereco(fornecedor.getEndereco())
                .prodFornecidos(fornecedor.getProdFornecidos())
                .build();
    }
}