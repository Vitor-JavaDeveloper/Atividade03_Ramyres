package peer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import protocol.MensagemP2P;

public class PeerSeeder extends Thread {
    private final int minhaPorta;

    public PeerSeeder(int portaP2P) {
        this.minhaPorta = portaP2P;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(minhaPorta)) {
            System.out.println("[SEEDER] Aguardando conexões P2P na porta " + minhaPorta);
            
            while (true) {
                Socket peerConectado = serverSocket.accept();
                try (
                    ObjectOutputStream out = new ObjectOutputStream(peerConectado.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(peerConectado.getInputStream())
                ) {
                    MensagemP2P req = (MensagemP2P) in.readObject();
                    
                    if (req.getComando() == MensagemP2P.Comando.DOWNLOAD) {
                        System.out.println("[SEEDER] Enviando arquivo '" + req.getNomeArquivo() + "' para outro Peer.");
                        MensagemP2P resp = new MensagemP2P(MensagemP2P.Comando.RESPOSTA, req.getNomeArquivo());
                        resp.setConteudoArquivo("<< DADOS BINÁRIOS DO ARQUIVO " + req.getNomeArquivo() + " >>");
                        out.writeObject(resp);
                    }
                } catch (Exception e) {
                    System.err.println("[SEEDER] Erro ao enviar arquivo: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
