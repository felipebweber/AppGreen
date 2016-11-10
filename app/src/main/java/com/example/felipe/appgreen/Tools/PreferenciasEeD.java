package com.example.felipe.appgreen.Tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by felipe on 10/11/16.
 */

public class PreferenciasEeD {
    private Context contexto;
    private SharedPreferences preferencesEeD;
    private final String NOME_ARQUIVO_ED = "PreferenciasED";
    private final int E_Preferences = 3;
    private final int D_Preferences = 7;
    private final int MODE = 0;
    private final SharedPreferences.Editor editor;

    public PreferenciasEeD(Context contexto) {
        this.contexto = contexto;
        this.preferencesEeD = contexto.getSharedPreferences(NOME_ARQUIVO_ED, MODE);
        this.editor = preferencesEeD.edit();
    }

    public void salvarDados_E(int e){
        //editor.putFloat(String.valueOf(e_Preferences), e);
        editor.putInt("E_Preferences", e);
        editor.commit();
    }

    public int get_E(){
        return preferencesEeD.getInt("E_Preferences", E_Preferences);
    }

    public void salvarDados_D(int d){
        editor.putInt("D_Preferences", d);
        editor.commit();
    }

    public int get_D(){
        return preferencesEeD.getInt("D_Preferences", D_Preferences);
    }
}
