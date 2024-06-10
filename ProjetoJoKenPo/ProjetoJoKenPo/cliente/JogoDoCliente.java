package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JogoDoCliente {
    public static void main(String[] args) {
        int vitorias = 0;
        int derrotas = 0;
        int empates = 0;

        try (BufferedReader inputUsuario = new BufferedReader(new InputStreamReader(System.in))) {
            // Solicita o IP do servidor
            System.out.println("Digite o IP do servidor:");
            String serverIp = inputUsuario.readLine();

            // Solicita a porta do servidor
            System.out.println("Digite a porta do servidor:");
            int serverPort = Integer.parseInt(inputUsuario.readLine());

            try (Socket socket = new Socket(serverIp, serverPort);
                 PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Solicita o modo de jogo ao usuário
                System.out.println("1. Jogador vs Jogador");
                System.out.println("2. Jogador vs CPU");

                String modoJogo = inputUsuario.readLine();
                saida.println(modoJogo); // Envia o modo de jogo escolhido ao servidor
                System.out.println("Modo de jogo escolhido: " + modoJogo); // Depuração

                while (true) {
                    // Solicita a escolha do jogador
                    System.out.println("Digite sua escolha: (1 = pedra, 2 = papel, 3 = tesoura)");
                    String escolha = inputUsuario.readLine();
                    saida.println(escolha); // Envia a escolha do jogador ao servidor
                    System.out.println("Escolha enviada: " + escolha); // Depuração

                    // Recebe e mostra o resultado do servidor
                    String resultado = entrada.readLine();
                    System.out.println(resultado); // Depuração

                    // Atualiza as estatísticas
                    if (resultado.contains("Vitória")) {
                        vitorias++;
                    } else if (resultado.contains("Derrota")) {
                        derrotas++;
                    } else if (resultado.contains("Empate")) {
                        empates++;
                    }

                    // Mostra as estatísticas atualizadas
                    System.out.println("Vitórias: " + vitorias + ", Derrotas: " + derrotas + ", Empates: " + empates);
                }
            } catch (IOException e) {
                System.err.println("Erro no cliente: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler entrada do usuário: " + e.getMessage());
        }
    }
}
