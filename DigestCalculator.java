// -------------------------
// Jam Ajna Soares - 2211689 
// Olavo Lucas     - 1811181
// -------------------------

import java.util.*; 
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
 
import java.security.*;
import javax.crypto.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import javax.xml.xpath.*;
import java.io.*;

public class DigestCalculator 
{
    public enum Estados
    {
        TOBECHECKED,
        OK,
        NOTOK,
        NOTFOUND,
        COLISION
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
	    else if (!arquivoCal.isDirectory())
        {
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
	    
        try
        {	
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document listXML = builder.parse(arquivoDi);
            listXML.getDocumentElement().normalize();
                
        } 
        catch (ParserConfigurationException parserProblem)
        {
                
            System.err.println("error na configuração do parser de XML");
            System.exit(1);
                
        } 
        catch(Exception SAXProblem)
        {
                
            System.err.println("error na leitura do arquivo XML");
            System.exit(1);
                
        }
        //verificar se arquivo DI é um arquivo XML no formato esperado pelo programa
		
	    /* cria lista de arquivos a serem calculados e dados como digest, tipo de digest
	    e estado */
	    List<Object[]> listaArquivos = new ArrayList<Object[]>();

	    for (File file : arquivoCal.listFiles())
        {
		    String arquivoNomei = file.getName();
		    String digestTipoi = tipoDigest;
		    String digesti = digestString(digestCalculate(digestTipoi, file));
		    Estados estado = Estados.TOBECHECKED;
		    Object[] linha = { (Object) arquivoNomei, (Object) digestTipoi, (Object) digesti, (Object) estado };
		    listaArquivos.add(linha);
	    }

	    //listaArquivos = digestDetectColision(listaArquivos);
	    //listaArquivos = digestCompare(listaArquivos, listXML);
	    //printList(listaArquivos);
    }

	private static String digestString(byte[] digest)
	{
		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < digest.length; i++) 
		{
			String hex = Integer.toHexString(0x0100 + (digest[i] & 0x00FF)).substring(1);
			buf.append((hex.length() < 2 ? "0" : "") + hex);
		}

		return buf.toString();
	}

    //TODO implementar digestCalculate, recebe o arquivo e o tipo de digest, retorna o digest calculado e uma array de byte, procure o metodo update adequado
    protected static byte[] digestCalculate(String tipoDigest, File conteudo)
    {
        //  retorna o digest calculado e uma array de byte, procure o metodo update
        //  adequado: Update(Byte[], Int32, Int32)
        int bufferSize = 1024;
        byte[] result = {};

        try 
        {
            MessageDigest calculadora = MessageDigest.getInstance(tipoDigest);
            byte[] bytebuffer = new byte[bufferSize];
            FileInputStream leitor = new FileInputStream(conteudo);
            int check = leitor.read(bytebuffer);
        
            while(check != -1)
            {	
                calculadora.update(bytebuffer, 0, check);
                check = leitor.read(bytebuffer);
            }
        
            leitor.close();
            result = calculadora.digest(); 
        
        }
        catch(IOException e)
        {
            System.err.println("Erro na leitura do arquivo durante o calculo do digest");
            System.exit(1);
        }
        catch (NoSuchAlgorithmException e)
        {        
            System.err.println("Error: " + tipoDigest + " não é suportado por essa aplicação");
            System.exit(1);
        }

        return result;
    }

    //TODO implementar disgestDetecColision, use algo como dicionario de python com os disgest como chave e os valores como listas e para cada no digest calculado, adicionar a lista adequada, no final, procure por listas com tamanho maior que 1 e marque os elementos com COLISION
    protected static List<Object[]> digestDetectColision(List<Object[]> list)
        {
            //  use algo como dicionario de python com
            //  os digests como chave e os valores como listas e para cada no digest
            //  calculado, adicionar a lista adequada, no final, procure por listas com
            //  tamanho maior que 1 e marque os elementos com COLISION
            HashMap <String, List<Integer>> mapeamento = new HashMap<String, List<Integer>>();


            for (int i = 0; i< list.size(); i++)
            {
                String digest = (String) list.get(i)[2];
                
                if(!mapeamento.containsKey(digest))
                {
                    mapeamento.put(digest,new ArrayList<Integer>());
                }

                mapeamento.get(digest).add(i);
            }

            for (List<Integer> i : mapeamento.values())
            {
                if (i.size() > 1)
                {
        	        for (int j:i)
                    {
        	            list.get(j)[3] = Estados.COLISION;
         	        }
	            }
            }

            return list;
        }

