package peer;

import protocol.MensagemP2P;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PeerNode {
    private static final String TRACKER_IP = "127.0.0.1";
    private static final int TRACKER_PORTA = 8000;

    public static void main(String[] args) throws InterruptedException {
        // Inicializa o seeder local e registra no tracker
        PeerSeeder seederAlice = new PeerSeeder(9001);
        seederAlice.start();
        registrarNoTracker("livro.pdf", 9001);

        Thread.sleep(1000);

        // Bob busca o arquivo no tracker e realiza a conexao direta
        System.out.println("\n[BOB] Quero baixar o livro.pdf. Perguntando ao Tracker...");
        MensagemP2P infoTracker = buscarNoTracker("livro.pdf");

        if (infoTracker.getComando() != MensagemP2P.Comando.ERRO) {
            System.out.println("[BOB] Tracker respondeu! O arquivo está no IP " + infoTracker.getIpPeer() + " Porta " + infoTracker.getPortaPeer());
            
            System.out.println("[BOB] Conectando diretamente ao Peer da porta " + infoTracker.getPortaPeer() + "...");
            baixarDoPeer(infoTracker.getIpPeer(), infoTracker.getPortaPeer(), "livro.pdf");
        } else {
            System.out.println("[BOB] Erro: " + infoTracker.getConteudoArquivo());
        }
    }

    private static void registrarNoTracker(String arquivo, int minhaPorta) {
        try (Socket socket = new Socket(TRACKER_IP, TRACKER_PORTA);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
             
            MensagemP2P req = new MensagemP2P(MensagemP2P.Comando.REGISTRAR, arquivo);
            req.setIpPeer("127.0.0.1");
            req.setPortaPeer(minhaPorta);
            out.writeObject(req);
            
            MensagemP2P resp = (MensagemP2P) in.readObject();
            System.out.println("[NODE] Tracker disse: " + resp.getConteudoArquivo());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static MensagemP2P buscarNoTracker(String arquivo) {
        try (Socket socket = new Socket(TRACKER_IP, TRACKER_PORTA);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
             
            MensagemP2P req = new MensagemP2P(MensagemP2P.Comando.BUSCAR, arquivo);
            out.writeObject(req);
            return (MensagemP2P) in.readObject();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private static void baixarDoPeer(String ip, int porta, String arquivo) {
        try (Socket socket = new Socket(ip, porta);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
             
            MensagemP2P req = new MensagemP2P(MensagemP2P.Comando.DOWNLOAD, arquivo);
            out.writeObject(req);
            
            MensagemP2P resp = (MensagemP2P) in.readObject();
            System.out.println("[BOB] DOWNLOAD CONCLUÍDO! Conteúdo recebido: " + resp.getConteudoArquivo());
        } catch (Exception e) { e.printStackTrace(); }
    }
}
