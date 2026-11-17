package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonaMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
	private static final int OPCION_VER_TODO = 1;
	private static final int OPCION_VER_UNO = 2;
	private static final int OPCION_CONTAR = 3;
	private static final int OPCION_CREAR = 4;
	private static final int OPCION_EDITAR = 5;
	private static final int OPCION_ELIMINAR = 6;
	private static final int OPCION_VER_TELEFONOS = 7;
	private static final int OPCION_VER_ESTUDIOS = 8;

	public void iniciarMenu(PersonaInputAdapterCli personaInputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuMotorPersistencia();
				int opcion = leerEntero(keyboard, "Ingrese una opción: ");
				switch (opcion) {
				case OPCION_REGRESAR_MODULOS:
					isValid = true;
					break;
				case PERSISTENCIA_MARIADB:
					personaInputAdapterCli.setPersonOutputPortInjection("MARIA");
					menuOpciones(personaInputAdapterCli, keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					personaInputAdapterCli.setPersonOutputPortInjection("MONGO");
					menuOpciones(personaInputAdapterCli, keyboard);
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(PersonaInputAdapterCli personaInputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuOpciones();
				int opcion = leerEntero(keyboard, "Ingrese una opción: ");
				switch (opcion) {
				case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
					isValid = true;
					break;
				case OPCION_VER_TODO:
					personaInputAdapterCli.historial();
					break;
				case OPCION_VER_UNO:
					personaInputAdapterCli.buscar(leerEntero(keyboard, "Cédula: "));
					break;
				case OPCION_CONTAR:
					personaInputAdapterCli.contar();
					break;
				case OPCION_CREAR:
					personaInputAdapterCli.crear(leerPersona(keyboard, leerEntero(keyboard, "Cédula: ")));
					break;
				case OPCION_EDITAR:
					int cc = leerEntero(keyboard, "Cédula de la persona a editar: ");
					personaInputAdapterCli.buscar(cc);
					System.out.println("Ingrese los nuevos datos:");
					personaInputAdapterCli.editar(leerPersona(keyboard, cc));
					break;
				case OPCION_ELIMINAR:
					personaInputAdapterCli.eliminar(leerEntero(keyboard, "Cédula: "));
					break;
				case OPCION_VER_TELEFONOS:
					personaInputAdapterCli.telefonos(leerEntero(keyboard, "Cédula: "));
					break;
				case OPCION_VER_ESTUDIOS:
					personaInputAdapterCli.estudios(leerEntero(keyboard, "Cédula: "));
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (NoExistException | AlreadyExistException | IllegalArgumentException e) {
				System.out.println("Error: " + e.getMessage());
			}
		} while (!isValid);
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println(OPCION_VER_TODO + " para ver todas las personas");
		System.out.println(OPCION_VER_UNO + " para buscar una persona");
		System.out.println(OPCION_CONTAR + " para contar las personas");
		System.out.println(OPCION_CREAR + " para crear una persona");
		System.out.println(OPCION_EDITAR + " para editar una persona");
		System.out.println(OPCION_ELIMINAR + " para eliminar una persona");
		System.out.println(OPCION_VER_TELEFONOS + " para ver los teléfonos de una persona");
		System.out.println(OPCION_VER_ESTUDIOS + " para ver los estudios de una persona");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("----------------------");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}

	private PersonaModelCli leerPersona(Scanner keyboard, Integer cc) {
		PersonaModelCli persona = new PersonaModelCli();
		persona.setCc(cc);
		persona.setNombre(leerTexto(keyboard, "Nombre: "));
		persona.setApellido(leerTexto(keyboard, "Apellido: "));
		persona.setGenero(leerTexto(keyboard, "Género (M/F): "));
		persona.setEdad(leerEnteroOpcional(keyboard, "Edad (Enter para omitir): "));
		return persona;
	}

	private int leerEntero(Scanner keyboard, String mensaje) {
		System.out.print(mensaje);
		String linea = keyboard.nextLine().trim();
		try {
			return Integer.parseInt(linea);
		} catch (NumberFormatException e) {
			log.warn("Solo se permiten números.");
			return leerEntero(keyboard, mensaje);
		}
	}

	private Integer leerEnteroOpcional(Scanner keyboard, String mensaje) {
		System.out.print(mensaje);
		String linea = keyboard.nextLine().trim();
		if (linea.isEmpty())
			return null;
		try {
			int valor = Integer.parseInt(linea);
			if (valor >= 0)
				return valor;
		} catch (NumberFormatException e) {
			// handled below
		}
		log.warn("Ingrese un número positivo o deje vacío.");
		return leerEnteroOpcional(keyboard, mensaje);
	}

	private String leerTexto(Scanner keyboard, String mensaje) {
		System.out.print(mensaje);
		String linea = keyboard.nextLine().trim();
		if (linea.isEmpty()) {
			log.warn("Este campo es obligatorio.");
			return leerTexto(keyboard, mensaje);
		}
		return linea;
	}
}
