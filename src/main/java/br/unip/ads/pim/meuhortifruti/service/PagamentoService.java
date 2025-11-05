package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.PagamentoResponseDTO;
import br.unip.ads.pim.meuhortifruti.dto.PagamentoRequestDTO;
import br.unip.ads.pim.meuhortifruti.entity.Pagamento;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorId(Integer id){
        Pagamento pagamento = buscarPagamentoPorId(id);
        return converterParaDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO criar(PagamentoRequestDTO dto){
        if(pagamentoRepository.existsByIdCompra(dto.getIdCompra())){
            throw new RecursoDuplicadoException("Pagamento", "id", dto.getIdCompra());
        }
        Pagamento pagamento = Pagamento.builder()
                .statusPagamento(dto.getStatusPagamento())
                .formaPagamento(dto.getFormaPagamento())
                .valor(dto.getValor())
                .build();
        pagamento = pagamentoRepository.save(pagamento);
        return converterParaDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO atualizar(Integer id, PagamentoRequestDTO dto){
        Pagamento pagamento = buscarPagamentoPorId(id);

        pagamentoRepository.findByIdCompra(dto.getIdCompra()).ifPresent(pagamentoExistente -> { //busca pelo ID da compra
            if(!pagamentoExistente.getIdPagamento().equals(id)){
                throw new RecursoDuplicadoException("Pagamento", "idCompra", id);
            }
        });
        pagamento.setFormaPagamento(dto.getFormaPagamento());
        pagamento.setStatusPagamento(dto.getStatusPagamento());
        pagamento.setValor(dto.getValor());
        pagamento = pagamentoRepository.save(pagamento);
        return converterParaDTO(pagamento);
    }

    @Transactional
    public void excluir(Integer id){
        Pagamento pagamento = buscarPagamentoPorId(id);
        pagamentoRepository.delete(pagamento);
    }
    private Pagamento buscarPagamentoPorId(Integer id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento", "id", id));
    }

    private PagamentoResponseDTO converterParaDTO(Pagamento pagamento) {
        return PagamentoResponseDTO.builder()
                .idPagamento(pagamento.getIdPagamento())
                .statusPagamento(pagamento.getStatusPagamento())
                .valor(pagamento.getValor())
                .formaPagamento(pagamento.getFormaPagamento())
                .build();
    }
}
