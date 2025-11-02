package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.*;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.dto.ItemCompraResponseDTO;
import br.unip.ads.pim.meuhortifruti.dto.ItemCompraRequestDTO;
import br.unip.ads.pim.meuhortifruti.repository.ItemCompraRepository;
import br.unip.ads.pim.meuhortifruti.entity.ItemCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemCompraService {

    private final ItemCompraRepository itemCompraRepository;

    @Transactional(readOnly = true)
    public ItemCompraResponseDTO buscarPorId(Integer id) {
        ItemCompra itemCompra = buscarItemCompraPorId(id);
        return converterParaDTO(itemCompra);
    }

    @Transactional
    public ItemCompraResponseDTO criar(ItemCompraRequestDTO dto) {
        if (itemCompraRepository.existsById(dto.getProduto().getIdProduto())) {
            throw new RecursoDuplicadoException("Item compra", "id", dto.getProduto().getIdProduto());
        }

        ItemCompra itemCompra = ItemCompra.builder()
                .produto(dto.getProduto())
                .preco(dto.getPreco())
                .build();

        itemCompra = itemCompraRepository.save(itemCompra);
        return converterParaDTO(itemCompra);
    }

    @Transactional
    public ItemCompraResponseDTO atualizar(Integer id, ItemCompraRequestDTO dto) {
        ItemCompra itemCompra = buscarItemCompraPorId(id);

        itemCompraRepository.findById(dto.getProduto().getIdProduto()).ifPresent(itemCompraExistente -> {
            if (!itemCompraExistente.getIdItemCompra().equals(id)) {
                throw new RecursoDuplicadoException("Item Compra", "id", dto.getProduto().getIdProduto());
            }
        });

        itemCompra.setPreco(dto.getPreco());
        itemCompra.setQuantidade(dto.getQuantidade());
        itemCompra.setProduto(dto.getProduto());
        itemCompra = itemCompraRepository.save(itemCompra);
        return converterParaDTO(itemCompra);
    }

    @Transactional
    public void excluir(Integer id) {
        ItemCompra itemCompra = buscarItemCompraPorId(id);
        itemCompraRepository.delete(itemCompra);
    }

    private ItemCompra buscarItemCompraPorId(Integer id) {
        return itemCompraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item Compra", "id", id));
    }

    private ItemCompraResponseDTO converterParaDTO(ItemCompra itemCompra) {
        return ItemCompraResponseDTO.builder()
                .idItemCompra(itemCompra.getIdItemCompra())
                .preco(itemCompra.getPreco())
                .quantidade(itemCompra.getQuantidade())
                .build();
    }
}
