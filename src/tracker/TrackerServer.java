package tracker;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import protocol.MensagemP2P;

public class TrackerServer {
    private static final int PORTA_TRACKER = 8000;
    private static final DiretorioArquivos diretorio = new DiretorioArquivos();

    public static void main(String[] args) {
        System.out.println("=== TRACKER P2P INICIADO NA PORTA " + PORTA_TRACKER + " ===");
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORTA_TRACKER)) {
            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(() -> processarRequisicao(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processarRequisicao(Socket socket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            MensagemP2P req = (MensagemP2P) in.readObject();
            MensagemP2P resp = new MensagemP2P(MensagemP2P.Comando.RESPOSTA, req.getNomeArquivo());

            if (req.getComando() == MensagemP2P.Comando.REGISTRAR) {
                diretorio.registrar(req.getNomeArquivo(), req.getIpPeer(), req.getPortaPeer());
                resp.setConteudoArquivo("Registrado com sucesso no Tracker.");
            } 
            else if (req.getComando() == MensagemP2P.Comando.BUSCAR) {
                String peerDono = diretorio.buscar(req.getNomeArquivo());
                if (peerDono != null) {
                    String[] partes = peerDono.split(":");
                    resp.setIpPeer(partes[0]);
                    resp.setPortaPeer(Integer.parseInt(partes[1]));
                } else {
                    resp = new MensagemP2P(MensagemP2P.Comando.ERRO, req.getNomeArquivo());
                    resp.setConteudoArquivo("Arquivo não encontrado na rede P2P.");
                }
            }
            out.writeObject(resp);
        } catch (Exception e) {
            System.err.println("Erro processando Peer: " + e.getMessage());
        }
    }
}