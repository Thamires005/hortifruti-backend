package br.unip.ads.pim.meuhortifruti.service;

import br.unip.ads.pim.meuhortifruti.dto.ClienteRequestDTO;
import br.unip.ads.pim.meuhortifruti.dto.ClienteResponseDTO;
import br.unip.ads.pim.meuhortifruti.entity.Cliente;
import br.unip.ads.pim.meuhortifruti.exception.RecursoDuplicadoException;
import br.unip.ads.pim.meuhortifruti.exception.RecursoNaoEncontradoException;
import br.unip.ads.pim.meuhortifruti.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Integer id) {
        Cliente cliente = buscarClientePorId(id);
        return converterParaDTO(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", "CPF", cpf));
        return converterParaDTO(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", "email", email));
        return converterParaDTO(cliente);
    }

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            throw new RecursoDuplicadoException("Cliente", "CPF", dto.getCpf());
        }

        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("Cliente", "email", dto.getEmail());
        }

        Cliente cliente = Cliente.builder()
            .nome(dto.getNome())
            .cpf(dto.getCpf())
            .endereco(dto.getEndereco())
            .telefone(dto.getTelefone())
            .email(dto.getEmail())
            .dtNascimento(dto.getDtNascimento())
            .senha(dto.getSenha())
            .tipoUsuario("CLIENTE")
            .build();

        cliente = clienteRepository.save(cliente);
        return converterParaDTO(cliente);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Integer id, ClienteRequestDTO dto) {
        Cliente cliente = buscarClientePorId(id);

        clienteRepository.findByCpf(dto.getCpf()).ifPresent(clienteExistente -> {
            if (!clienteExistente.getIdUsuario().equals(id)) {
                throw new RecursoDuplicadoException("Cliente", "CPF", dto.getCpf());
            }
        });

        clienteRepository.findByEmail(dto.getEmail()).ifPresent(clienteExistente -> {
            if (!clienteExistente.getIdUsuario().equals(id)) {
                throw new RecursoDuplicadoException("Cliente", "email", dto.getEmail());
            }
        });

        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setEndereco(dto.getEndereco());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setDtNascimento(dto.getDtNascimento());

        cliente = clienteRepository.save(cliente);
        return converterParaDTO(cliente);
    }

    @Transactional
    public void excluir(Integer id) {
        Cliente cliente = buscarClientePorId(id);
        clienteRepository.delete(cliente);
    }

    private Cliente buscarClientePorId(Integer id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", "id", id));
    }

    private ClienteResponseDTO converterParaDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
            .idUsuario(cliente.getIdUsuario())
            .nome(cliente.getNome())
            .cpf(cliente.getCpf())
            .endereco(cliente.getEndereco())
            .telefone(cliente.getTelefone())
            .email(cliente.getEmail())
            .dtNascimento(cliente.getDtNascimento())
            .build();
    }
}
