package br.com.projetogestao.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerenciador de notificações para coordenar atualizações entre abas
 */
public class UsuarioNotificationManager {
    private static UsuarioNotificationManager instance;
    private final List<UsuarioListener> listeners = new ArrayList<>();
    
    private UsuarioNotificationManager() {}
    
    public static UsuarioNotificationManager getInstance() {
        if (instance == null) {
            instance = new UsuarioNotificationManager();
        }
        return instance;
    }
    
    public void addListener(UsuarioListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(UsuarioListener listener) {
        listeners.remove(listener);
    }
    
    public void notifyUsuarioCadastrado() {
        for (UsuarioListener listener : listeners) {
            listener.onUsuarioCadastrado();
        }
    }
}