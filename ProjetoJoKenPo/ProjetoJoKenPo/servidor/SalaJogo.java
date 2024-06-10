package servidor;

import java.util.List;

public class SalaJogo implements Runnable {
    private List<ControladorClienteEmJogo> clientes; // Lista de clientes na sala

    public SalaJogo(List<ControladorClienteEmJogo> clientes) {
        this.clientes = clientes;
    }

    @Override
    public void run() {
        iniciarPartida(); // Inicia a partida quando a thread é executada
    }

    public void iniciarPartida() {
        if (clientes.size() != 2) {
            System.out.println("A sala não tem dois jogadores."); // Depuração
            return;
        }

        ControladorClienteEmJogo cliente1 = clientes.get(0);
        ControladorClienteEmJogo cliente2 = clientes.get(1);

        System.out.println("Partida iniciada."); // Depuração, caso haja os dois clientes aparece essa mensagem 

        // Simula a escolha dos jogadores e envia o resultado de volta aos clientes
        cliente1.enviarResultado(cliente1.getEscolhaCliente(), cliente2.getEscolhaCliente());
        cliente2.enviarResultado(cliente2.getEscolhaCliente(), cliente1.getEscolhaCliente());

        System.out.println("Partida encerrada."); // Depuração

        clientes.clear(); // Limpa a lista de clientes após a partida
    }
}