    //TODO implementar digestCompare, recebe a lista de digest calculado e compara com os digests salvos no arquivo XML, atualiza o campo de status de cada item com os valores adequados e chame o updateXML caso um ou mais elementos deram como NOTFOUND
    protected static List<Object[]> digestCompare(List<Object[]> list, Document XMLroot)
	{
        //crie um hashset com pares (tipo_digest, digest) e veja se algum item de list está incluso e marque no encontradoSet
        //organize queries no XML
        //primeiro nome do arquivo, segundo o tipo de digest, terceiro o digest
        //no caso de falhas e encontros, registre no encontradoQuerie
        //Compare os booleanos para os casos:
        //OK: true em ambos
        //NOTFOUND: false em ambos
        //COISION: true no encontradoSet e false no encontradoQuery
        //NOTOK: false no encontradoSet e true no encontradoQuery

        Element root = XMLroot.getDocumentElement();
        HashMap<String,HashSet<String>> XMLdigests = new HashMap<String,HashSet<String>>();
        List<Boolean> encontradoSet = new ArrayList<Boolean>();
        List<Boolean> encontradoQuerie = new ArrayList<Boolean>();

	    if(!root.hasChildNodes())
	    {
            for (Object[] line : list)
            {
                Estados check = (Estados) line[3];
                        
                if(check == Estados.COLISION){continue;}
                    
                line[3] = Estados.NOTFOUND;
                //XMLUpdate ou acumula para fazer update com todos os novos digest
            }
            
		    return list;
	    }

	    for(Node childNode = root.getFirstChild(); childNode != null; childNode = childNode.getNextSibling())
        {
		    NodeList Arqinfo = childNode.getChildNodes();

		    for(int i = 0; i < Arqinfo.getLength(); i++)
            {
			    if(Arqinfo.item(i).getNodeName().equals("FILE_NAME"))
                {
                    continue;
                }

			    NodeList DigestInfo = Arqinfo.item(i).getChildNodes();
			    String DigestType = DigestInfo.item(0).getNodeValue();
			    String DigestHex = DigestInfo.item(1).getNodeValue();

			    if(!XMLdigests.containsKey(DigestType))
                {
                    XMLdigests.put(DigestType, new HashSet<String>());
                }

			    XMLdigests.get(DigestType).add(DigestHex);
		    }
	    }

	    for (Object[] line : list)
        {
		    Estados check = (Estados) line[3];

		    if(check == Estados.COLISION){continue;}

		    String NomeArquivo = (String) line[0];
		    String digestTipo = (String) line[1];
		    String digest = (String) line[2];
			
		    try
            {	
			    XPath xPath = XPathFactory.newInstance().newXPath();
			    encontradoSet.add(XMLdigests.get(digestTipo).contains(digest));
			    String expressaoArquivo = "/FILE_ENTRY[FILE_NAME = '"+NomeArquivo+"']/DIGEST_ENTRY[DIGEST_TYPE = '"+digestTipo+"']/DIGEST_HEX";
			    XPathExpression expr = xPath.compile(expressaoArquivo);
			    NodeList ProcuraDigest = (NodeList) expr.evaluate(XMLroot, XPathConstants.NODESET);
			    int tam = ProcuraDigest.getLength(); 

			    if (tam == 0)
                {
                    encontradoQuerie.add(false);continue;
                }
			    else if (tam > 1)
                {
				    List<Integer> indices = new ArrayList<Integer>();

				    for (int i = 0; i < tam; i++)
                    {
					    if(ProcuraDigest.item(i).getNodeValue().equals(digest)){indices.add(i);}
				    }

				    int numPositivos = indices.size();
				    if(numPositivos == 1)
                    {
                        encontradoQuerie.add(true);
                    }
				    else
                    {
                        encontradoQuerie.add(false);
                    }
                    
				    continue;
			    }

			    encontradoQuerie.add(digest.equals(ProcuraDigest.item(0).getNodeValue()));

			} catch(XPathException x)
            {
				
				System.err.println("Erro no Xpath durante a procura dos arquivos");
				System.exit(1);
				
			}

			for (int i = 0; i < list.size(); i++)
            {
				boolean resultadoQuerie = encontradoQuerie.get(i);
				boolean resultadoSet = encontradoSet.get(i);

				if(resultadoQuerie && resultadoSet){
					list.get(i)[3] = Estados.OK;
				}
				else if(resultadoQuerie && !resultadoSet){
					list.get(i)[3] = Estados.NOTOK;
				}
				else if(!resultadoQuerie && resultadoSet){
					list.get(i)[3] = Estados.COLISION;
				}
				else if(!resultadoQuerie && !resultadoSet){
					list.get(i)[3] = Estados.NOTFOUND;
					//XML Update ou acumula para um unico update
				}
			}
		}

		    return list;
	}
        
    //TODO implementar updateXML, atualiza o arquivo XML recebido com as informações, prioridade: Digest entry> File entry> Catalog
    //protected static void updateXML(String[] XMLlist, String newInfo, String digestType){}
        
    //TODO implementar printList, imprime a lista recebida no formato especificado no enunciado:
    //nome_arquivo <espaço> tipo_digest <espaço> digest_Hex_calculado <espaço> Status
    //IMPORTANTE: presta atenção como é salvo o hexadecimal comparado a string
    protected static void printList(List<Object[]> list)
    {
        for (Object[] line: list)
        {
            String nomeArquivo = (String) line[0];
            String tipoDigest = (String) line[1];
            String digest = (String) line[2];
            Estados estado = (Estados) line[3];
        
            System.out.println(nomeArquivo + " " + tipoDigest + " " + digest + " " + estado);
        }
    }
}
