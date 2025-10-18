package br.unip.ads.pim.meuhortifruti.service;


import br.unip.ads.pim.meuhortifruti.dto.PagamentoRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.PagamentoResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Pagamento;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;

    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarTodas(){
        return pagamentoRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorId(Integer id){
        Pagamento pagamento = buscarPagamentoPorId(id);
        return converterParaDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO criar(PagamentoRequestDTO dto){
        if(pagamentoRepository.existsByNome(dto.getNome())) {
            throw new RecursoDuplicadoException("Pagamento ", "nome ", dto.getNome());
        }

        Pagamento pagamento = Pagamento.builder()
                .nome(dto.getNome)
                .build();

        pagamento = pagamentoRepository.save(pagamento);
        return converterParaDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO atualizar(Integer id, PagamentoRequestDTO dto) {
        Pagamento pagamento = buscarPagamentoPorId(id);

        pagamentoRepository.findByNome(dto.getNome()).ifPresent(pagamentoExistente ->{
            if (!pagamentoExistente.getIdPagamento().equals(id)) {
                throw new RecursoDuplicadoException("Categoria", "nome", dto.getNome());
            }
        });

        pagamento.setNome(dto.getNome());
        pagamento = pagamentoRepository.save(pagamento);
        return converterParaDTO(pagamento);
    }

    @Transactional
    public void excluir(Integer id){
        Pagamento pagamento = buscarPagamentoPorId(id);
        pagamentoRepository.delete(pagamento);
    }

    private Pagamento buscarPagamentoPorId(Integer id){
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", "id", id));
    }

    private PagamentoResponseDTO converterParaDTO (Pagamento pagamento){
        return PagamentoResponseDTO.builder()
                .idPagamento(pagamento.getIdPagamento())
                .nome(pagamento.getNome())
                .build();
    }
}
