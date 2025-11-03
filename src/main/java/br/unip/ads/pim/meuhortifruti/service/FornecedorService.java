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
    public List<FornecedorResponseDTO> listarTodas() {
        return fornecedorRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Integer id){
        Fornecedor fornecedor = buscarFornecedorPorId(id);
        return converterParaDTO(fornecedor);

    }
    @Transactional
    public FornecedorResponseDTO criar(FornecedorRequestDTO dto){
        if(fornecedorRepository.existsByEmail(dto.getEmail())){
            throw new RecursoDuplicadoException("Fornecedor", "email", dto.getEmail());
        }
        Fornecedor fornecedor = Fornecedor.builder()
                .telefone(dto.getTelefone())
                .cnpj(dto.getCnpj())
                .produtosFornecidos(dto.getProdutosFornecidos())
                .nome(dto.getNome())
                .endereco(dto.getEndereco())
                .email(dto.getEmail())
                .build();
        fornecedor = fornecedorRepository.save(fornecedor);
        return converterParaDTO(fornecedor);
    }
    @Transactional
    public FornecedorResponseDTO atualizar (Integer id, FornecedorRequestDTO dto){
        Fornecedor fornecedor = buscarFornecedorPorId(id);

        fornecedorRepository.findByEmail(dto.getEmail()).ifPresent(fornecedorExistente ->{
            if (!fornecedorExistente.getIdFornecedor().equals(id)){
                throw new RecursoDuplicadoException("Fornecedor", "email", dto.getEmail());
            }
        });
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());
        fornecedor.setEndereco(dto.getEndereco());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setProdutosFornecidos(dto.getProdutosFornecidos());
        fornecedor = fornecedorRepository.save(fornecedor);
        return converterParaDTO(fornecedor);
    }
    @Transactional
    public void excluir(Integer id){
        Fornecedor fornecedor = buscarFornecedorPorId(id);
        fornecedorRepository.delete(fornecedor);
    }

    private Fornecedor buscarFornecedorPorId(Integer id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor", "id", id));
    }

    private FornecedorResponseDTO converterParaDTO(Fornecedor fornecedor) {
        return FornecedorResponseDTO.builder()
                .nome(fornecedor.getNome())
                .cnpj(fornecedor.getCnpj())
                .telefone(fornecedor.getTelefone())
                .idFornecedor(fornecedor.getIdFornecedor())
                .email(fornecedor.getEmail())
                .endereco(fornecedor.getEndereco())
                .produtosFornecidos(fornecedor.getProdutosFornecidos())
                .build();
    }
}
