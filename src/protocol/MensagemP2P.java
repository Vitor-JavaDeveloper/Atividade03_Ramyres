package protocol;
import java.io.Serializable;

public class MensagemP2P implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Comando {REGISTRAR, BUSCAR, DOWNLOAD, RESPOSTA, ERRO}

    private Comando comando;
    private String nomeArquivo;
    private String ipPeer;
    private int portaPeer;
    private String conteudoArquivo;

    public MensagemP2P (Comando comando, String nomeArquivo){
        this.comando = comando;
        this.nomeArquivo = nomeArquivo;
    }
    
    public Comando getComando() { return comando; }
    public String getNomeArquivo() { return nomeArquivo; }
    public String getIpPeer(){ return ipPeer;}
    public void setIpPeer (String ipPeer) { this.ipPeer = ipPeer;}
    public int getPortaPeer (){return portaPeer;}
    public void setPortaPeer (int portaPeer){this.portaPeer = portaPeer;}
    public String getConteudoArquivo() { return conteudoArquivo; }  
    public void setConteudoArquivo (String conteudoArquivo){this.conteudoArquivo = conteudoArquivo;}


}
