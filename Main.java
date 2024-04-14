import java.util.*; 
 
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
 
import java.security.*;
import javax.crypto.*;
 
public class DigestCalculator 
{
    public enum estados
    {
        TOBECHEKED(0),
        OK(1),
        NOTOK(2),
        NOTFOUND(3),
        COLISION(4)
    }
 
    public static void main(String[] args) throws IOException
    {
        String[] argumentos = args;
        HashSet<String> tiposSuportadosDigest = new HashSet<String>(Arrays.asList("MD5","SHA1","SHA256","SHA512"));
    
        if (argumentos.length != 3)
        {
            System.err.println("Usage: java DigestCalculator DigestType FileAddress DigestListFileAddress");
            System.exit(1);
        }
    
        String tipoDigest = argumentos[0].toUpperCase();
        String arquivosCalcular = argumentos[1];
        String arqDigest = argumentos[2];
        
        if (!tiposSuportadosDigest.contains(tipoDigest))
        {
            System.err.println("Error: " + argumentos[0]+ " não é suportado");
            System.exit(1);
        }
        
        File arquivoCal = new File(arquivosCalcular);

        if (!arquivoCal.exists())
        {
            System.err.println("Error: " + argumentos[1]+ " não foi encontrado");
            System.exit(1);
        }
        
        File arquivoDi = new File(arqDigest);

        if (!arquivoDi.exists())
        {
            System.err.println("Error: " + argumentos[2]+ " não foi encontrado");
            System.exit(1);
        } else if(!arquivoDi.isFile())
        {
            System.err.println("Error: " + argumentos[2]+ " não é um arquivo");
            System.exit(1);
        } else if()
        {

        }
    
    }
    
        //TODO implementar digestCalculate, recebe o arquivo e o tipo de digest, retorna o digest calculado e uma array de byte, procure o metodo update adequado
        //private static byte[] digestCalculate(){}
        
        //TODO implementar disgestDetecColision, use algo como dicionario de python com os disgest como chave e os valores como listas e para cada no digest calculado, adicionar a lista adequada, no final, procure por listas com tamanho maior que 1 e marque os elementos com COLISION
        //private static void digestDetectColision(){}
        
        //TODO implementar digestCompare, recebe a lista de digest calculado e compara com os digests salvos no arquivo XML, atualiza o campo de status de cada item com os valores adequados e chame o updateXML caso um ou mais elementos deram como NOTFOUND
        //private static void digestCompare(){}
        
        //TODO implementar updateXML, atualiza o arquivo XML recebido com as informações, prioridade: Digest entry> File entry> Catalog
        //private static void updateXML(){}
        
        //TODO implementar printList, imprime a lista recebida no formato especificado no enunciado:
        //nome_arquivo <espaço> tipo_digest <espaço> digest_Hex_calculado <espaço> Status
        //IMPORTANTE: presta atenção como é salvo o hexadecimal comparado a string
        //public static void printList(){}
}