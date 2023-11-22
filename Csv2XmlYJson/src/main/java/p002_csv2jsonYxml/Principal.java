package p002_csv2jsonYxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Principal {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Se debe pasar como parámetro la ruta a un fichero CSV");
			return;
		}
		String ruta = args[0];//Esto es para a la hora de runearlo el pasarle el parametro de la ruta directamete

		// Si el fichero CSV no es legible me voy, es la primera comprobación que se tiene que hacer
		File f = new File(ruta);
		if (!f.canRead()) {
			System.err.println("El fichero " + ruta + " no es accesible");
			return;
		}

		// Si no puedo crear el fichero JSON me voy, lo que compruebo es que se ha creado correctamente el filewriter .json SIEMPRE ACABAR CERRANDO
		FileWriter fJson = abrirFicheroJson(ruta);
		if (fJson == null) {
			System.err.println("El fichero " + ruta + ".json no se puede generar");
			return;
		}
		
		// Si no puedo crear el fichero XML me voy, compruebo que el filewriter con extension .xml se ha creado correctamente SIEMPRE ACABAR CERRANDO
		FileWriter fXml = abrirFicheroXML(ruta);
		if (fXml == null) {
			System.err.println("El fichero " + ruta + ".xml no se puede generar");
			cerrarFichero(fJson);
			return;
		}

		Scanner sc = null;
		try {
			sc = new Scanner(f);
			
			// Leer cabecera con los nombres de los campos
			String[] cabecera = leerCabecera(sc);
			if (cabecera == null) {
				System.err.println("El fichero no contiene información");
				cerrarFichero(fXml);
				cerrarFichero(fJson);				
				return;
			}
				//SI NO HAY ERRORES EN LA CREACION DE LOS FILEWRITERS ME ABRE LOS ENCABEZADOS
			fXml.write("<registros>\n");
			fJson.write("[\n");

			// Leemos la información
			while (sc.hasNextLine()) {
				String[] campos = sc.nextLine().split(",");
				fXml.write(toXml(cabecera, campos));
				fJson.write(toJson(cabecera, campos, !sc.hasNextLine()));
			}
			fXml.write("</registros>");
			fJson.write("]");

			cerrarFichero(fXml);
			cerrarFichero(fJson);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sc.close();
	}

	/**
	 * Cerrar un FileWriter
	 * 
	 * @param fw
	 */
	private static void cerrarFichero(FileWriter fw) {
		try {
			fw.close();
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * Abrir un fichero XML de escritura
	 * 
	 * @param ruta
	 * @return el descriptor o null si no se puede abrir
	 */
	private static FileWriter abrirFicheroXML(String ruta) {
		FileWriter fXml = null;
		try {
			fXml = new FileWriter(ruta + ".xml");
		} catch (IOException e) {
			return null;
		}
		return fXml;
	}

	/**
	 * Abrir un fichero JSON de escritura
	 * Aqui crea un fileWriter al que a la ruta inicial le añado .json y 
	 * @param ruta
	 * @return el descriptor o null si no se puede abrir
	 */
	private static FileWriter abrirFicheroJson(String ruta) {
		FileWriter fJson = null;
		try {
			fJson = new FileWriter(ruta + ".json");
		} catch (IOException e) {
			return null;
		}
		return fJson;
	}

	/**
	 * Leer el primer registro que contiene la cabecera con los nombres de los campos
	 * 
	 * @param sc
	 * @return array con los nombres de los campos
	 */
	private static String[] leerCabecera(Scanner sc) {

		if (!sc.hasNextLine()) {
			return null;
		}

		// Leemos la cabecera donde están los nombres de los campos, separa los objetos del csv que estan por comas con el split
		String[] cabecera = sc.nextLine().split(",");
		for (int i = 0; i < cabecera.length; i++) {
			cabecera[i] = cabecera[i].replace("\"", "").trim();//Crea un array de strings que va reyenando con los datos de la fila
		}
		return cabecera;
	}

	/**
	 * Convertir un registro CSV en un registro JSON
	 * 
	 * @param cabecera
	 * @param campos
	 * @param ultimo indica si es el último registro a procesar
	 * @return la cadena JSON
	 */
	private static String toJson(String[] cabecera, String[] campos, boolean ultimo) {
		String json = "\t{\n";
		for (int i = 0; i < cabecera.length; i++) {
			String campo = campos[i].replace("\"", "").trim();
			json += String.format("\t\t\"%s\": \"%s\"%s\n", 
					cabecera[i], 
					campo, 
					i == cabecera.length - 1 ? "" : ",");
		}
		return json + String.format("\t}%s\n", ultimo ? "" : ",");
	}

	/**
	 * Convertir un registro CSV en un registro XML
	 * 
	 * @param cabecera
	 * @param campos
	 * @return la cadena XML
	 */
	private static String toXml(String[] cabecera, String[] campos) {
		StringBuffer xml = new StringBuffer("\t<registro>\n");
		for (int i = 0; i < cabecera.length; i++) {
			String campo = campos[i].replace("\"", "").trim();
			xml.append(String.format("\t\t<%s>%s</%s>\n", 
					cabecera[i], campo, cabecera[i]));
//			xml.append("\t\"<");
//			xml.append(cabecera[i]);
//			xml.append(">");
//			xml.append(campo);
//			xml.append("</");
//			xml.append(cabecera[i]);
//			xml.append(">\n");
		}
		xml.append("\t</registro>\n");
		return xml.toString();
	}

}
