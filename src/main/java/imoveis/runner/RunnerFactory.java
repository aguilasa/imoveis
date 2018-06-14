package imoveis.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import imoveis.base.IImobiliaria;

public class RunnerFactory {

    public static final String PACKAGE = "imoveis.imobiliarias";
    public static final List<String> CLASSES = Arrays.asList("Abelardo", "ACRC", "Alianca", "Conexao", "Habitacao", "ImoveisSc", "LFernando", "Orbi", "Portal", "Tropical", "DinamicaSul");
    public static final String[] PARAMETERS = { "apartamento", "casa" };

    public static List<Runnable> getRunners() {
        return getRunners(new ArrayList<>(CLASSES));
    }

    public static List<Runnable> getRunners(List<String> list) {
        Collections.shuffle(list);
        List<Runnable> runners = new LinkedList<>();
        for (String name : list) {
            String className = String.format("%s.%s", PACKAGE, name);
            for (String parameter : PARAMETERS) {
                IImobiliaria object = createObject(className, parameter);
                runners.add(new Runner(object));
            }
        }
        return runners;
    }

    private static IImobiliaria createObject(String className, String parameter) {
        Object object = null;
        try {
            Class<?> classDefinition = Class.forName(className);
            Constructor<?> constructor = classDefinition.getConstructor(String.class);
            object = constructor.newInstance(parameter);
        } catch (InstantiationException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (NoSuchMethodException e) {
            System.out.println(e);
        } catch (SecurityException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e);
        }
        return (IImobiliaria) object;
    }

}
