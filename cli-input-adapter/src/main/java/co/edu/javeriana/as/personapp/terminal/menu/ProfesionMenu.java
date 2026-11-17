package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfesionMenu {

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
        private static final int OPCION_VER_ESTUDIOS = 7;

        public void iniciarMenu(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
                                        profesionInputAdapterCli.setProfessionOutputPortInjection("MARIA");
                                        menuOpciones(profesionInputAdapterCli, keyboard);
                                        break;
                                case PERSISTENCIA_MONGODB:
                                        profesionInputAdapterCli.setProfessionOutputPortInjection("MONGO");
                                        menuOpciones(profesionInputAdapterCli, keyboard);
                                        break;
                                default:
                                        log.warn("La opción elegida no es válida.");
                                }
                        } catch (InvalidOptionException e) {
                                log.warn(e.getMessage());
                        }
                } while (!isValid);
        }

        private void menuOpciones(ProfesionInputAdapterCli adapter, Scanner keyboard) {
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
                                        adapter.buscar(leerEntero(keyboard, "Id de la profesión: "));
                                        break;
                                case OPCION_CONTAR:
                                        adapter.contar();
                                        break;
                                case OPCION_CREAR:
                                        adapter.crear(leerProfesion(keyboard));
                                        break;
                                case OPCION_EDITAR:
                                        adapter.editar(leerProfesion(keyboard));
                                        break;
                                case OPCION_ELIMINAR:
                                        adapter.eliminar(leerEntero(keyboard, "Id de la profesión: "));
                                        break;
                                case OPCION_VER_ESTUDIOS:
                                        adapter.estudios(leerEntero(keyboard, "Id de la profesión: "));
                                        break;
                                default:
                                        log.warn("La opción elegida no es válida.");
                                }
                        } catch (Exception e) {
                                // NoExistException, AlreadyExistException o errores de la base de datos
                                log.warn(e.getMessage());
                        }
                } while (!isValid);
        }

        // Para crear y editar: el id identifica la profesión y no cambia al editar
        private ProfesionModelCli leerProfesion(Scanner keyboard) {
                Integer id = leerEntero(keyboard, "Id de la profesión: ");
                String nombre = leerTextoObligatorio(keyboard, "Nombre: ");
                System.out.print("Descripción (opcional): ");
                String descripcion = keyboard.nextLine().trim();
                return new ProfesionModelCli(id, nombre, descripcion);
        }

        private String leerTextoObligatorio(Scanner keyboard, String mensaje) {
                System.out.print(mensaje);
                while (true) {
                        String linea = keyboard.nextLine().trim();
                        if (!linea.isEmpty())
                                return linea;
                        log.warn("Este campo es obligatorio.");
                        System.out.print(mensaje);
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
                System.out.println(OPCION_VER_TODO + " para ver todas las profesiones");
                System.out.println(OPCION_BUSCAR + " para buscar una profesión");
                System.out.println(OPCION_CONTAR + " para contar las profesiones");
                System.out.println(OPCION_CREAR + " para crear una profesión");
                System.out.println(OPCION_EDITAR + " para editar una profesión");
                System.out.println(OPCION_ELIMINAR + " para eliminar una profesión");
                System.out.println(OPCION_VER_ESTUDIOS + " para ver los estudios de una profesión");
                System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
        }

        private void mostrarMenuMotorPersistencia() {
                System.out.println("----------------------");
                System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
                System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
                System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
        }
}
