package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    public static final int PORT = 9999; // Porta do servidor
    public static List<ControladorClienteEmJogo> clients = new CopyOnWriteArrayList<>(); // Lista de clientes conectados

    public static void main(String[] args) {
        System.out.println("Servidor de Jogo iniciado...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {// bloco de loop infinito para vvvv
                                                       // Aceitar novas conexões de clientes
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão: " + clientSocket);

                //criação de uma thread
                ControladorClienteEmJogo clientThread = new ControladorClienteEmJogo(clientSocket);
                clients.add(clientThread);//adiciona a thread ao objeto da lista
                new Thread(clientThread).start();

                // Inicia uma nova partida quando há dois clientes conectados
                if (clients.size() % 2 == 0) {
                    iniciarPartida(clients.subList(clients.size() - 2, clients.size()));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    // Inicia uma partida entre dois clientes
    public static void iniciarPartida(List<ControladorClienteEmJogo> clientes) {
        if (clientes.size() != 2) {
            System.out.println("A sala não tem dois jogadores."); // Depuração
            return;
        }

        ControladorClienteEmJogo cliente1 = clientes.get(0);//retorna o primeiro cliente da lista
        ControladorClienteEmJogo cliente2 = clientes.get(1);//retorna o segundo

        System.out.println("Partida iniciada."); // Depuração

        while (true) {
            synchronized (cliente1) {         //faz um bloqueio nos objetos para fazer a sincronização das escolhas dos clietnes que estara no laço a seguir
                synchronized (cliente2) {
                    //  verifica se as escolhas dos clientes (cliente1 e cliente2) ainda não foram feitas (assumindo que 0 indica que a escolha ainda não foi feita).
                    while (cliente1.getEscolhaCliente() == 0 || cliente2.getEscolhaCliente() == 0) {
                        try {
                            if (cliente1.getEscolhaCliente() == 0) {
                                cliente1.wait();
                            }
                            if (cliente2.getEscolhaCliente() == 0) {
                                cliente2.wait();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println("Thread de partida interrompida."); // Depuração
                            break;
                        }
                    }

                    // Exibe as escolhas dos clientes (para depuração)
                    System.out.println("Cliente 1 escolheu: " + cliente1.getEscolhaCliente()); // Depuração
                    System.out.println("Cliente 2 escolheu: " + cliente2.getEscolhaCliente()); // Depuração

                    // Envia os resultados para ambos os clientes
                    cliente1.enviarResultado(cliente1.getEscolhaCliente(), cliente2.getEscolhaCliente());
                    cliente2.enviarResultado(cliente2.getEscolhaCliente(), cliente1.getEscolhaCliente());

                    // Reseta as escolhas para a próxima rodada
                    cliente1.setEscolhaCliente(0);
                    cliente2.setEscolhaCliente(0);
                }
            }
           
        }
        
    }
    
}
