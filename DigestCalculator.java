import java.util.*; 
 
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
 
import java.security.*;
import javax.crypto.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class DigestCalculator 
{
    public enum estados
    {
        TOBECHECKED(0),
        OK(1),
        NOTOK(2),
        NOTFOUND(3),
        COLISION(4)
    }
 
    public static void main(String[] args) throws IOException
    {
        String[] argumentos = args;
        HashSet<String> tiposSuportadosDigest = new HashSet<String>(Arrays.asList("MD5","SHA1","SHA256","SHA512","SHA-1", "SHA-256", "SHA-512"));
    
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

        if (tipoDigest.equals("SHA1") || tipoDigest.equals("SHA256") || tipoDigest.equals("SHA512")) 
        {
	    tipoDigest = tipoDigest.substring(0, 3) + "-" + tipoDigest.substring(3);
	}

        File arquivoCal = new File(arquivosCalcular);

        if (!arquivoCal.exists())
        {
            System.err.println("Error: " + argumentos[1]+ " não foi encontrado");
            System.exit(1);
        }
	else if (!arquivoCal.isDirectory()) {
	    System.err.println("Error: " + argumentos[2] + " não é uma pasta");
	    System.exit(1);
        }
        
        File arquivoDi = new File(arqDigest);
        if (!arquivoDi.exists())
        {
            System.err.println("Error: " + argumentos[2]+ " não foi encontrado");
            System.exit(1);
        } 
        else if(!arquivoDi.isFile())
        {
            System.err.println("Error: " + argumentos[2]+ " não é um arquivo");
            System.exit(1);
        }
	    
	try{	
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document listXML = builder.parse(arquivoDi);
	    listXML.getDocumentElement().normalize();
			
	} catch (ParserConfigurationException parserProblem){
			
	    System.err.println("error na configuração do parser de XML");
	    System.exit(1);
			
	} catch(Exception SAXProblem){
			
	    System.err.println("error na leitura do arquivo XML");
	    System.exit(1);
			
       }
       //verificarse arquivoDI é um arquivo XML no formato experado pelo programa
		
	// cria lista de arquivos a serem calculados e dados como digest, tipo de digest
	// e estado
	List<Object> listaArquivos = new ArrayList<Object>();
	for (File file : arquivoCal.listFiles()) {
		String arquivoNomei = file.getName();
		String digestTipoi = tipoDigest;
		byte[] digesti = digestCalculate(digestTipoi, file);
		Estados estado = Estados.TOBECHECKED;
		Object[] linha = { (Object) arquivoNomei, (Object) digestTipoi, (Object) digesti, (Object) estado };
		listaArquivos.add(linha);
	}
	//digestDetectColision(listaArquivos);
	//digestCompare(listaArquivos, listXML);
	//printList(listaArquivos);
    }

        //TODO implementar digestCalculate, recebe o arquivo e o tipo de digest, retorna o digest calculado e uma array de byte, procure o metodo update adequado
        protected static byte[] digestCalculate(String tipoDigest, File conteudo) {
        //  retorna o digest calculado e uma array de byte, procure o metodo update
        //  adequado: Update(Byte[], Int32, Int32)
            byte[] result = {};
            try {
		MessageDigest calculadora = MessageDigest.getInstance(tipoDigest);
		byte[] bytebuffer = new byte[bufferSize];
		FileInputStream leitor = new FileInputStream(conteudo);
		int check = leitor.read(bytebuffer);
			
		while(check != -1){	
			calculadora.update(bytebuffer, 0, check);
			check = leitor.read(bytebuffer);
		}
			
		leitor.close();
		result = calculadora.digest(); 
			
	    }catch(IOException e){
			
		System.err.println("Erro na leitura do arquivo durante o calculo do digest");
		System.exit(1);
			
	    }catch (NoSuchAlgorithmException e) {
			
		System.err.println("Error: " + tipoDigest + " não é suportado por essa aplicação");
		System.exit(1);
			
	    }
            return result;
        }

        //TODO implementar disgestDetecColision, use algo como dicionario de python com os disgest como chave e os valores como listas e para cada no digest calculado, adicionar a lista adequada, no final, procure por listas com tamanho maior que 1 e marque os elementos com COLISION
        protected static void digestDetectColision(List<Object[]> list) {
        //  use algo como dicionario de python com
        //  os digests como chave e os valores como listas e para cada no digest
        //  calculado, adicionar a lista adequada, no final, procure por listas com
        //  tamanho maior que 1 e marque os elementos com COLISION
          Hashmap <byte[], List<int>>mapeamento
          for (int i = 0; i< list.length;i++){
            byte[] digest = (byte[]) list[i][2];
            if(!mapeamento.contain(digest)){
              mapeamento.put(digest,new ArrayList<int>());
            }
            mapeamento.put(digest,mapeamento.get().add(i);
          }
          for (List<int> i : mapeamento.values()) {
            if (i.size() > 1){
        	for (int j:i){
        	  list[j][3] = Estados.COLISION;
         	}
	    }
          }
          return list
        }

        //TODO implementar digestCompare, recebe a lista de digest calculado e compara com os digests salvos no arquivo XML, atualiza o campo de status de cada item com os valores adequados e chame o updateXML caso um ou mais elementos deram como NOTFOUND
        //protected static void digestCompare(List<Object> list, String[] XMLlist){}
        
        //TODO implementar updateXML, atualiza o arquivo XML recebido com as informações, prioridade: Digest entry> File entry> Catalog
        //protected static void updateXML(String[] XMLlist, String newInfo, String digestType){}
        
        //TODO implementar printList, imprime a lista recebida no formato especificado no enunciado:
        //nome_arquivo <espaço> tipo_digest <espaço> digest_Hex_calculado <espaço> Status
        //IMPORTANTE: presta atenção como é salvo o hexadecimal comparado a string
        protected static void printList(List<Object[]> list){
          for (Object[] line: list){
            String nomeArquivo = (String) line[0];
            String tipoDigest = (String) line[1];
            byte[] digest = (byte[]) line[2];
            Estados estado = (Estados) line[3];
	    StringBuffer buf = new StringBuffer();
			
	    for(int i = 0; i < digest.length; i++) {
		String hex = Integer.toHexString(0x0100 + (digest[i] & 0x00FF)).substring(1);
		buf.append((hex.length() < 2 ? "0" : "") + hex);
	    }
            System.out.println(nomeArquivo + " " + tipoDigest + " " + buff + " " + estado);
          }
        }
}
