package imoveis.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import imoveis.base.IImobiliaria;
import imoveis.base.IImovel;
import imoveis.excel.Excel;

public class RunnerFactory {

    private static final String PACKAGE = "imoveis.imobiliarias";
    private static final String[] CLASSES = { "Abelardo", "ACRC", "Alianca", "Conexao", "Habitacao", "ImoveisSc", "LFernando", "Orbi", "Portal" };
    private static final String[] PARAMETERS = { "apartamento", "casa" };

    public List<Runnable> getRunners() {
        List<Runnable> runners = new LinkedList<>();
        for (String name : CLASSES) {
            String className = String.format("%s.%s", PACKAGE, name);
            for (String parameter : PARAMETERS) {
                IImobiliaria object = createObject(className, parameter);
                runners.add(new RunnerFactory.Runner(object));
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

    public class Runner implements Runnable {

        private IImobiliaria imobiliaria;

        public Runner(IImobiliaria imobiliaria) {
            this.imobiliaria = imobiliaria;
        }

        @Override
        public void run() {
            List<IImovel> imoveis = imobiliaria.getImoveis();
            Excel.getInstance().addTodosImovel(imoveis);
        }

    }

}
