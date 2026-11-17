package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstudioMenu {

        private static final int OPCION_REGRESAR_MODULOS = 0;
        private static final int PERSISTENCIA_MARIADB = 1;
        private static final int PERSISTENCIA_MONGODB = 2;

        private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
        private static final int OPCION_VER_TODO = 1;
        private static final int OPCION_BUSCAR = 2;
        private static final int OPCION_CONTAR = 3;
        private static final int OPCION_CREAR = 4;
        private static final int OPCION_EDITAR = 5;
        private static final int OPCION_ELIMINAR = 6;
        private static final int OPCION_VER_PERSONA = 7;
        private static final int OPCION_VER_PROFESION = 8;

        public void iniciarMenu(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
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
                                        estudioInputAdapterCli.setStudyOutputPortInjection("MARIA");
                                        menuOpciones(estudioInputAdapterCli, keyboard);
                                        break;
                                case PERSISTENCIA_MONGODB:
                                        estudioInputAdapterCli.setStudyOutputPortInjection("MONGO");
                                        menuOpciones(estudioInputAdapterCli, keyboard);
                                        break;
                                default:
                                        log.warn("La opción elegida no es válida.");
                                }
                        } catch (InvalidOptionException e) {
                                log.warn(e.getMessage());
                        }
                } while (!isValid);
        }

        private void menuOpciones(EstudioInputAdapterCli adapter, Scanner keyboard) {
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
                                        adapter.historial();
                                        break;
                                case OPCION_BUSCAR:
                                        adapter.buscar(leerEntero(keyboard, "Cédula de la persona: "),
                                                        leerEntero(keyboard, "Id de la profesión: "));
                                        break;
                                case OPCION_CONTAR:
                                        adapter.contar();
                                        break;
                                case OPCION_CREAR:
                                        adapter.crear(leerEstudio(keyboard));
                                        break;
                                case OPCION_EDITAR:
                                        adapter.editar(leerEstudio(keyboard));
                                        break;
                                case OPCION_ELIMINAR:
                                        adapter.eliminar(leerEntero(keyboard, "Cédula de la persona: "),
                                                        leerEntero(keyboard, "Id de la profesión: "));
                                        break;
                                case OPCION_VER_PERSONA:
                                        adapter.persona(leerEntero(keyboard, "Cédula de la persona: "),
                                                        leerEntero(keyboard, "Id de la profesión: "));
                                        break;
                                case OPCION_VER_PROFESION:
                                        adapter.profesion(leerEntero(keyboard, "Cédula de la persona: "),
                                                        leerEntero(keyboard, "Id de la profesión: "));
                                        break;

                                default:
                                        log.warn("La opción elegida no es válida.");
                                }
                        } catch (Exception e) {
                                // NoExistException, AlreadyExistException o errores de la base de datos
                                // (por ejemplo, persona o profesión inexistente en MariaDB)
                                log.warn(e.getMessage());
                        }
                } while (!isValid);
        }

        // Para crear y editar: la llave (cédula + id de profesión) identifica el estudio y no cambia al editar
        private EstudioModelCli leerEstudio(Scanner keyboard) {
                Integer ccPer = leerEntero(keyboard, "Cédula de la persona: ");
                Integer idProf = leerEntero(keyboard, "Id de la profesión: ");
                System.out.print("Universidad: ");
                String univer = keyboard.nextLine().trim();
                LocalDate fecha = leerFecha(keyboard);
                return new EstudioModelCli(ccPer, null, idProf, null, univer, fecha);
        }

        private LocalDate leerFecha(Scanner keyboard) {
                System.out.print("Fecha de graduación (yyyy-MM-dd, vacío si no aplica): ");
                while (true) {
                        String linea = keyboard.nextLine().trim();
                        if (linea.isEmpty())
                                return null;
                        try {
                                return LocalDate.parse(linea);
                        } catch (DateTimeParseException e) {
                                log.warn("Formato de fecha inválido, use yyyy-MM-dd.");
                                System.out.print("Fecha de graduación (yyyy-MM-dd, vacío si no aplica): ");
                        }
                }
        }

        private Integer leerEntero(Scanner keyboard, String mensaje) {
                System.out.print(mensaje);
                while (true) {
                        String linea = keyboard.nextLine().trim();
                        if (linea.isEmpty())
                                continue; // descarta el salto de línea que dejó un nextInt() de otro menú
                        try {
                                return Integer.valueOf(linea);
                        } catch (NumberFormatException e) {
                                log.warn("Solo se permiten números.");
                                System.out.print(mensaje);
                        }
                }
        }

        private void mostrarMenuOpciones() {
                System.out.println("----------------------");
                System.out.println(OPCION_VER_TODO + " para ver todos los estudios");
                System.out.println(OPCION_BUSCAR + " para buscar un estudio");
                System.out.println(OPCION_CONTAR + " para contar los estudios");
                System.out.println(OPCION_CREAR + " para crear un estudio");
                System.out.println(OPCION_EDITAR + " para editar un estudio");
                System.out.println(OPCION_ELIMINAR + " para eliminar un estudio");
                System.out.println(OPCION_VER_PERSONA + " para ver la persona de un estudio");
                System.out.println(OPCION_VER_PROFESION + " para ver la profesión de un estudio");
                System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
        }

        private void mostrarMenuMotorPersistencia() {
                System.out.println("----------------------");
                System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
                System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
                System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
        }
}