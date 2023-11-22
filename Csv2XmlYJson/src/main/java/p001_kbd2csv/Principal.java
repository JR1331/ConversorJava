package p001_kbd2csv;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import io.IO;

public class Principal {
	
	private static String FICHERO = "datos.csv";

	/**
	 * Programa principal
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(FICHERO, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(System.in);
		while (true) {
			Persona persona = leerDatos(sc);
			if (persona == null) {
				sc.close();
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
				return;
			}
			grabarEnCSV(raf, persona);
		}
	}

	/**
	 * Graba los datos de una persona en un fichero CSV
	 * 
	 * @param persona
	 */
	private static void grabarEnCSV(RandomAccessFile raf, Persona persona) {
		try {
			String registro = String.format("\"%s\",%d,\"%s\"\n",
					persona.getNombre(),
					persona.getEdad(),
					persona.getProfesion());
			raf.seek(raf.length());//MUY IMPORTATE a la hora de escribir en ficheros el situarte al final de fichero para seguir escribiendo
			raf.writeBytes(registro);//Despues de posicionarte al final del fichero escribe el registro
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Lee datos de una persona desde teclado
	 * 
	 * @return objeto Persona o null si no se introduce nada
	 */
	private static Persona leerDatos(Scanner sc) {
		System.out.println("Nombre ? ");
		String nombre = IO.readString();
		
		if (nombre.isBlank()) {
			sc.close();
			return null;
		}

		System.out.println("Edad ? ");
		int edad = IO.readInt();

		System.out.println("Profesión ? ");
		String profesion = IO.readString();

		return new Persona(nombre, edad, profesion);
	}

}
