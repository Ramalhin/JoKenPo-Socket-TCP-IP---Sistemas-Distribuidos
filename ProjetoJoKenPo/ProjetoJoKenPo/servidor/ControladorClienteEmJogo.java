package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControladorClienteEmJogo implements Runnable {
    private Socket clientSocket; // Socket do cliente
    private PrintWriter out; // Saída para o cliente
    private BufferedReader entrada; // Entrada do cliente
    private int escolhaCliente; // Escolha do cliente atual
    private boolean contraCPU; // Indica se o cliente está jogando contra a CPU

    public ControladorClienteEmJogo(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // Inicializa a entrada e saída do cliente
            entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //criado para ler a entrada de dados enviada pelo cliente
            out = new PrintWriter(clientSocket.getOutputStream(), true);// criado para enviar os dados de volta ao cliente

            // Recebe a escolha de modo de jogo do cliente (1 para PvP, 2 para PvCPU)
            String modoJogo = entrada.readLine();
            System.out.println("Modo de jogo recebido: " + modoJogo); // Depuração
            contraCPU = modoJogo.equals("2");

            while (true) {
                // Recebe a escolha do cliente (1 = pedra, 2 = papel, 3 = tesoura)
                String escolhaStr = entrada.readLine();
                if (escolhaStr == null) {
                    System.out.println("Conexão encerrada pelo cliente."); // Depuração
                    break; // Sai do loop se a conexão for encerrada
                }
                System.out.println("Escolha do cliente recebida: " + escolhaStr); // Depuração
                escolhaCliente = Integer.parseInt(escolhaStr);

                if (contraCPU) {
                    // Se contra CPU, gera uma escolha aleatória para a CPU
                    int escolhaCPU = (int) (Math.random() * 3) + 1;
                    System.out.println("Escolha da CPU: " + escolhaCPU); // Depuração
                    enviarResultado(escolhaCliente, escolhaCPU); // Envia o resultado ao cliente
                } else {
                    // Se contra outro jogador, notifica o oponente
                    ControladorClienteEmJogo oponente = getOponente();//pega a escolha do oponente
                    if (oponente != null) {//verifica se ha oponente para jogar
                        synchronized (oponente) {//garante que apenas um dos clietes sera o objeto oponente
                            oponente.notify();//notifica qualquer thread que esteja aguardando neste objeto, que uma mudança ocorreu e que elas podem continuar a execução.
                        }

                        // cliente aguarda escolha do oponente
                        synchronized (this) {
                            wait();
                        }

                        // Envia o resultado ao cliente com base na escolha do oponente
                        enviarResultado(escolhaCliente, oponente.getEscolhaCliente());
                    } else {
                        System.out.println("Oponente não encontrado."); // Depuração
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Erro ao lidar com cliente: " + e.getMessage());
        }
    }

    // Envia o resultado da partida de acordo com as escolhas dos jogadores
    public void enviarResultado(int escolhaCliente, int escolhaOponente) {
        String resultado;
        if (escolhaCliente == escolhaOponente) {
            resultado = "Empate";
        } else if ((escolhaCliente == 1 && escolhaOponente == 3) ||
                   (escolhaCliente == 2 && escolhaOponente == 1) ||
                   (escolhaCliente == 3 && escolhaOponente == 2)) {
            resultado = "Vitória";
        } else {
            resultado = "Derrota";
        }

        System.out.println("Resultado enviado: " + resultado); // Depuração
        // Envia o resultado de volta ao cliente
        out.println("Resultado: " + resultado);
    }

    // Obtém o oponente do cliente atual na mesma sala
    private ControladorClienteEmJogo getOponente() {
        for (ControladorClienteEmJogo cliente : GameServer.clients) {
            if (cliente != this && !cliente.contraCPU) {
                return cliente;
            }
        }
        return null;
    }

    // Define a escolha do cliente atual
    public void setEscolhaCliente(int escolhaCliente) {
        this.escolhaCliente = escolhaCliente;
    }

    // Obtém a escolha do cliente atual
    public int getEscolhaCliente() {
        return escolhaCliente;
    }
}
