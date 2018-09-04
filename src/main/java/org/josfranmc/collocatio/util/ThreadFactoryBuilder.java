package org.josfranmc.collocatio.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Permite configurar y construir objetos de tipo ThreadFactory que sirven para personalizar los hilos a utilizarse. Permite configurar
 * el nombre que se asigna a cada hilo, si su comportamiento es de tipo daemon o no lo es y la prioridad que se le va a asignar al hilo. 
 * La configuración se aplica al crear un ExecutorService 
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ThreadFactoryBuilder {

    private String nameThread = null;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;

    
    /**
     * Establece el nombre para los hilos.
     * @param nameThread nombre a asignar
     * @return referencia al mismo objeto
     */
    public ThreadFactoryBuilder setNameThread(String nameThread) {
        if (nameThread == null) {
            throw new NullPointerException();
        }
        this.nameThread = nameThread;
        return this;
    }

    /**
     * Establece si el hilo se comportará como un daemon o no.
     * @param daemon <i>true</i> si se quiere que se comporte como un daemon, <i>false</i> en caso contrario
     * @return referencia al mismo objeto
     */
    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * Establece la prioridad que debe tener un hilo.
     * @param priority valor de la prioridad 
     * @return referencia al mismo objeto
     */
    public ThreadFactoryBuilder setPriority(int priority) {
    	if (priority < Thread.MIN_PRIORITY) {
    		throw new IllegalArgumentException(String.format("La prioridad mínima debe ser mayor que %s", Thread.MAX_PRIORITY));
    	}
        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format("La prioridad máxima debe ser menor que %s", Thread.MAX_PRIORITY));
        }
        this.priority = priority;
        return this;
    }

    /**
     * Realiza la construcción de un objeto ThreadFactory, configurándolo según las propiedades que se hayan establecido previamente.
     * @return un objeto ThreadFactory
     */
    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        final String nameThread = builder.nameThread;
        final Boolean daemon = builder.daemon;
        final Integer priority = builder.priority;
        final AtomicLong count = new AtomicLong(0);

        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                if (nameThread != null) {
                    thread.setName(nameThread + "-" + count.getAndIncrement());
                }
                if (daemon != null) {
                    thread.setDaemon(daemon);
                }
                if (priority != null) {
                    thread.setPriority(priority);
                }
                return thread;
            }
        };
    }
}