package tracker;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DiretorioArquivos {

    private final ConcurrentHashMap<String, Set<String>> arquivosAtivos =
            new ConcurrentHashMap<>();

    public void registrar(String nomeArquivo, String ip, int porta) {
        String peerAddress = ip + ":" + porta;

        arquivosAtivos
                .computeIfAbsent(nomeArquivo, k -> ConcurrentHashMap.newKeySet())
                .add(peerAddress);

        System.out.println("[DIRETÓRIO] Arquivo '" + nomeArquivo
                + "' registrado por " + peerAddress);
    }

    public String buscar(String nomeArquivo) {
        Set<String> peers = arquivosAtivos.get(nomeArquivo);

        if (peers != null && !peers.isEmpty()) {
            return peers.iterator().next();
        }

        return null;
    }
}
